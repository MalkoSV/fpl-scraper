package fpl.arch.utils;

import com.microsoft.playwright.Locator;

public class SelectorUtils {

    public static final String BASE_URL = "https://fantasy.premierleague.com";
    public static final String BASE_OVERALL_LEAGUE_PATH = "/leagues/314/standings/c";
    public static final String BASE_MALS_LEAGUE_PATH = "/leagues/1032011/standings/c";
    public static final String BASE_PROGNOZILLA_LEAGUE_PATH = "/leagues/1031449/standings/c";
    public static final String RECORD_LINK_SELECTOR = "a._1jqkqxq4";

    public static final String TEAM_NAME_SELECTOR = "#page-title";
    public static final String TRANSFERS_COUNT_SELECTOR = "li.rd5cco4:has(h4:text('Total transfers')) > div.rd5cco6";
    public static final String CHIP_SELECTOR = "div._18v1mulc";
    public static final String TRIPLE_CAPTAIN = "Triple Captain";
    public static final String BENCH_BOOST = "Bench Boost";
    public static final String FREE_HIT = "Free Hit";

    public static final String ALL_PLAYERS_SELECTOR = "._2j6lqn7";
    public static final String FOR_100PC_PLAYER_SELECTOR = "._174gkcl5";
    public static final String FOR_75PC_PLAYER_SELECTOR = "._174gkcl4";
    public static final String FOR_50PC_PLAYER_SELECTOR = "._174gkcl3";
    public static final String FOR_25PC_PLAYER_SELECTOR = "._174gkcl2";
    public static final String FOR_0PC_PLAYER_SELECTOR = "._174gkcl1";

    public static final String PLAYER_SELECTOR = "._2j6lqn6";
    public static final String GOALKEEPER_LINE_PLAYER_SELECTOR = "._1k6tww12 " + PLAYER_SELECTOR;
    public static final String DEFENDER_LINE_PLAYER_SELECTOR = "._1k6tww13 " + PLAYER_SELECTOR;
    public static final String MIDFIELDER_LINE_PLAYER_SELECTOR = "._1k6tww14 " + PLAYER_SELECTOR;
    public static final String OFFENDER_LINE_PLAYER_SELECTOR = "._1k6tww15 " + PLAYER_SELECTOR;

    public static final String GOALKEEPER_CLASS = "_1k6tww12";
    public static final String DEFENDER_CLASS = "_1k6tww13";
    public static final String MIDFIELDER_CLASS = "_1k6tww14";
    public static final String OFFENDER_CLASS = "_1k6tww15";
    public static final String ALL_ROLES_PLAYERS_CONTAINER = "xpath=ancestor::*[contains(@class,'" + GOALKEEPER_CLASS + "') "
            + "or contains(@class,'" + DEFENDER_CLASS + "') "
            + "or contains(@class,'" + MIDFIELDER_CLASS + "') "
            + "or contains(@class,'" + OFFENDER_CLASS + "')]";

    public static final String BENCH_SELECTOR = ".tczxyc5 " + ALL_PLAYERS_SELECTOR;
    public static final String NAME_SELECTOR = "._174gkcl0";
    public static final String CAPTAIN_ICON_SELECTOR = "svg[aria-label='Captain']";
    public static final String VICE_ICON_SELECTOR = "svg[aria-label='Vice Captain']";
    public static final String PLAYER_POINTS_SELECTOR = "._63rl0j3._63rl0j0 span:nth-of-type(2)";

    public static String getFullUrl(String urlEnd) {
        return BASE_URL + urlEnd;
    }

    public static String getStandingsPageUrl(int pageNumber, int mode) {
            return getFullUrl(getStandingsPagePath(mode) + "?page_standings=" + pageNumber);
        }

    public static String getStandingsPagePath(int mode) {
        return switch (mode) {
            case 21 -> BASE_MALS_LEAGUE_PATH;
            case 22 -> BASE_PROGNOZILLA_LEAGUE_PATH;
            default -> BASE_OVERALL_LEAGUE_PATH;
            };
    }

    public static String filterSelectorByChild(String selector, String child) {
        return ":is(%s):has(%s)".formatted(selector, child);
    }

    public static String getSelectorChild(String selector, String child) {
        return ":is(%s) %s".formatted(selector, child);
    }

    public static boolean hasCaptainIcon(Locator el) {
        return el.locator(CAPTAIN_ICON_SELECTOR).first().isVisible();
    }

    public static boolean hasViceIcon(Locator el) {
        return el.locator(VICE_ICON_SELECTOR).first().isVisible();
    }

    public static boolean hasStartSquad(Locator el) {
        return el.locator(ALL_ROLES_PLAYERS_CONTAINER).first().count() > 0;
    }

    public static int getAvailability(Locator el) {
        if (el.locator(FOR_100PC_PLAYER_SELECTOR).first().isVisible()) return 100;
        if (el.locator(FOR_75PC_PLAYER_SELECTOR).first().isVisible()) return 75;
        if (el.locator(FOR_50PC_PLAYER_SELECTOR).first().isVisible()) return 50;
        if (el.locator(FOR_25PC_PLAYER_SELECTOR).first().isVisible()) return 25;

        return 0;
    }
}
