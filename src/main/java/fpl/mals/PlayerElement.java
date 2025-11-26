package fpl.mals;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerElement(
        String webName,
        double form,
        double pointsPerGame,
        int totalPoints,
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
        String news
) {
    public static List<PlayerElement> filter(List<PlayerElement> players, int points, double ppm, double xgi) {
        return players.stream()
                .filter(
                        pe -> pe.totalPoints > points
                        && pe.pointsPerGame > ppm
                        && pe.expectedGoalInvolvements > xgi
                )
                .sorted(Comparator.comparing(PlayerElement::totalPoints)
                        .thenComparing(PlayerElement::form)
                        .reversed()
                )
                .toList();
    }
}
