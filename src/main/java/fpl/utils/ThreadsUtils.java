package fpl.utils;

public class ThreadsUtils {

    private ThreadsUtils() {}

    public static int getThreadsNumber() {
        int threadsMin = 16;

        return Math.min(threadsMin, Runtime.getRuntime().availableProcessors() * 2);
    }
}
