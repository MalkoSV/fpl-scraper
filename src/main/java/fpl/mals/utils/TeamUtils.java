package fpl.mals.utils;

import fpl.mals.Team;
import fpl.mals.TeamSummary;

import java.util.List;

public class TeamUtils {

    public static final String JS_FOR_TEAM_PAGE_SCRAPING_LIGHT = """
            (
                (
                    teamNameSelector, chipSelector
                ) => {
                    return {
                        teamName: document.querySelector(teamNameSelector)?.innerText || '',
                        chip    : document.querySelector(chipSelector)?.innerText || '',
                    };
                }
            )
            """;

    public static final String JS_FOR_TEAM_PAGE_SCRAPING = """
            (            
                (
                    goalkeeperSelector, defenderSelector, midfielderSelector, offenderSelector, benchSelector,
                    nameSelector, scoreSelector, captainIconSelector, viceIconSelector, startSquadSelector,
                    teamNameSelector, teamPositionSelector, tripleCaptainText, benchBoostText, freeHitText, wildcardText
                ) => {
                        const findText = (text) => [...document.querySelectorAll('*')].some(el => el.innerText && el.innerText.trim() === text);
            
                        const positions = {
                            GOALKEEPER: goalkeeperSelector,
                            DEFENDER:   defenderSelector,
                            MIDFIELDER: midfielderSelector,
                            OFFENDER:   offenderSelector,
                            BENCH:      benchSelector
                        };
            
                        const playersByPosition = {};
                        for (const [pos, selector] of Object.entries(positions)) {
                            const elements = selector ? document.querySelectorAll(selector) : [];
                            playersByPosition[pos] = [...elements].map(p => ({
                                    name:       p.querySelector(nameSelector)?.innerText?.trim() || '',
                                    score:      parseInt(p.querySelector(scoreSelector)?.innerText) || 0,
                                    isCaptain:  !!p.querySelector(captainIconSelector),
                                    isTripleCaptain: !!p.querySelector(tripleCaptainText) && findText(tripleCaptainText),
                                    isVice:     !!p.querySelector(viceIconSelector),
                                    isStarting: !!p.querySelector(startSquadSelector)
                                }));
                        }
            
                        return {
                            teamName:      document.querySelector(teamNameSelector)?.innerText || '',
                            teamPosition:  document.querySelector(teamPositionSelector)?.innerText || '',
                            tripleCaptain: findText(tripleCaptainText),
                            benchBoost:    findText(benchBoostText),
                            freeHit:       findText(freeHitText),
                            wildcard:      findText(wildcardText),
                            playersByPosition: playersByPosition
                        };
                    }
                )
            """;

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
