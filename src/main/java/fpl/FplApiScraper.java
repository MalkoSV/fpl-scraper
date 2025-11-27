package fpl;

import fpl.api.model.PlayerDto;
import fpl.api.parser.PlayerParser;
import fpl.utils.FplLogger;
import fpl.web.model.Team;
import fpl.utils.OutputUtils;
import fpl.utils.Utils;
import org.fusesource.jansi.AnsiConsole;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class FplApiScraper {
    private static final Logger logger = Logger.getLogger(FplApiScraper.class.getName());

    public static void main(String[] args) throws Exception {
        System.setProperty("PLAYWRIGHT_BROWSERS_PATH", "browsers");
        AnsiConsole.systemInstall();

        int totalStandingsPage = Utils.getEnteredPageCount();
        Utils.terminateProgramIfNeeded(totalStandingsPage);
        FplLogger.writeProcessingLog(totalStandingsPage);

        logger.info("ℹ️ Starting to parse pages!!");
        long startTime = System.currentTimeMillis();

        logger.info("ℹ️ Fetching all team links...");
        List<String> allTeamLinks = Utils.collectAllTeamLinks(totalStandingsPage);
        logger.info("✅ Successfully retrieved all team links (in " + (System.currentTimeMillis() - startTime) / 1000 + " sec).");

        logger.info("ℹ️ Collecting data from the team pages...");
        List<Team> teams = Utils.collectStats(allTeamLinks);

        logger.info("ℹ️ Collecting players data from API...");
        List<PlayerDto> playersData = PlayerParser.parsePlayers();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String fileName = "FPL_Teams_top%d(%ds_duration)_%s.xlsx".formatted(
                allTeamLinks.size(), (System.currentTimeMillis() - startTime) / 1000, timestamp);
        OutputUtils.exportResultsToExcel(teams, playersData, fileName, args);

        logger.info("⏱️ Completed in " + (System.currentTimeMillis() - startTime) / 1000 + "s");
        AnsiConsole.systemUninstall();
        Thread.sleep(3000);
    }
}
