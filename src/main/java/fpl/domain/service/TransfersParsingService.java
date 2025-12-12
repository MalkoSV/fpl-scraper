package fpl.domain.service;

import fpl.api.dto.PlayerDto;
import fpl.api.dto.TransferDto;
import fpl.domain.mapper.TransferMapper;
import fpl.domain.model.Team;
import fpl.utils.RetryUtils;
import fpl.utils.ThreadsUtils;
import fpl.domain.transfers.Transfer;
import fpl.logging.ProgressBar;
import fpl.parser.TransfersParser;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TransfersParsingService {

    private static final Logger logger = Logger.getLogger(TransfersParsingService.class.getName());

    private TransfersParsingService() {}

    public static List<Transfer> collectTransfers(
            Map<Integer, PlayerDto> playersById,
            List<URI> uris,
            List<Team> teams,
            int event
    ) {

        int totalUris = (uris.size());
        ProgressBar progressBar = new ProgressBar(totalUris);

        Map<Integer, Team> teamsByEntry = teams.stream()
                .collect(Collectors.toMap(Team::entryId, team -> team));

        int threadCount = ThreadsUtils.getThreadsNumber();

        logger.info("🚀 Starting to fetch transfers (using %s threads)...".formatted(threadCount));
        long startTime = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<CompletableFuture<List<Transfer>>> tasks = uris.stream()
                .map(uri -> CompletableFuture.supplyAsync(
                        () -> processTransfersWithRetry(uri, playersById, teamsByEntry, event, progressBar),
                        executorService
                ))
                .toList();

        List<Transfer> transferList = tasks.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
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

        logger.info("✅ Transfer fetching completed (in %d sec)".formatted((System.currentTimeMillis() - startTime) / 1000));

        return transferList;
    }

    private static List<Transfer> processTransfersWithRetry(
            URI uri,
            Map<Integer, PlayerDto> playersById,
            Map<Integer, Team> teamsByEntry,
            int event,
            ProgressBar progressBar
    ) {
        try {
            List<TransferDto> transfers = RetryUtils.retry(
                    () -> {
                        try {
                            return TransfersParser.parse(uri, event);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            progressBar.step();

            return transfers.stream()
                    .map(dto -> TransferMapper.toDomain(dto, playersById, teamsByEntry))
                    .toList();

        } catch (Exception e) {
            logger.warning("⚠️ Failed to fetch transfers for " + uri + ": " + e.getMessage());

            return List.of();
        }
    }
}
