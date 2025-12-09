package fpl.domain.service;

import fpl.api.dto.PlayerDto;
import fpl.api.dto.TransferDto;
import fpl.domain.transfers.Transfer;
import fpl.parser.TransfersParser;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TransfersParsingService {

    private static final Logger logger = Logger.getLogger(TransfersParsingService.class.getName());

    private TransfersParsingService() {
    }

    public static List<Transfer> collectTransfers(Map<Integer, PlayerDto> playersById, List<URI> uris, int event) {

        AtomicInteger counter = new AtomicInteger(0);
        int totalUri = uris.size();

        int threadCount = Math.min(5, Runtime.getRuntime().availableProcessors());
        logger.info("🚀 Gameweek transfers fetching in multi-threaded mode using " + threadCount + " threads...");

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<CompletableFuture<List<Transfer>>> tasks = uris.stream()
                .map(uri -> CompletableFuture.supplyAsync(
                        () -> processTransfers(uri, playersById, counter, totalUri, event),
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
            AtomicInteger doneCounter,
            int totalUri,
            int event
    ) {
        try {
            List<TransferDto> transfers = TransfersParser.parse(uri, event);

            List<Transfer> result = transfers.stream()
                    .map(dto -> dtoToModel(dto, playersById))
                    .toList();

            int done = doneCounter.incrementAndGet();
            if (done % 100 == 1) {
                System.out.printf("%n✅ Done [%d/%d] teams. Processing next.", done - 1, totalUri);
            }
            System.out.print(".");
            if (done == totalUri) {
                System.out.println();
            }
            return result;
        } catch (Exception e) {
            logger.warning("⚠️ Error on " + uri + ": " + e.getMessage());

            return null;
        }
    }

    public static Transfer dtoToModel(TransferDto dto, Map<Integer, PlayerDto> playersById) {
        return new Transfer(
                playersById.get(dto.elementIn()).webName(),
                playersById.get(dto.elementOut()).webName()
        );
    }

}
