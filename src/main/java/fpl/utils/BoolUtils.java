package fpl.utils;

public class BoolUtils {
    private BoolUtils() {}

    public static int asInt(boolean predicate) {
        return predicate ? 1 : 0;
    }
}
