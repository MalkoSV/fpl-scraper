package fpl.mals.utils;

import fpl.mals.Player;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OutputUtils {

    private static final Logger logger = Logger.getLogger(OutputUtils.class.getName());

    public static File getOutputDir(String[] args) {
        String outputDir = Arrays.stream(args)
                .filter(arg -> arg.startsWith("/output=") || arg.startsWith("--output="))
                .findFirst()
                .map(arg -> arg.substring(arg.indexOf('=') + 1).trim())
                .orElse(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "FPLScraper");

        File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            System.err.println("❌ Cannot create output directory: " + outDir.getAbsolutePath());
        }

        return outDir;
    }

    public static void saveResultsToExcel(List<Player> players, String fileName, String[] args) {
        File file = new File(getOutputDir(args), fileName);
        List<String> columnHeader = List.of(
                "Name",
                "Count",
                "Start",
                "Captain",
                "Triple",
                "Vice",
                "Bench",
                "Score"
        );
        int columnCount = columnHeader.size();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Players");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row header = sheet.createRow(0);
            Cell headerCell;

            for (int i = 0; i < columnCount; i++) {
                headerCell = header.createCell(i);
                headerCell.setCellValue(columnHeader.get(i));
                headerCell.setCellStyle(headerStyle);

            }

            int rowNum = 1;
            for (var entry : players) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getName());
                row.createCell(1).setCellValue(entry.getCount());
                row.createCell(2).setCellValue(entry.getStart());
                row.createCell(3).setCellValue(entry.getCaptain());
                row.createCell(4).setCellValue(entry.getTripleCaptain());
                row.createCell(5).setCellValue(entry.getVice());
                row.createCell(6).setCellValue(entry.getCount() - entry.getStart());
                row.createCell(7).setCellValue(entry.getScore());
            }

            for (int i = 0; i < columnCount ; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            logger.severe("❌ Failed to save Excel file: " + e.getMessage());
        }
        logger.info("💾 Excel file saved successfully: " + file.getAbsolutePath());
    }

    public static void saveResultsToExcel(Map<String, Integer> players, String fileName, String[] args) {
        File file = new File(getOutputDir(args), fileName);
        List<Map.Entry<String, Integer>> sortedPlayers = players.entrySet().stream()
                .sorted(Comparator
                        .comparing(Map.Entry<String, Integer>::getValue).reversed()
                        .thenComparing(Map.Entry::getKey)
                ).toList();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Players");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row header = sheet.createRow(0);

            Cell headerCell0 = header.createCell(0);
            headerCell0.setCellValue("Name");
            headerCell0.setCellStyle(headerStyle);

            Cell headerCell1 = header.createCell(1);
            headerCell1.setCellValue("Count");
            headerCell1.setCellStyle(headerStyle);

            int rowNum = 1;
            for (var entry : sortedPlayers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            logger.severe("❌ Failed to save Excel file: " + e.getMessage());
        }
        logger.info("💾 Excel file saved successfully: " + file.getAbsolutePath());
    }

}
