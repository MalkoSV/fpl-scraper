package fpl.excel.sheets;

import fpl.api.dto.PlayerDto;
import fpl.excel.builder.Col;
import fpl.excel.builder.TableSheetWriter;

import java.util.List;

public class PlayerStatsSheetWriter extends TableSheetWriter<PlayerDto> {

    private static final List<Col<PlayerDto>> COLUMNS = List.of(
            new Col<>("  Name", PlayerDto::webName),
            new Col<>("Points", PlayerDto::totalPoints),
            new Col<>("   PPM", PlayerDto::pointsPerGame),
            new Col<>("  Form", PlayerDto::form),
            new Col<>("Rank(F)", PlayerDto::formRankType),
            new Col<>(" Round", PlayerDto::eventPoints),
            new Col<>(" Rnd-F", p -> p.eventPoints() - p.form()),
            new Col<>(" Bonus", PlayerDto::bonus),
            new Col<>("Minutes", PlayerDto::minutes),
            new Col<>("Starts", PlayerDto::starts),
            new Col<>("CSheets", PlayerDto::cleanSheets),
            new Col<>("    DC", PlayerDto::defensiveContribution),
            new Col<>("DC(90)", PlayerDto::defensiveContributionPer90),
            new Col<>(" Goals", PlayerDto::goalsScored),
            new Col<>("Assists", PlayerDto::assists),
            new Col<>("   G+A", p -> p.goalsScored() + p.assists()),
            new Col<>("    xG", PlayerDto::expectedGoals),
            new Col<>("    xA", PlayerDto::expectedAssists),
            new Col<>("   xGI", PlayerDto::expectedGoalInvolvements),
            new Col<>("GA-xGI", p -> p.goalsScored() + p.assists() - p.expectedGoalInvolvements()),
            new Col<>(" #Corn", PlayerDto::cornersAndIndirectFreekicksOrder),
            new Col<>(" #Free", PlayerDto::directFreekicksOrder),
            new Col<>("  #Pen", PlayerDto::penaltiesOrder),
            new Col<>(" % sel", PlayerDto::selectedByPercent),
            new Col<>("  Cost", p -> p.nowCost() / 10.0),
            new Col<>("Val(S)", PlayerDto::valueSeason),
            new Col<>("Val(F)", PlayerDto::valueForm),
            new Col<>("  News", PlayerDto::news)
    );

    public PlayerStatsSheetWriter(List<PlayerDto> players) {
        super("Player Stats", players, COLUMNS);
    }
}
