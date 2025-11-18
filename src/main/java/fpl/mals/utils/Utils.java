package fpl.mals.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import fpl.mals.Player;
import fpl.mals.Position;
import fpl.mals.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static org.apache.commons.collections4.ListUtils.partition;

public class Utils {

    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    public static void terminateProgramIfNeeded(int pageNumber) throws InterruptedException {
        if (pageNumber == 0) {
            logger.info("❌ Your choice - program terminated. Good luck!");
            Thread.sleep(3000);
            System.exit(0);
        }
    }


    public static int getEnteredPageCount() {
        return InputUtils.getEnteredNumber(InputUtils.DESCRIPTION_FOR_ENTER_PAGE_NUMBER, 0, 20);
    }

    public static List<String> getAllTeamLinks(int pageCount) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             BrowserContext context = browser.newContext();
             Page page = context.newPage())
        {
        return IntStream.rangeClosed(1, pageCount)
                .mapToObj(SelectorUtils::getStandingsPageUrl)
                .map(url -> Utils.getTeamLinks(url, page))
                .flatMap(Collection::stream)
                .toList();
        }
    }

    public static List<String> getTeamLinks(String url, Page page) {
            page.navigate(url);
            Locator links = page.locator(SelectorUtils.RECORD_LINK_SELECTOR);
            links.first().waitFor();

            return links.all().stream()
                    .map(el -> SelectorUtils.getFullUrl(el.getAttribute("href")))
                    .toList();
    }

    public static List<Player> collectPlayers(List<String> teamLinks, String playerSelector) {
        System.out.println("🚀 Running in multi-threaded mode by Browser pool...");

        AtomicInteger counter = new AtomicInteger(0);
        int total = teamLinks.size();

        int browserCount = Math.min(5, Runtime.getRuntime().availableProcessors());
        logger.info("⏱️ Using " + browserCount + " browser threads!");

        ExecutorService executorServicePool = Executors.newFixedThreadPool(browserCount);
        List<List<String>> partitions = partition(teamLinks, browserCount);

        List<CompletableFuture<List<Player>>> tasks = new ArrayList<>();

        for (var teamSublist : partitions) {
            CompletableFuture<List<Player>> task = CompletableFuture.supplyAsync(() -> {
                List<Player> localList = new ArrayList<>();

                try (Playwright playwright = Playwright.create();
                     Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
                     BrowserContext context = browser.newContext())
                {
                    Page page = context.newPage();
                    for (String link : teamSublist) {
                        try {
                            page.navigate(link);
                            page.locator(SelectorUtils.NAME_SELECTOR).last().waitFor();

                            boolean hasCaptain = false;
                            boolean hasVice = false;
                            boolean hasTripleCaptain = page.getByText(SelectorUtils.TRIPLE_CAPTAIN).count() > 0;
                            boolean hasBenchBoost = page.getByText(SelectorUtils.BENCH_BOOST).count() > 0;
                            Locator player = page.locator(playerSelector);
                            List<Locator> teamPlayers = player.all();


                            for (Locator el : teamPlayers) {
                                String name = el.locator(SelectorUtils.NAME_SELECTOR).innerText().trim();
                                int score = Integer.parseInt(el.locator(SelectorUtils.GW_SCORE_SELECTOR).innerText());
                                Player currentPlayer = new Player(name, 1, score);

                                if (hasBenchBoost || SelectorUtils.hasStartSquad(el)) {
                                    currentPlayer.setStart(1);
                                }

                                if (!hasCaptain && SelectorUtils.hasCaptainIcon(el)) {
                                    hasCaptain = true;
                                    currentPlayer.setCaptain(1);
                                    if (hasTripleCaptain) {
                                        currentPlayer.setTripleCaptain(1);
                                    }
                                }

                                if (!hasVice && SelectorUtils.hasViceIcon(el)) {
                                    hasVice = true;
                                    currentPlayer.setVice(1);
                                }

                                localList.add(currentPlayer);
                            }

                            int done = counter.incrementAndGet();
                            System.out.printf("✅ %d players, [%d/%d] %s%n", teamPlayers.size(), done, total, link);
                        } catch (Exception e) {
                            logger.warning("⚠️ Error on " + link + ": " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.severe("❌ Browser cluster thread failed: " + e.getMessage());
                }
                return localList;
            }, executorServicePool);

            tasks.add(task);
        }

        List<Player> allPlayers = tasks.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        executorServicePool.shutdown();
        try {
            if (!executorServicePool.awaitTermination(1, TimeUnit.MINUTES)) {
                executorServicePool.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorServicePool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        List<Player> mergedPlayers = PlayerUtils.mergePlayers(allPlayers);
        System.out.printf("📊 Found %d unique players%n", mergedPlayers.size());

        return mergedPlayers;
    }

    public static List<Team> collectStats(List<String> teamLinks) {
        AtomicInteger counter = new AtomicInteger(0);
        int total = teamLinks.size();

        int browserCount = Math.min(5, Runtime.getRuntime().availableProcessors());
        logger.info("⏱️ Using " + browserCount + " browser threads!");

        ExecutorService executorServicePool = Executors.newFixedThreadPool(browserCount);
        List<List<String>> partitions = partition(teamLinks, browserCount);

        List<CompletableFuture<List<Team>>> tasks = new ArrayList<>();

        for (var teamsSublist : partitions) {
            CompletableFuture<List<Team>> task = CompletableFuture.supplyAsync(() -> {
                List<Team> localList = new ArrayList<>();

                try (Playwright playwright = Playwright.create();
                     Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
                     BrowserContext context = browser.newContext();
                     Page page = context.newPage())
                {
                    for (String link : teamsSublist) {
                        try {
                            long startTime = System.currentTimeMillis();

                            page.navigate(link);
                            page.locator(SelectorUtils.NAME_SELECTOR).last().waitFor();

                            boolean foundCaptain = false;
                            boolean foundVice = false;

                            String teamName = page.locator(SelectorUtils.TEAM_NAME_SELECTOR).innerText();
                            String teamPosition = page.locator(SelectorUtils.TEAM_POSITION_SELECTOR).innerText();
                            boolean hasTripleCaptain = page.getByText(SelectorUtils.TRIPLE_CAPTAIN).count() > 0;
                            boolean hasBenchBoost = page.getByText(SelectorUtils.BENCH_BOOST).count() > 0;
                            int freeHit = page.getByText(SelectorUtils.FREE_HIT).count();
                            int wildcard = page.getByText(SelectorUtils.WILDCARD).count();

                            Map<Position, List<Player>> playersByPosition = new EnumMap<>(Position.class);
                            for (Position pos : Position.values()) {
                                playersByPosition.put(pos, new ArrayList<>());
                            }

                            Map<Position, Locator> locatorsByPosition = Map.of(
                                    Position.GOALKEEPER, page.locator(SelectorUtils.GOALKEEPER_LINE_PLAYER_SELECTOR),
                                    Position.DEFENDER, page.locator(SelectorUtils.DEFENDER_LINE_PLAYER_SELECTOR),
                                    Position.MIDFIELDER, page.locator(SelectorUtils.MIDFIELDER_LINE_PLAYER_SELECTOR),
                                    Position.OFFENDER, page.locator(SelectorUtils.OFFENDER_LINE_PLAYER_SELECTOR),
                                    Position.BENCH, page.locator(SelectorUtils.BENCH_SELECTOR)
                            );

                            int totalTeamPlayers = 0;

                            for (Position pos : Position.values()) {
                                List<Player> playerslist = playersByPosition.get(pos);
                                Locator player = locatorsByPosition.get(pos);

                                List<Locator> teamPlayers = player.all();
                                for (Locator el : teamPlayers) {
                                    String name = el.locator(SelectorUtils.NAME_SELECTOR).innerText().trim();
                                    int score = Integer.parseInt(el.locator(SelectorUtils.GW_SCORE_SELECTOR).innerText());
                                    Player currentPlayer = new Player(name, 1, score);

                                    if (hasBenchBoost || SelectorUtils.hasStartSquad(el)) {
                                        currentPlayer.setStart(1);
                                    }

                                    if (!foundCaptain && SelectorUtils.hasCaptainIcon(el)) {
                                        foundCaptain = true;
                                        currentPlayer.setCaptain(1);
                                        if (hasTripleCaptain) {
                                            currentPlayer.setTripleCaptain(1);
                                        }
                                    }

                                    if (!foundVice && SelectorUtils.hasViceIcon(el)) {
                                        foundVice = true;
                                        currentPlayer.setVice(1);
                                    }

                                    playerslist.add(currentPlayer);
                                    totalTeamPlayers++;
                                }
                            }

                            Team currentTeam = new Team(
                                    teamName,
                                    teamPosition,
                                    hasTripleCaptain ? 1 : 0,
                                    wildcard,
                                    hasBenchBoost ? 1 : 0,
                                    freeHit,
                                    playersByPosition.get(Position.GOALKEEPER),
                                    playersByPosition.get(Position.DEFENDER),
                                    playersByPosition.get(Position.MIDFIELDER),
                                    playersByPosition.get(Position.OFFENDER),
                                    playersByPosition.get(Position.BENCH)
                            );

                            localList.add(currentTeam);
                            int done = counter.incrementAndGet();
                            System.out.printf("✅ %d players, [%d/%d] (in %d sec) %s%n", totalTeamPlayers, done, total,
                                    (System.currentTimeMillis() - startTime) / 1000, link);

                        } catch (Exception e) {
                            logger.warning("⚠️ Error on " + link + ": " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.severe("❌ Browser cluster thread failed: " + e.getMessage());
                }
                return localList;
            }, executorServicePool);

            tasks.add(task);
        }

        List<Team> allTeamsList = tasks.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        executorServicePool.shutdown();
        try {
            if (executorServicePool.awaitTermination(1, TimeUnit.MINUTES)) {
                executorServicePool.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorServicePool.shutdownNow();
            Thread.currentThread().interrupt();
        }

//        List<Player> mergedPlayers = PlayerUtils.mergePlayers(allTeamsList);
//        System.out.printf("📊 Found %d unique players%n", mergedPlayers.size());

        return allTeamsList;
    }

    public static List<Player> getFullPlayerListFromTeams(List<Team> teams) {
        return teams.stream()
                .flatMap(Team::streamPlayers)
                .toList();
    }

}
