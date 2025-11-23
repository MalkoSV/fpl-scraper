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

    public static final List<String> COLUMNHEADERS = List.of(
            "Name",
            "Count",
            "Start",
            "Captain",
            "Triple",
            "Vice",
            "Bench",
            "Availability",
            "Points"
    );


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

    public static void exportResultsToExcel(List<Team> teams, String fileName, String[] args) {
        TeamSummary summary = TeamUtils.calculateSummary(teams);
        File file = new File(getOutputDir(args), fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet allPlayersSheet = createPlayersSheet(workbook, summary.players(), "All players");
            createPlayersSheet(workbook, PlayerUtils.getOnlyStartPlayers(summary.players()), "Only start");
            createPlayersSheet(workbook, PlayerUtils.getOnlyBenchPlayers(summary.players()), "Only bench");
            createPlayersSheet(workbook, PlayerUtils.getDoubtfulPlayers(summary.players()), "Doubtful");
            createPlayersSheet(workbook, PlayerUtils.getBenchPlayersWithHighPoints(summary.players()), "Bench (>5 points)");
            createPlayersSheet(workbook, PlayerUtils.getPlayersWhoCaptain(summary.players()), "Captain");
            addSummaryInformation(workbook, allPlayersSheet, teams, summary);

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            logger.severe("❌ Failed to save Excel file: " + e.getMessage());
        }
        logger.info("💾 Excel file saved successfully: " + file.getAbsolutePath());
    }

    public static Sheet createPlayersSheet(Workbook workbook, List<Player> players, String sheetName) {
        Sheet sheet = workbook.createSheet(sheetName);
        CellStyle headerStyle = getHeaderStyle(workbook);

        Row header = sheet.createRow(0);
        for (int i = 0; i < COLUMNHEADERS.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(COLUMNHEADERS.get(i));
            headerCell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
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
            row.createCell(7).setCellValue(entry.getAvailability());
            row.createCell(8).setCellValue(entry.getPoints());
        }
        sheet.autoSizeColumn(0);

        return sheet;
    }

    public static void addSummaryInformation(Workbook workbook, Sheet sheetName, List<Team> teams, TeamSummary summary) {
        Map<String, Long> formations = TeamUtils.calculateFormationType(teams);
        Map<Long, Long> countWithZero = TeamUtils.calculateStartPlayersWithZero(teams);
        int columnCount = COLUMNHEADERS.size();
        int column1 = columnCount + 1;
        int column2 = columnCount + 2;

        Object[][] rows = {
                {"Teams",          summary.count()},
                {"Players",        summary.players().size()},
                {"Triple Captain", summary.tripleCaptain()},
                {"Wildcard",       summary.wildcard()},
                {"Bench Boost",    summary.benchBoost()},
                {"Free Hit",       summary.freeHit()}
        };

        CellStyle headerStyle = getHeaderStyle(workbook);
        headerStyle.setAlignment(HorizontalAlignment.LEFT);
        for (int i = 0; i < rows.length; i++) {
            Row row = sheetName.getRow(i + 1);

            Cell cell1 = row.createCell(column1);
            cell1.setCellValue((String) rows[i][0]);
            cell1.setCellStyle(headerStyle);

            Cell cell2 = row.createCell(column2);
            cell2.setCellValue(((Number) rows[i][1]).doubleValue());
        }

        int n = rows.length + 2;
        for (var entry : formations.entrySet()) {
            Row row = sheetName.getRow(n);
            if (row == null) {
                row = sheetName.createRow(n);
            }
            Cell cell1 = row.createCell(column1);
            cell1.setCellValue(entry.getKey());
            cell1.setCellStyle(headerStyle);

            Cell cell2 = row.createCell(column2);
            cell2.setCellValue(entry.getValue());
            n++;
        }
        sheetName.autoSizeColumn(column1);

        int column3 = column2 + 2;
        int column4 = column2 + 3;

        Cell headerCell1 = sheetName.getRow(0).createCell(column3);
        headerCell1.setCellValue("0 pts players");
        headerCell1.setCellStyle(headerStyle);
        sheetName.autoSizeColumn(column3);

        Cell headerCell2 = sheetName.getRow(0).createCell(column4);
        headerCell2.setCellValue("Teams");
        headerCell2.setCellStyle(headerStyle);
        sheetName.autoSizeColumn(column4);

        int rowNum = 1;
        for (var entry : countWithZero.entrySet()) {
            Row row = sheetName.getRow(rowNum++);
            row.createCell(column3).setCellValue(entry.getKey());
            row.createCell(column4).setCellValue(entry.getValue());
        }

    }

    public static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        return headerStyle;
    }

}
