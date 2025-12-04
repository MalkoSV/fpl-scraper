package fpl.excel.io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileNameGenerator {

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    public String generate(String baseName) {
        return baseName + "_" + LocalDateTime.now().format(formatter) + ".xlsx";
    }
}
