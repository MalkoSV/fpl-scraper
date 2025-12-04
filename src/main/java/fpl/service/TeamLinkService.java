package fpl.service;

import fpl.api.FplApiEndPoints;
import fpl.api.model.dto.BootstrapResponse;
import fpl.api.model.dto.TeamStats;
import fpl.api.parser.BootstrapParser;
import fpl.api.parser.StandingsParser;

import java.net.URI;
import java.util.List;

public final class TeamLinkService {

    private TeamLinkService() {}

    public static List<URI> collectTeamLinks(int totalPages) throws Exception {
        List<TeamStats> teams = StandingsParser.of(totalPages).parseStandings();
        BootstrapResponse bootstrapResponse = BootstrapParser.parseBootstrap();
        int event = BootstrapParser.getLastEvent(bootstrapResponse);

        return teams.stream()
                .map(team -> FplApiEndPoints.getUri(
                        FplApiEndPoints.TEAM,
                        team.entry(),
                        event
                ))
                .toList();
    }
}
