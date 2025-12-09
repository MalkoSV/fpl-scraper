package fpl.logging;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class ProgressBar {

    private final int total;
    private final AtomicInteger current = new AtomicInteger(0);

    private final Instant start = Instant.now();
    private final int barWidth = 50;

    public ProgressBar(int total) {
        this.total = total;
    }

    public void step() {
        int done = current.incrementAndGet();
        print(done);
    }

    private void print(int done) {
        double progress = (double) done / total;
        int filled = (int) (progress * barWidth);

        String bar = "[%s%s]".formatted("#".repeat(filled), "-".repeat(barWidth - filled));

        long elapsed = Duration.between(start, Instant.now()).toMillis();
        long etaSec = (long) ((elapsed / (double) done) * (total - done)) / 1000;

        System.out.printf(
                "\r%s %3d%% (%d/%d), ETA: %ds",
                bar,
                (int) (progress * 100),
                done,
                total,
                etaSec
        );

        if (done == total) {
            System.out.println();
        }
    }
}
