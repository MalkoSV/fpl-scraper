package fpl.mals.utils;

import fpl.mals.Team;
import fpl.mals.TeamSummary;

import java.util.List;

public class TeamUtils {
    public static TeamSummary calculateSummary(List<Team> teams) {
        return new TeamSummary(
                teams.size(),
                teams.stream().mapToInt(Team::getTripleCaptain).sum(),
                teams.stream().mapToInt(Team::getWildCard).sum(),
                teams.stream().mapToInt(Team::getBenchBoost).sum(),
                teams.stream().mapToInt(Team::getFreeHit).sum(),
                PlayerUtils.mergePlayers(Utils.getFullPlayerListFromTeams(teams))
        );
    }
}
