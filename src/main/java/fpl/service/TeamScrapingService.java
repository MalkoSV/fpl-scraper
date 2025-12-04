package fpl.service;

import fpl.api.model.dto.BootstrapResponse;
import fpl.api.model.dto.EntryResponse;
import fpl.api.model.dto.Pick;
import fpl.api.model.dto.PlayerDto;
import fpl.api.model.PositionType;
import fpl.api.parser.BootstrapParser;
import fpl.api.parser.EntryParser;
import fpl.utils.BoolUtils;
import fpl.api.model.Player;
import fpl.api.model.Team;

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.partition;

public class TeamScrapingService {

    private static final Logger logger = Logger.getLogger(TeamScrapingService.class.getName());

    private static final String WILDCARD = "wildcard";
    private static final String TRIPLE_CAPTAIN = "3xc";
    private static final String BENCH_BOOST = "bboost";
    private static final String FREE_HIT = "freehit";

    private TeamScrapingService() {
    }

    public static List<Team> collectStats(List<URI> uris) throws Exception {

        AtomicInteger counter = new AtomicInteger(0);
        int totalUri = uris.size();

        BootstrapResponse bootstrapResponse = BootstrapParser.parseBootstrap();

        List<PlayerDto> players = BootstrapParser.getPlayers(bootstrapResponse);
        Map<Integer, PlayerDto> playersById = players.stream()
                .collect(Collectors.toMap(PlayerDto::id, Function.identity()));

        int threadCount = Math.min(5, Runtime.getRuntime().availableProcessors());
        logger.info("🚀 Running in multi-threaded mode using " + threadCount + " threads...");

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<List<URI>> partitions = partition(uris, (int) Math.ceil((double) uris.size() / threadCount));

        List<CompletableFuture<Team>> tasks = uris.stream()
                .map(uri -> CompletableFuture.supplyAsync(
                        () -> processTeam(uri, playersById, counter, totalUri),
                        executorService
                ))
                .toList();

        List<Team> allTeamList = tasks.stream()
                .map(CompletableFuture::join)
                .toList();

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return allTeamList;
    }

    private static Team processTeam(
            URI uri,
            Map<Integer, PlayerDto> playersById,
            AtomicInteger doneCounter,
            int totalUri
    ) {
        try {
            long startTime = System.currentTimeMillis();

            EntryResponse entryResponse = EntryParser.parseEntry(uri);
            List<Pick> picks = EntryParser.getPicks(entryResponse);
            String activeChip = EntryParser.getActiveChip(entryResponse);
            int transfers = EntryParser.getEventTransfers(entryResponse);
            int transfersCost = EntryParser.getEventTransfersCost(entryResponse);

            boolean hasBenchBoost = BENCH_BOOST.equalsIgnoreCase(activeChip);
            boolean hasFreeHit = FREE_HIT.equalsIgnoreCase(activeChip);
            boolean hasTripleCaptain = TRIPLE_CAPTAIN.equalsIgnoreCase(activeChip);
            boolean hasWildcard = WILDCARD.equalsIgnoreCase(activeChip);

            Map<PositionType, List<Player>> startSquad = new EnumMap<>(PositionType.class);
            for (PositionType type : PositionType.values()) {
                startSquad.put(type, new ArrayList<>());
            }
            List<Player> bench = new ArrayList<>();

            for (Pick p : picks) {
                PlayerDto player = playersById.get(p.element());
                Player currentPlayer = new Player(
                        player.webName(),
                        1,
                        BoolUtils.asInt(p.multiplier() > 0),
                        BoolUtils.asInt(p.multiplier() >= 2),
                        BoolUtils.asInt(p.multiplier() == 3),
                        BoolUtils.asInt(p.isViceCaptain()),
                        player.eventPoints(),
                        player.chanceSafe()
                );

                if (p.multiplier() == 0) {
                    bench.add(currentPlayer);
                    continue;
                }

                PositionType positionType = PositionType.fromCode(p.elementType());
                startSquad.get(positionType).add(currentPlayer);
            }

            int done = doneCounter.incrementAndGet();
            logger.info("✅ %d players, [%d/%d] (in %d sec) %s%n".formatted(
                    picks.size(),
                    done,
                    totalUri,
                    (System.currentTimeMillis() - startTime) / 1000, uri)
            );

            return new Team(
                    BoolUtils.asInt(hasTripleCaptain),
                    BoolUtils.asInt(hasWildcard),
                    BoolUtils.asInt(hasBenchBoost),
                    BoolUtils.asInt(hasFreeHit),
                    transfers,
                    transfersCost,
                    startSquad.get(PositionType.GK),
                    startSquad.get(PositionType.DEF),
                    startSquad.get(PositionType.MID),
                    startSquad.get(PositionType.FWD),
                    bench
            );

        } catch (Exception e) {
            logger.warning("⚠️ Error on " + uri + ": " + e.getMessage());
            return null;
        }
    }

}
