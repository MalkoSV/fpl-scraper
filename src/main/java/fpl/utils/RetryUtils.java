package fpl.utils;

import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class RetryUtils {

    private static final Logger logger = Logger.getLogger(RetryUtils.class.getName());
    private static final Random RANDOM = new Random();

    private RetryUtils() {}

    public static <T> T retry(
            Supplier<T> action,
            int attempts,
            long baseDelayMs,
            long maxDelayMs
    ) {
        for (int i = 0; i < attempts; i++) {
            try {
                return action.get();
            } catch (Exception e) {
                Throwable cause = (e.getCause() != null) ? e.getCause() : e;

                if (!(cause instanceof IOException) || i == attempts - 1) {
                    throw new RuntimeException("Retry failed after " + (i + 1) + " attempts", e);
                }

                long delay = calculateDelay(i, baseDelayMs, maxDelayMs);
                logger.warning(String.format(
                        "⚠️ Attempt %d/%d failed (%s). Retrying in %.2f sec...",
                        i + 1, attempts, cause.getClass().getSimpleName(), delay / 1000.0
                ));

                sleep(delay);
            }
        }

        throw new RuntimeException("Unreachable code");
    }

    public static <T> T retry(Supplier<T> action) {
        return retry(action, 10, 500, 10_000);
    }

    private static long calculateDelay(
            int attempt,
            long baseDelay,
            long maxDelay
    ) {
        long delay = (long) (baseDelay * Math.pow(2, attempt));
        long jitter = (long) ((delay * 0.4) * (RANDOM.nextDouble() - 0.5));
        delay += jitter;

        return Math.min(delay, maxDelay);
    }


    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
