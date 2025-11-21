package fpl.mals.utils;

import fpl.mals.Player;
import fpl.mals.Team;
import fpl.mals.TeamSummary;
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

    public static void saveAllResultsToExcel(List<Team> teams, String fileName, String[] args) {
        TeamSummary summary = TeamUtils.calculateSummary(teams);
        Map<String, Long> formations = TeamUtils.calculateFormationType(teams);

        File file = new File(getOutputDir(args), fileName);
        List<String> columnHeaders = List.of(
                "Name",
                "Count",
                "Start",
                "Captain",
                "Triple",
                "Vice",
                "Bench",
                "Availability",
                "Score"
        );

        int columnCount = columnHeaders.size();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Players");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row header = sheet.createRow(0);

            for (int i = 0; i < columnCount; i++) {
                Cell headerCell = header.createCell(i);
                headerCell.setCellValue(columnHeaders.get(i));
                headerCell.setCellStyle(headerStyle);

            }

            int rowNum = 1;
            for (var entry : summary.players()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getName());
                row.createCell(1).setCellValue(entry.getCount());
                row.createCell(2).setCellValue(entry.getStart());
                row.createCell(3).setCellValue(entry.getCaptain());
                row.createCell(4).setCellValue(entry.getTripleCaptain());
                row.createCell(5).setCellValue(entry.getVice());
                row.createCell(6).setCellValue(entry.getCount() - entry.getStart());
                row.createCell(7).setCellValue(entry.getAvailability());
                row.createCell(8).setCellValue(entry.getScore());
            }

            headerStyle.setAlignment(HorizontalAlignment.LEFT);
            int column1 = columnCount + 1;
            int column2 = columnCount + 2;

            Row row = sheet.getRow(1);
            Cell cell = row.createCell(column1);
            cell.setCellValue("Teams");
            cell.setCellStyle(headerStyle);
            cell = row.createCell(column2);
            cell.setCellValue(summary.count());

            row = sheet.getRow(2);
            cell = row.createCell(column1);
            cell.setCellValue("Players");
            cell.setCellStyle(headerStyle);
            cell = row.createCell(column2);
            cell.setCellValue(summary.players().size());

            row = sheet.getRow(3);
            cell = row.createCell(column1);
            cell.setCellValue("Triple Captain");
            cell.setCellStyle(headerStyle);
            cell = row.createCell(column2);
            cell.setCellValue(summary.tripleCaptain());

            row = sheet.getRow(4);
            cell = row.createCell(column1);
            cell.setCellValue("Wildcard");
            cell.setCellStyle(headerStyle);
            cell = row.createCell(column2);
            cell.setCellValue(summary.wildcard());

            row = sheet.getRow(5);
            cell = row.createCell(column1);
            cell.setCellValue("Bench Boost");
            cell.setCellStyle(headerStyle);
            cell = row.createCell(column2);
            cell.setCellValue(summary.benchBoost());

            row = sheet.getRow(6);
            cell = row.createCell(column1);
            cell.setCellValue("Free Hit");
            cell.setCellStyle(headerStyle);
            cell = row.createCell(column2);
            cell.setCellValue(summary.freeHit());

            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            int n = 8;
            for (var entry : formations.entrySet()) {
                row = sheet.getRow(n);
                if (row == null) {
                    row = sheet.createRow(n);
                }
                n++;
                cell = row.createCell(column1);
                cell.setCellValue(entry.getKey());
                cell.setCellStyle(headerStyle);
                cell = row.createCell(column2);
                cell.setCellValue(entry.getValue());
            }

            for (int i = 0; i < columnCount + 3; i++) {
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

}
