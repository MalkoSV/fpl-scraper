package fpl.output;

import java.io.File;
import java.util.Arrays;

/**
 * Resolves the directory where Excel reports should be saved.
 * Supports CLI arguments:
 *   --output=/path/to/dir
 *   /output=/path/to/dir
 */
public class OutputDirectoryResolver {

    public File resolve(String[] args) {
        String outputDir = Arrays.stream(args)
                .filter(arg -> arg.startsWith("/output=") || arg.startsWith("--output="))
                .findFirst()
                .map(arg -> arg.substring(arg.indexOf('=') + 1).trim())
                .orElse(System.getProperty("user.home")
                        + File.separator + "Documents"
                        + File.separator + "FPL-reports");

        File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            throw new RuntimeException("Cannot create output directory: " + outDir.getAbsolutePath());
        }

        return outDir;
    }
}
