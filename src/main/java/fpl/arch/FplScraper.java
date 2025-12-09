package fpl.arch;

import fpl.api.dto.BootstrapResponse;
import fpl.api.dto.PlayerDto;
import fpl.app.ConsoleService;
import fpl.parser.BootstrapParser;
import fpl.output.ReportExportService;
import fpl.arch.scraper.TeamLinksScraper;
import fpl.domain.model.Team;
import org.fusesource.jansi.AnsiConsole;

import java.util.List;
import java.util.logging.Logger;

public class FplScraper {
    private static final Logger logger = Logger.getLogger(FplScraper.class.getName());

    public static void main(String[] args) throws Exception {
        System.setProperty("PLAYWRIGHT_BROWSERS_PATH", "browsers");
        AnsiConsole.systemInstall();

        int standingsPageCount = ConsoleService.getEnteredPageCount();
        ConsoleService.terminateProgramIfNeeded(standingsPageCount);
        switch (standingsPageCount) {
            case 21 -> System.out.println("ℹ️  Processing Mals League teams...");
            case 22 -> System.out.println("ℹ️  Processing Prognozilla teams...");
            default -> System.out.printf("ℹ️  Processing the first %d teams...%n%n", standingsPageCount * 50);
        }

        logger.info("ℹ️ Starting to parse pages!!");
        long startTime = System.currentTimeMillis();

        logger.info("ℹ️ Fetching all team links...");
        List<String> allTeamLinks = TeamLinksScraper.collectAllTeamLinks(standingsPageCount);
        logger.info("✅ Successfully retrieved all team links (in " + (System.currentTimeMillis() - startTime) / 1000 + " sec).");

        logger.info("ℹ️ Collecting data from the team pages...");
        List<Team> teams = TeamLinksScraper.collectStats(allTeamLinks);

        logger.info("ℹ️ Collecting players data from API...");
        BootstrapResponse bootstrapResponse = BootstrapParser.parse();
        List<PlayerDto> playersData = BootstrapParser.getPlayers(bootstrapResponse);

        new ReportExportService().exportResults(teams, playersData, null,1, args);

        logger.info("⏱️ Completed in " + (System.currentTimeMillis() - startTime) / 1000 + "s");
        AnsiConsole.systemUninstall();
        Thread.sleep(3000);
    }
}
