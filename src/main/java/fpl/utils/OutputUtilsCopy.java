package fpl.utils;

import fpl.api.model.Player;
import fpl.api.model.Team;
import fpl.api.model.TeamSummary;
import fpl.api.model.dto.PlayerDto;
import fpl.output.OutputDirectoryResolver;
import fpl.service.PlayerApiService;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OutputUtilsCopy {

    private static final Logger logger = Logger.getLogger(OutputUtilsCopy.class.getName());

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

    public static void exportResultsToExcel(List<Team> teams, List<PlayerDto> playersData, String fileName, String[] args) {
        TeamSummary summary = TeamUtils.calculateSummary(teams);
        File file = new File(new OutputDirectoryResolver().resolve(args), fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet allPlayersSheet = createPlayerGwSheet(workbook, summary.players(), "All players");
            createPlayerGwSheet(workbook, PlayerUtils.getOnlyStartPlayers(summary.players()), "Only start");
            createPlayerGwSheet(workbook, PlayerUtils.getOnlyBenchPlayers(summary.players()), "Only bench");
            createPlayerGwSheet(workbook, PlayerUtils.getDoubtfulPlayers(summary.players()), "Doubtful");
            createPlayerGwSheet(workbook, PlayerUtils.getBenchPlayersWithHighPoints(summary.players()), "Bench (>5 points)");
            createPlayerGwSheet(workbook, PlayerUtils.getPlayersWhoCaptain(summary.players()), "Captain");
            addSummaryInformation(workbook, allPlayersSheet, teams, summary);
            createPlayerStatsSheet(workbook, PlayerApiService.filter(playersData, 20, 3,1.2),"Players stats");

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            logger.severe("❌ Failed to save Excel file: " + e.getMessage());
        }
        logger.info("💾 Excel file saved successfully: " + file.getAbsolutePath());
    }

    public static Sheet createPlayerGwSheet(Workbook workbook, List<Player> players, String sheetName) {
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

    public static Sheet createPlayerStatsSheet(Workbook workbook, List<PlayerDto> players, String sheetName) {

        List<String> columnHeaders = List.of(
                "Name",
                "Points",
                "Round",
                "PPM",
                "Form",
                "Rank(F)",
                "Bonus",
                "Minutes",
                "Starts",
                "CSheets",
                "DA",
                "DA(90)",
                "Goals",
                "Assists",
                "G+A",
                "xG",
                "xA",
                "xGI",
                "GA-xGI",
                "#Corn",
                "#Free",
                "#Pen",
                "% sel",
                "Cost",
                "Val(S)",
                "Val(F)",
                "News"
        );

        Sheet sheet = workbook.createSheet(sheetName);
        CellStyle headerStyle = getHeaderStyle(workbook);

        Row header = sheet.createRow(0);
        for (int i = 0; i < columnHeaders.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(columnHeaders.get(i));
            headerCell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (var entry : players) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.webName());
            row.createCell(1).setCellValue(entry.totalPoints());
            row.createCell(2).setCellValue(entry.eventPoints());
            row.createCell(3).setCellValue(entry.pointsPerGame());
            row.createCell(4).setCellValue(entry.form());
            row.createCell(5).setCellValue(entry.formRankType());
            row.createCell(6).setCellValue(entry.bonus());
            row.createCell(7).setCellValue(entry.minutes());
            row.createCell(8).setCellValue(entry.starts());
            row.createCell(9).setCellValue(entry.cleanSheets());
            row.createCell(10).setCellValue(entry.defensiveContribution());
            row.createCell(11).setCellValue(entry.defensiveContributionPer90());
            row.createCell(12).setCellValue(entry.goalsScored());
            row.createCell(13).setCellValue(entry.assists());
            row.createCell(14).setCellValue(entry.goalsScored() + entry.assists());
            row.createCell(15).setCellValue(entry.expectedGoals());
            row.createCell(16).setCellValue(entry.expectedAssists());
            row.createCell(17).setCellValue(entry.expectedGoalInvolvements());
            row.createCell(18).setCellValue((entry.goalsScored() + entry.assists()) - entry.expectedGoalInvolvements());
            row.createCell(19).setCellValue(entry.cornersAndIndirectFreekicksOrder());
            row.createCell(20).setCellValue(entry.directFreekicksOrder());
            row.createCell(21).setCellValue(entry.penaltiesOrder());
            row.createCell(22).setCellValue(entry.selectedByPercent());
            row.createCell(23).setCellValue((double) entry.nowCost() / 10);
            row.createCell(24).setCellValue(entry.valueSeason());
            row.createCell(25).setCellValue(entry.valueForm());
            row.createCell(26).setCellValue(entry.news());
        }
        for (int i = 0; i < columnHeaders.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        return sheet;
    }

    public static void addSummaryInformation(
            Workbook workbook,
            Sheet sheetName,
            List<Team> teams,
            TeamSummary summary
    ) {

        Map<String, Long> formations = TeamUtils.calculateFormationType(teams);
        Map<Long, Long> zeroPlayersCount = TeamUtils.calculateStartPlayersWithZero(teams);
        Map<Integer, Long> transfersCount = TeamUtils.calculateTransfers(teams);
        Map<Integer, Long> transfersCostCount = TeamUtils.calculateTransfersCost(teams);

        int baseColumn = COLUMNHEADERS.size();
        int summaryColumn1 = baseColumn + 1;
        int summaryColumn2 = baseColumn + 2;
        int zeroPlayersColumn1 = summaryColumn2 + 2;
        int zeroPlayersColumn2 = summaryColumn2 + 3;
        int transfersColumn1 = zeroPlayersColumn2 + 2;
        int transfersColumn2 = zeroPlayersColumn2 + 3;
        int formationsColumn1 = transfersColumn2 + 2;
        int formationsColumn2 = transfersColumn2 + 3;

        Object[][] teamsInformation = {
                {"Teams",          summary.count()},
                {"Players",        summary.players().size()},
                {"Triple Captain", summary.tripleCaptain()},
                {"Wildcard",       summary.wildcard()},
                {"Bench Boost",    summary.benchBoost()},
                {"Free Hit",       summary.freeHit()}
        };
        CellStyle headerStyle = getHeaderStyle(workbook);
        headerStyle.setAlignment(HorizontalAlignment.LEFT);

        writeSimpleTable(sheetName, 0,
                summaryColumn1,
                summaryColumn2,
                headerStyle,
                teamsInformation
        );
        writeMappedSection(sheetName, 0,
                zeroPlayersColumn1, "0 pts players",
                zeroPlayersColumn2, "Teams",
                headerStyle,
                zeroPlayersCount
        );
        writeMappedSection(sheetName, 0,
                transfersColumn1, "Total transfers",
                transfersColumn2, "Teams",
                headerStyle,
                transfersCount
        );
        writeMappedSection(sheetName, transfersCount.size() + 1,
                transfersColumn1, "Transfers cost",
                transfersColumn2, "Teams",
                headerStyle,
                transfersCostCount
        );
        writeMappedSection(sheetName, 0,
                formationsColumn1, "Formations",
                formationsColumn2, "Teams",
                headerStyle,
                formations
        );

        sheetName.autoSizeColumn(summaryColumn1);
        sheetName.autoSizeColumn(summaryColumn2);
        sheetName.autoSizeColumn(zeroPlayersColumn1);
        sheetName.autoSizeColumn(zeroPlayersColumn2);
        sheetName.autoSizeColumn(transfersColumn1);
        sheetName.autoSizeColumn(transfersColumn2);
        sheetName.autoSizeColumn(formationsColumn1);
        sheetName.autoSizeColumn(formationsColumn2);
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

    private static int writeMappedSection(
            Sheet sheet,
            int startRow,
            int col1,
            String title1,
            int col2,
            String title2,
            CellStyle style,
            Map<?,?> map
    ) {
        Row header = sheet.getRow(startRow);
        if (header == null) {
            header = sheet.createRow(startRow);
        }

        Cell h1 = header.createCell(col1);
        h1.setCellValue(title1);
        h1.setCellStyle(style);

        Cell h2 = header.createCell(col2);
        h2.setCellValue(title2);
        h2.setCellStyle(style);

        return writeMapBlock(sheet, startRow + 1, col1, col2, map);
    }

    private static int writeSimpleTable(
            Sheet sheet,
            int startRow,
            int col1,
            int col2,
            CellStyle style,
            Object[][] rows
    ) {
        int rowNum = startRow;

        for (Object[] rowData : rows) {
            Row row = sheet.getRow(rowNum);
            if (row == null) row = sheet.createRow(rowNum);

            Cell c1 = row.createCell(col1);
            c1.setCellValue((String) rowData[0]);
            c1.setCellStyle(style);

            row.createCell(col2).setCellValue(((Number) rowData[1]).doubleValue());

            rowNum++;
        }
        return rowNum;
    }

    private static int writeMapBlock(
            Sheet sheet,
            int startRow,
            int col1,
            int col2,
            Map<?, ?> map
    ) {

        int rowNum = startRow;
        CellStyle simpleStyle = sheet.getWorkbook().createCellStyle();
        simpleStyle.setAlignment(HorizontalAlignment.CENTER);

        for (var entry : map.entrySet()) {
            Row row = sheet.getRow(rowNum);

            if (row == null){
                row = sheet.createRow(rowNum);
            }

            Cell c1 = row.createCell(col1);
            Object key = entry.getKey();
            if (key instanceof Number n) {
                c1.setCellValue(n.doubleValue());
            } else {
                c1.setCellValue(key.toString());
            }
            c1.setCellStyle(simpleStyle);

            Cell c2 = row.createCell(col2);
            Object value = entry.getValue();
            if (value instanceof Number n) {
                c2.setCellValue(n.doubleValue());
            } else {
                c2.setCellValue(value.toString());
            }
            rowNum++;
        }
        return rowNum;
    }

}
