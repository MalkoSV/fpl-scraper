package fpl.domain.service;

import fpl.api.dto.EntryResponse;
import fpl.api.dto.Pick;
import fpl.api.dto.PlayerDto;
import fpl.domain.model.PositionType;
import fpl.logging.ProgressBar;
import fpl.parser.EntryParser;
import fpl.domain.utils.BoolUtils;
import fpl.domain.model.Player;
import fpl.domain.model.Team;

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TeamParsingService {

    private static final Logger logger = Logger.getLogger(TeamParsingService.class.getName());

    private static final String WILDCARD = "wildcard";
    private static final String TRIPLE_CAPTAIN = "3xc";
    private static final String BENCH_BOOST = "bboost";
    private static final String FREE_HIT = "freehit";

    private TeamParsingService() {
    }

    public static List<Team> collectStats(Map<Integer, PlayerDto> playersById, List<URI> uris) {

        ProgressBar progressBar = new ProgressBar(uris.size());

        int threadCount = Math.min(16, Runtime.getRuntime().availableProcessors() * 2);
        logger.info("🚀 Running picks fetching in multi-threaded mode using " + threadCount + " threads...");

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<CompletableFuture<Team>> tasks = uris.stream()
                .map(uri -> CompletableFuture.supplyAsync(
                        () -> processTeam(uri, playersById, progressBar),
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
            ProgressBar progressBar
    ) {
        try {
            EntryResponse entryResponse = EntryParser.parse(uri);
            List<Pick> picks = EntryParser.getPicks(entryResponse);
            String activeChip = EntryParser.getActiveChip(entryResponse);
            int points = EntryParser.getPoints(entryResponse);
            int bank = EntryParser.getBank(entryResponse);
            int value = EntryParser.getValue(entryResponse);
            int transfers = EntryParser.getEventTransfers(entryResponse);
            int transfersCost = EntryParser.getEventTransfersCost(entryResponse);
            int pointsOnBench = EntryParser.getPointsOnBench(entryResponse);

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
            progressBar.step();

            return new Team(
                    points,
                    pointsOnBench,
                    value,
                    bank,
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
