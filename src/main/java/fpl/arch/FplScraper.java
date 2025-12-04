package fpl.arch;

import fpl.api.model.dto.BootstrapResponse;
import fpl.api.model.dto.PlayerDto;
import fpl.api.parser.BootstrapParser;
import fpl.utils.OutputUtils;
import fpl.utils.Utils;
import fpl.api.model.Team;
import org.fusesource.jansi.AnsiConsole;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class FplScraper {
    private static final Logger logger = Logger.getLogger(FplScraper.class.getName());

    public static void main(String[] args) throws Exception {
        System.setProperty("PLAYWRIGHT_BROWSERS_PATH", "browsers");
        AnsiConsole.systemInstall();

        int standingsPageCount = Utils.getEnteredPageCount();
        Utils.terminateProgramIfNeeded(standingsPageCount);
        switch (standingsPageCount) {
            case 21 -> System.out.println("ℹ️  Processing Mals League teams...");
            case 22 -> System.out.println("ℹ️  Processing Prognozilla teams...");
            default -> System.out.printf("ℹ️  Processing the first %d teams...%n%n", standingsPageCount * 50);
        }


        logger.info("ℹ️ Starting to parse pages!!");
        long startTime = System.currentTimeMillis();

        logger.info("ℹ️ Fetching all team links...");
        List<String> allTeamLinks = Utils.collectAllTeamLinks(standingsPageCount);
        logger.info("✅ Successfully retrieved all team links (in " + (System.currentTimeMillis() - startTime) / 1000 + " sec).");

        logger.info("ℹ️ Collecting data from the team pages...");
        List<Team> teams = Utils.collectStats(allTeamLinks);

        logger.info("ℹ️ Collecting players data from API...");
        BootstrapResponse bootstrapResponse = BootstrapParser.parseBootstrap();
        List<PlayerDto> playersData = BootstrapParser.getPlayers(bootstrapResponse);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String fileName = "FPL_Teams_top%d(%ds_duration)_%s.xlsx".formatted(
                allTeamLinks.size(), (System.currentTimeMillis() - startTime) / 1000, timestamp);
        OutputUtils.exportResultsToExcel(teams, playersData, fileName, args);

        logger.info("⏱️ Completed in " + (System.currentTimeMillis() - startTime) / 1000 + "s");
        AnsiConsole.systemUninstall();
        Thread.sleep(3000);
    }
}
