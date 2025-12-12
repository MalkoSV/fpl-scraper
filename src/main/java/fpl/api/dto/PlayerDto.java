package fpl.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerDto(
        String webName,
        int id,
        int elementType,
        double form,
        double pointsPerGame,
        int totalPoints,
        int eventPoints,
        double valueForm,
        double valueSeason,
        int goalsScored,
        int assists,
        int bonus,
        int nowCost,
        double expectedGoals,
        double expectedAssists,
        double expectedGoalInvolvements,
        double selectedByPercent,
        int minutes,
        int starts,
        int defensiveContribution,

        @JsonProperty("defensive_contribution_per_90")
        double defensiveContributionPer90,

        int cleanSheets,
        int cornersAndIndirectFreekicksOrder,
        int directFreekicksOrder,
        int penaltiesOrder,
        int formRankType,
        Integer chanceOfPlayingThisRound,
        Integer chanceOfPlayingNextRound,
        String news
) {
    public int chanceSafe() {
        return chanceOfPlayingThisRound == null ? 100 : chanceOfPlayingThisRound;
    }
}
