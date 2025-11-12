package fpl.mals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FplScraper {
    private static final Logger logger = Logger.getLogger(FplScraper.class.getName());
    private static final String ABSENT_PLAYER = null;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("PLAYWRIGHT_BROWSERS_PATH", "browsers");

        int standingsPageCount = Utils.getEnteredPageCount();
        Utils.terminateProgramIfNeeded(standingsPageCount);

        logger.info("✅ Start parsing from " + standingsPageCount + " pages (" + standingsPageCount * 50 + " teams)");
        long startTime = System.currentTimeMillis();

        List<String> allTeamLinks = Utils.getAllTeamLinks(standingsPageCount);
        Map<String, Integer> players = Utils.collectPlayers(allTeamLinks, Utils.getThreadMode(), ABSENT_PLAYER);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String fileName = String.format("FPL_Teams_top%d(%d_players-%ds_duration)_%s.xlsx",
                allTeamLinks.size(), players.size(), (System.currentTimeMillis() - startTime) / 1000, timestamp);

        Utils.saveResultsToExcel(players, fileName, args);

        logger.info("⏱️ Completed in " + (System.currentTimeMillis() - startTime) / 1000 + "s");
        Thread.sleep(3000);
    }
}
