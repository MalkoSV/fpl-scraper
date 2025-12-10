package fpl.utils;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FplUtils {

    private static final Pattern ENTRY_PATTERN = Pattern.compile("/entry/(\\d+)/");

    public static Optional<Integer> extractEntryId(URI uri) {
        Matcher matcher = ENTRY_PATTERN.matcher(uri.getPath());
        if (matcher.find()) {
            return Optional.of(Integer.parseInt(matcher.group(1)));
        } else {
            return Optional.empty();
        }
    }
}
