package fpl.mals;

import fpl.mals.utils.OutputUtils;
import fpl.mals.utils.SelectorUtils;
import fpl.mals.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class FplScraper {
    private static final Logger logger = Logger.getLogger(FplScraper.class.getName());

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("PLAYWRIGHT_BROWSERS_PATH", "browsers");

        int standingsPageCount = Utils.getEnteredPageCount();
        Utils.terminateProgramIfNeeded(standingsPageCount);
        String playerSelector = SelectorUtils.getPlayerSelector();

        logger.info("ℹ️ Start parsing from " + standingsPageCount + " pages (" + standingsPageCount * 50 + " teams)");
        long startTime = System.currentTimeMillis();

        List<String> allTeamLinks = Utils.getAllTeamLinks(standingsPageCount);
        logger.info("✅ All team links received (in " + (System.currentTimeMillis() - startTime) / 1000 + " sec).");

        logger.info("🚀 Running in multi-threaded mode by Browser pool...");
        if (playerSelector == null) {
            List<Team> teams = Utils.collectStats(allTeamLinks);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
            String fileName = "FPL_Teams_top%d(%ds_duration)_%s.xlsx".formatted(
                    allTeamLinks.size(), (System.currentTimeMillis() - startTime) / 1000, timestamp);
            OutputUtils.saveAllResultsToExcel(teams, fileName, args);

        } else {
            List<Player> players = Utils.collectPlayers(allTeamLinks, playerSelector);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
            String fileName = "FPL_Teams_top%d(%d_players-%ds_duration)_%s.xlsx".formatted(
                    allTeamLinks.size(), players.size(), (System.currentTimeMillis() - startTime) / 1000, timestamp);
            OutputUtils.saveResultsToExcel(players, fileName, args);
        }

        logger.info("⏱️ Completed in " + (System.currentTimeMillis() - startTime) / 1000 + "s");
        Thread.sleep(3000);
    }
}
