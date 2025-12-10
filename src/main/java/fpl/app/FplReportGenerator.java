package fpl.app;

import fpl.api.dto.BootstrapResponse;
import fpl.api.dto.EntryInfo;
import fpl.api.dto.PlayerDto;
import fpl.domain.transfers.Transfer;
import fpl.domain.service.TransfersParsingService;
import fpl.domain.utils.PlayerUtils;
import fpl.parser.BootstrapParser;
import fpl.domain.service.LinkService;
import fpl.domain.service.TeamParsingService;
import fpl.logging.FplLogger;
import fpl.domain.model.Team;
import fpl.output.ReportExportService;
import fpl.parser.StandingsParser;
import org.fusesource.jansi.AnsiConsole;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FplReportGenerator {
    private static final Logger logger = Logger.getLogger(FplReportGenerator.class.getName());

    public static void main(String[] args) throws Exception {
        AnsiConsole.systemInstall();

        int totalStandingsPages = ConsoleService.getEnteredPageCount();
        ConsoleService.terminateProgramIfNeeded(totalStandingsPages);
        FplLogger.writeProcessingLog(totalStandingsPages);

        logger.info("ℹ️ Starting to parse pages!!");
        long startTime = System.currentTimeMillis();

        BootstrapResponse bootstrapResponse = BootstrapParser.parse();

        int lastEvent = BootstrapParser.getLastEvent(bootstrapResponse);
        List<PlayerDto> playersData = BootstrapParser.getPlayers(bootstrapResponse);
        Map<Integer, PlayerDto> playersById = PlayerUtils.getPlayersById(playersData);

        logger.info("ℹ️ Fetching all team links...");
        List<EntryInfo> entries = StandingsParser.of(totalStandingsPages).parse();

        List<URI> teamUris = LinkService.collectTeamEndpoints(entries, lastEvent);
        List<URI> transfersUris = LinkService.collectTeamTransfersEndpoints(entries);
        logger.info("✅ Successfully retrieved all team links (in " + (System.currentTimeMillis() - startTime) / 1000 + " sec).");

        logger.info("ℹ️ Collecting data from the pages...");
        List<Team> teams = TeamParsingService.collectStats(playersById, teamUris);
        List<Transfer> transfers = TransfersParsingService.collectTransfers(playersById, transfersUris, teams, lastEvent);

        logger.info("ℹ️ Collecting players data from API...");

        new ReportExportService().exportResults(teams, playersData, transfers, lastEvent, args);

        logger.info("⏱️ Completed in " + (System.currentTimeMillis() - startTime) / 1000 + "s");
        AnsiConsole.systemUninstall();
    }
}
