package fpl.mals.utils;

import com.microsoft.playwright.Locator;

public class SelectorUtils {

    public static final String BASE_URL = "https://fantasy.premierleague.com";
    public static final String BASE_OVERALL_LEAGUE_PATH = "/leagues/314/standings/c";
    public static final String RECORD_LINK_SELECTOR = "a._1jqkqxq4";

    public static final String TEAM_NAME_SELECTOR = "#page-title";
    public static final String TEAM_POSITION_SELECTOR = "h4:has-text(\"Overall rank\") + *";
    public static final String TRIPLE_CAPTAIN = "Triple Captain";
    public static final String BENCH_BOOST = "Bench Boost";
    public static final String FREE_HIT = "Free Hit";
    public static final String WILDCARD = "Wild Card";

    public static final String ALL_PLAYERS_SELECTOR = "._2j6lqn7";
    public static final String FOR_100PC_PLAYER_SELECTOR = "._174gkcl5";
    public static final String FOR_75PC_PLAYER_SELECTOR = "._174gkcl4";
    public static final String FOR_50PC_PLAYER_SELECTOR = "._174gkcl3";
    public static final String FOR_25PC_PLAYER_SELECTOR = "._174gkcl2";
    public static final String FOR_0PC_PLAYER_SELECTOR = "._174gkcl1";
    public static final String GOALKEEPER_LINE_PLAYER_SELECTOR = "._1k6tww12 ._2j6lqn6";
    public static final String DEFENDER_LINE_PLAYER_SELECTOR = "._1k6tww13 ._2j6lqn6";
    public static final String MIDFIELDER_LINE_PLAYER_SELECTOR = "._1k6tww14 ._2j6lqn6";
    public static final String OFFENDER_LINE_PLAYER_SELECTOR = "._1k6tww15 ._2j6lqn6";
    public static final String GOALKEEPER_CLASS = "_1k6tww12";
    public static final String DEFENDER_CLASS = "_1k6tww13";
    public static final String MIDFIELDER_CLASS = "_1k6tww14";
    public static final String OFFENDER_CLASS = "_1k6tww15";
    public static final String ALL_ROLES_PLAYERS_CONTAINER = "xpath=ancestor::*[contains(@class,'" + GOALKEEPER_CLASS + "') "
            + "or contains(@class,'" + DEFENDER_CLASS + "') "
            + "or contains(@class,'" + MIDFIELDER_CLASS + "') "
            + "or contains(@class,'" + OFFENDER_CLASS + "')]";
    public static final String START_SQUAD_SELECTOR = String.join(", ",
            GOALKEEPER_LINE_PLAYER_SELECTOR,
            DEFENDER_LINE_PLAYER_SELECTOR,
            MIDFIELDER_LINE_PLAYER_SELECTOR,
            OFFENDER_LINE_PLAYER_SELECTOR
    );
    public static final String BENCH_SELECTOR = ".tczxyc5 " + ALL_PLAYERS_SELECTOR;
    public static final String NAME_SELECTOR = "._174gkcl0";
    public static final String CAPTAIN_ICON_SELECTOR = "svg[aria-label='Captain']";
    public static final String VICE_ICON_SELECTOR = "svg[aria-label='Vice Captain']";
    public static final String GW_SCORE_SELECTOR = "._63rl0j3._63rl0j0 span:nth-of-type(2)";

    public static String getFullUrl(String urlEnd) {
        return BASE_URL + urlEnd;
    }

    public static String getStandingsPageUrl(int pageNumber) {
        return getFullUrl(BASE_OVERALL_LEAGUE_PATH + "?page_standings=" + pageNumber);
    }

    public static String filterSelectorByChild(String selector, String child) {
        return ":is(%s):has(%s)".formatted(selector, child);
    }

    public static String getSelectorChild(String selector, String child) {
        return ":is(%s) %s".formatted(selector, child);
    }

    public static String getPlayerSelector() {
        int filterType = InputUtils.getEnteredNumber(InputUtils.DESCRIPTION_FOR_CHOOSE_PLAYER_SELECTOR, 1, 5);

        return switch (filterType) {
            case 1 -> {
                System.out.println("""
                        ✅ Your choice - All players + Teams statistics!
                        ℹ️  Full statistics includes all players and teams.
                        """);
                yield null;
            }
            case 2 -> {
                System.out.println("""
                        ✅ Your choice - START SQUAD!
                        ℹ️  Collect 11 players from start squad.
                        """);
                yield START_SQUAD_SELECTOR;
            }
            case 3 -> {
                System.out.println("""
                        ✅ Your choice - CAPTAIN!
                        ℹ️  Collect players with CAPTAIN role.
                        """);
                yield filterSelectorByChild(ALL_PLAYERS_SELECTOR, CAPTAIN_ICON_SELECTOR);
            }
            case 4 -> {
                System.out.println("""
                        ✅ Your choice - BENCH!
                        ℹ️  Collect 4 players from bench.
                        """);
                yield BENCH_SELECTOR;
            }
            case 5 -> {
                System.out.println("""
                        ✅ Your choice - Doubtful, unlikely or unavailable to play (0-50%)!
                        ℹ️  Questionable or unavailable players.
                        """);
                yield String.join(", ",
                        filterSelectorByChild(ALL_PLAYERS_SELECTOR, FOR_50PC_PLAYER_SELECTOR),
                        filterSelectorByChild(ALL_PLAYERS_SELECTOR, FOR_25PC_PLAYER_SELECTOR),
                        filterSelectorByChild(ALL_PLAYERS_SELECTOR, FOR_0PC_PLAYER_SELECTOR)
                );
            }
            default -> throw new IllegalArgumentException("Unknown filter type: " + filterType);
        };
    }

    public static boolean hasCaptainIcon(Locator el) {
        return el.locator(CAPTAIN_ICON_SELECTOR).count() > 0;
    }

    public static boolean hasViceIcon(Locator el) {
        return el.locator(VICE_ICON_SELECTOR).count() > 0;
    }

    public static boolean hasStartSquad(Locator el) {
        return el.locator(ALL_ROLES_PLAYERS_CONTAINER).first().count() > 0;
    }
}
