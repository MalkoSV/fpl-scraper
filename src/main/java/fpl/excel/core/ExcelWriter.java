package fpl.excel.core;

import fpl.excel.builder.GenericSheetWriter;
import fpl.excel.io.FileNameGenerator;
import fpl.excel.io.WorkbookFactory;
import fpl.excel.style.ExcelStyleFactory;
import fpl.output.OutputDirectoryResolver;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class ExcelWriter {

    private static final Logger logger = Logger.getLogger(ExcelWriter.class.getName());

    private final WorkbookFactory workbookFactory;
    private final OutputDirectoryResolver directoryResolver;
    private final FileNameGenerator fileNameGenerator;

    public ExcelWriter(
            WorkbookFactory workbookFactory,
            OutputDirectoryResolver directoryResolver,
            FileNameGenerator fileNameGenerator
    ) {
        this.workbookFactory = workbookFactory;
        this.directoryResolver = directoryResolver;
        this.fileNameGenerator = fileNameGenerator;
    }

    public void writeExcel(String baseFileName,
                           String[] args,
                           GenericSheetWriter<?>... sheetWriters) {

        File outputDir = directoryResolver.resolve(args);
        String finalFileName = fileNameGenerator.generate(baseFileName);
        File outputFile = new File(outputDir, finalFileName);

        try (Workbook workbook = workbookFactory.createWorkbook()) {

            ExcelStyleFactory styleFactory = new ExcelStyleFactory(workbook);

            for (GenericSheetWriter<?> writer : sheetWriters) {
                writer.writeSheet(workbook, styleFactory);
            }

            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                workbook.write(out);
            }

            logger.info("Excel file saved: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            logger.severe("Failed to write Excel file: " + e.getMessage());
        }
    }
}
