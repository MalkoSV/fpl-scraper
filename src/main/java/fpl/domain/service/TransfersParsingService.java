package fpl.domain.service;

import fpl.api.dto.PlayerDto;
import fpl.api.dto.TransferDto;
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

public class TransfersParsingService {

    private static final Logger logger = Logger.getLogger(TransfersParsingService.class.getName());

    private TransfersParsingService() {
    }

    public static List<Transfer> collectTransfers(Map<Integer, PlayerDto> playersById, List<URI> uris, int event) {

        ProgressBar progressBar = new ProgressBar(uris.size());

        int threadCount = Math.min(16, Runtime.getRuntime().availableProcessors() * 2);
        logger.info("🚀 Gameweek transfers fetching in multi-threaded mode using " + threadCount + " threads...");

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<CompletableFuture<List<Transfer>>> tasks = uris.stream()
                .map(uri -> CompletableFuture.supplyAsync(
                        () -> processTransfers(uri, playersById, progressBar, event),
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

        return transferList;
    }

    private static List<Transfer> processTransfers(
            URI uri,
            Map<Integer, PlayerDto> playersById,
            ProgressBar progressBar,
            int event
    ) {
        try {
            List<TransferDto> transfers = TransfersParser.parse(uri, event);

            progressBar.step();

            return transfers.stream()
                    .map(dto -> dtoToModel(dto, playersById))
                    .toList();

        } catch (Exception e) {
            logger.warning("⚠️ Error on " + uri + ": " + e.getMessage());

            return List.of();
        }
    }

    public static Transfer dtoToModel(TransferDto dto, Map<Integer, PlayerDto> playersById) {
        PlayerDto in = playersById.get(dto.elementIn());
        PlayerDto out = playersById.get(dto.elementOut());

        return new Transfer(
                in != null ? in.webName() : "UNKNOWN",
                out != null ? out.webName() : "UNKNOWN"
        );
    }
}
