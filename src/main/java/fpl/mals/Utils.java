package fpl.mals;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static org.apache.commons.collections4.ListUtils.partition;

public class Utils {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    private static final String PLAYER_SELECTOR_FOR_ALL = "[class*=\"_174gkcl\"]";
    private static final String PLAYER_SELECTOR_FOR_100PC_CHANCE = "._174gkcl5";
    private static final String PLAYER_SELECTOR_FOR_75PC_CHANCE = "._174gkcl4";
    private static final String PLAYER_SELECTOR_FOR_50PC_CHANCE = "._174gkcl3";
    private static final String PLAYER_SELECTOR_FOR_25PC_CHANCE = "._174gkcl2";
    private static final String PLAYER_SELECTOR_FOR_0PC_CHANCE = "._174gkcl1";

    private static final String BASE_URL = "https://fantasy.premierleague.com";
    private static final String BASE_OVERALL_LEAGUE_PATH = "/leagues/314/standings/c";
    private static final String RECORD_LINK_SELECTOR = "a._1jqkqxq4";
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String BOLD = "\u001B[1m";
    private static final String DESCRIPTION_FOR_ENTER_PAGE_NUMBER = CYAN + """
            =====================================================================
             ⚽ FPL SCRAPER
            =====================================================================
            """ + RESET + """                         
            Every standings page displays names of 50 teams.
            Page #1: 1-50 position
            Page #2: 51-100 position
            ...
            You choose the number of standing pages starting from #1 onwards.
            That is, if you enter, for example, 4 pages,
            it means viewing teams occupying positions 1 through 200.
            """ + CYAN + """
            =====================================================================
            """ + RESET + BOLD + """
            Enter the number of pages to parse (0 - exit):\s""" + RESET;

    private static final String DESCRIPTION_FOR_CHOOSE_THREAD_MODE = CYAN + """
            ==========================================
                          SCRAPING MODES
            ==========================================
            """ + RESET +
            GREEN + " 1 " + RESET + "- Single-threaded mode\n" +
            GREEN + " 2 " + RESET + "- Multi-threaded mode by Browser pool\n" +
            CYAN + "==========================================\n" + RESET +
            BOLD + "Choose thread mode: " + RESET;

    private static final String DESCRIPTION_FOR_CHOOSE_PLAYER_SELECTOR = CYAN + """
            =======================================================
                         PLAYER FILTER BY AVAILABILITY
            =======================================================
            """ + RESET +
            GREEN + " 1 " + RESET + "- All players\n" +
            GREEN + " 2 " + RESET + "- Available to play\n" +
            GREEN + " 3 " + RESET + "- All with limited availability\n" +
            GREEN + " 4 " + RESET + "- 75% chance of playing only\n" +
            GREEN + " 5 " + RESET + "- 50% chance of playing only\n" +
            GREEN + " 6 " + RESET + "- 25% chance of playing only\n" +
            GREEN + " 7 " + RESET + "- Unavailable to play\n" +
            GREEN + " 8 " + RESET + "- Doubtful, unlikely or unavailable to play (0-50%)\n" +
            CYAN + "=======================================================\n" + RESET +
            BOLD + "Choose a filter: " + RESET;

    public static String getFullUrl(String urlEnd) {
        return BASE_URL + urlEnd;
    }

    public static String getStandingsPageUrl(int pageNumber) {
        return getFullUrl(BASE_OVERALL_LEAGUE_PATH + "?page_standings=" + pageNumber);
    }

    public static void terminateProgramIfNeeded(int pageNumber) throws InterruptedException {
        if (pageNumber == 0) {
            logger.info("Program terminated. Good luck!");
            Thread.sleep(3000);
            System.exit(0);
        }
    }

    public static int getEnteredNumber(String description, int min, int max) {
        int result;
        while (true) {
            System.out.print(description);

            if (scanner.hasNextInt()) {
                result = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                if (result >= min && result <= max) {
                    break;
                } else {
                    System.out.printf("⚠️ Error: the number must be between %d and %d%n", min, max);
                }
            } else {
                System.out.println("⚠️ Error: a number is required!");
                scanner.nextLine();
            }
        }
        return result;
    }

    public static int getEnteredPageCount() {
        return getEnteredNumber(DESCRIPTION_FOR_ENTER_PAGE_NUMBER, 0, 20);
    }

