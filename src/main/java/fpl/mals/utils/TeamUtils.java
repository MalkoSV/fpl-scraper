package fpl.mals.utils;

import fpl.mals.Team;
import fpl.mals.TeamSummary;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TeamUtils {

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

    public static final String JS_FOR_PLAYERS_SCRAPING = """
            (
                (
                    goalkeeperSelector, defenderSelector, midfielderSelector, offenderSelector, benchSelector,
                    nameSelector, scoreSelector, captainIconSelector, viceIconSelector, startSquadSelector, hasTripleCaptain
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
                                    isTripleCaptain: !!p.querySelector(captainIconSelector) && hasTripleCaptain,
                                    isVice:     !!p.querySelector(viceIconSelector),
                                    isStarting: !!p.querySelector(startSquadSelector)
                                }));
                        }
            
                        return {
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

    public static Map<String, Long> calculateFormationType(List<Team> teams) {
        return teams.stream()
                .map(t -> String.format("%d-%d-%d",
                        t.getDefenders().size(),
                        t.getMidfielders().size(),
                        t.getOffenders().size()
                ))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
