package fpl.utils;

public class ThreadsUtils {

    private ThreadsUtils() {}

    public static int getThreadsNumber(int totalUri) {
        int threadsMin = totalUri > 4000 ? 8 : 16;

        return Math.min(threadsMin, Runtime.getRuntime().availableProcessors() * 2);
    }
}