    public static int getThreadMode() {
        return getEnteredNumber(DESCRIPTION_FOR_CHOOSE_THREAD_MODE, 1, 2);
    }

    public static String getPlayerSelector() {
        int filterType = getEnteredNumber(DESCRIPTION_FOR_CHOOSE_PLAYER_SELECTOR, 1, 8);

        return switch (filterType) {
            case 1 -> {
                System.out.println("""
                        ✅ Your choice - All players!
                        ℹ️  Includes all players regardless of status.
                        """);
                yield PLAYER_SELECTOR_FOR_ALL;
            }
            case 2 -> {
                System.out.println("""
                        ✅ Your choice - Available to play!
                        ℹ️  Players who are fully fit and expected to play.
                        """);
                yield PLAYER_SELECTOR_FOR_100PC_CHANCE;
            }
            case 3 -> {
                System.out.println("""
                        ✅ Your choice - All with limited availability!
                        ℹ️  Players who are not fully fit or unavailable.
                        """);
                yield String.join(", ",
                        PLAYER_SELECTOR_FOR_75PC_CHANCE,
                        PLAYER_SELECTOR_FOR_50PC_CHANCE,
                        PLAYER_SELECTOR_FOR_25PC_CHANCE,
                        PLAYER_SELECTOR_FOR_0PC_CHANCE
                );
            }
            case 4 -> {
                System.out.println("""
                        ✅ Your choice - 75% chance of playing only!
                        ℹ️  Players likely to play, but not guaranteed.
                        """);
                yield PLAYER_SELECTOR_FOR_75PC_CHANCE;
            }
            case 5 -> {
                System.out.println("""
                        ✅ Your choice - 50% chance of playing only!
                        ℹ️  Doubtful players — 50/50 chance of participation.
                        """);
                yield PLAYER_SELECTOR_FOR_50PC_CHANCE;
            }
            case 6 -> {
                System.out.println("""
                        ✅ Your choice - 25% chance of playing only!
                        ℹ️  Players with a low probability of appearing.
                        """);
                yield PLAYER_SELECTOR_FOR_25PC_CHANCE;
            }
            case 7 -> {
                System.out.println("""
                        ✅ Your choice - Unavailable to play!
                        ℹ️  Injured, suspended, or otherwise unavailable players.
                        """);
                yield PLAYER_SELECTOR_FOR_0PC_CHANCE;
            }
            case 8 -> {
                System.out.println("""
                        ✅ Your choice - Doubtful, unlikely or unavailable to play (0-50%)!
                        ℹ️  Questionable or unavailable players.
                        """);
                yield String.join(", ",
                        PLAYER_SELECTOR_FOR_50PC_CHANCE,
                        PLAYER_SELECTOR_FOR_25PC_CHANCE,
                        PLAYER_SELECTOR_FOR_0PC_CHANCE
                );
            }
            default -> throw new IllegalArgumentException("Unknown filter type: " + filterType);
        };
    }

    public static List<String> getAllTeamLinks(int pageCount) {
        return IntStream.rangeClosed(1, pageCount)
                .mapToObj(Utils::getStandingsPageUrl)
                .map(Utils::getTeamLinks)
                .flatMap(Collection::stream)
                .toList();
    }

    public static List<String> getTeamLinks(String url) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             BrowserContext context = browser.newContext();
             Page page = context.newPage())
        {
            page.navigate(url);
            Locator links = page.locator(RECORD_LINK_SELECTOR);
            links.first().waitFor();

            return links.all().stream()
                    .map(el -> getFullUrl(el.getAttribute("href")))
                    .toList();
        }
    }

    public static Map<String, Integer> collectPlayers(List<String> teamLinks, int threadMode, String absentPlayer) {
        return switch (threadMode) {
            case 1 -> {
                System.out.println("🐢 Running in single-threaded mode...");
                yield Utils.collectPlayersInSingleThreadMode(teamLinks);
            }
            case 2 -> {
                System.out.println("🚀 Running in multi-threaded mode by Browser pool...");
                yield Utils.collectPlayersConcurrentlyByBrowserPool(teamLinks, absentPlayer);
            }
            default -> throw new IllegalArgumentException("Unknown thread mode: " + threadMode);
        };
    }

    public static Map<String, Integer> collectPlayersInSingleThreadMode(List<String> teamLinks) {
        Map<String, Integer> players = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(0);
        int total = teamLinks.size();
        String playerSelector = getPlayerSelector();

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             BrowserContext context = browser.newContext();
             Page page = context.newPage())
        {
            for (String link : teamLinks) {
                page.navigate(link);
                Locator player = page.locator(playerSelector);
                page.locator(PLAYER_SELECTOR_FOR_ALL).last().waitFor();

                for (Locator el : player.all()) {
                    players.merge(el.innerText().trim(), 1, Integer::sum);
                }

                int done = counter.incrementAndGet();
                System.out.printf("✅ [%d/%d] %s%n", done, total, link);
            }
        }
        System.out.printf("📊 Found %d unique players%n", players.size());

        return players;
    }

    public static Map<String, Integer> collectPlayersConcurrentlyByBrowserPool(List<String> teamLinks, String absentPlayer) {
        Map<String, Integer> players = new ConcurrentHashMap<>();
        AtomicInteger counter = new AtomicInteger(0);
        int total = teamLinks.size();
        String playerSelector = getPlayerSelector();

        int browserCount = Math.min(5, Runtime.getRuntime().availableProcessors());
        logger.info("⏱️ Using " + browserCount + " browser threads!");

        ExecutorService executorServicePool = Executors.newFixedThreadPool(browserCount);
        List<List<String>> partitions = partition(teamLinks, browserCount);
        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        for (var teamSublist : partitions) {
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                try (Playwright playwright = Playwright.create();
                     Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
                     BrowserContext context = browser.newContext())
                {
                    Page page = context.newPage();
                    for (String link : teamSublist) {
                        try {
                            page.navigate(link);
                            page.locator(PLAYER_SELECTOR_FOR_ALL).last().waitFor();

                            Locator player = page.locator(playerSelector);
                            List<Locator> teamPlayers = player.all();

                            boolean hasPlayer = absentPlayer == null;

                            for (Locator el : teamPlayers) {
                                String name = el.innerText().trim();
                                players.merge(name, 1, Integer::sum);

                                if (!hasPlayer && name.equalsIgnoreCase(absentPlayer)) {
                                    hasPlayer = true;
                                }
                            }

                            int done = counter.incrementAndGet();
                            System.out.printf("✅ %d players, [%d/%d] %s%n", teamPlayers.size(), done, total, link);

                            if (!hasPlayer) {
                                System.out.printf("❌ %s is absent in this team%n", absentPlayer);
                            }
                        } catch (Exception e) {
                            logger.warning("⚠️ Error on " + link + ": " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.severe("❌ Browser cluster thread failed: " + e.getMessage());
                }
            }, executorServicePool).exceptionally(e -> {
                logger.severe("❌ Exception in browser thread: " + e.getMessage());
                return null;
            });

            tasks.add(task);
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        executorServicePool.shutdown();
        try {
            if (!executorServicePool.awaitTermination(1, TimeUnit.MINUTES)) {
                executorServicePool.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorServicePool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.printf("📊 Found %d unique players%n", players.size());

        return players;
    }

    public static File getOutputDir(String[] args) {
        String outputDir = Arrays.stream(args)
                .filter(arg -> arg.startsWith("/output=") || arg.startsWith("--output="))
                .findFirst()
                .map(arg -> arg.substring(arg.indexOf('=') + 1).trim())
                .orElse(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "FPLScraper");

        File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            System.err.println("❌ Cannot create output directory: " + outDir.getAbsolutePath());
        }

        return outDir;
    }

    public static void saveResultsToExcel(Map<String, Integer> players, String fileName, String[] args) {
        File file = new File(getOutputDir(args), fileName);
        List<Map.Entry<String, Integer>> sortedPlayers = players.entrySet().stream()
                .sorted(Comparator
                        .comparing(Map.Entry<String, Integer>::getValue).reversed()
                        .thenComparing(Map.Entry::getKey)
                ).toList();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Players");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row header = sheet.createRow(0);

            Cell headerCell0 = header.createCell(0);
            headerCell0.setCellValue("Name");
            headerCell0.setCellStyle(headerStyle);

            Cell headerCell1 = header.createCell(1);
            headerCell1.setCellValue("Count");
            headerCell1.setCellStyle(headerStyle);

            int rowNum = 1;
            for (var entry : sortedPlayers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            logger.severe("❌ Failed to save Excel file: " + e.getMessage());
        }
        logger.info("💾 Excel file saved successfully: " + file.getAbsolutePath());
    }
}
