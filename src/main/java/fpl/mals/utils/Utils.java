package fpl.mals.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import fpl.mals.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    public static Map<String, Integer> collectPlayers(List<String> teamLinks, String playerSelector, String absentPlayer) {
        System.out.println("🚀 Running in multi-threaded mode by Browser pool...");
        Map<String, Integer> players = new ConcurrentHashMap<>();
        AtomicInteger counter = new AtomicInteger(0);
        int total = teamLinks.size();
        String playerNameSelector = SelectorUtils.getSelectorChild(playerSelector, SelectorUtils.NAME_SELECTOR);

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
                            page.locator(SelectorUtils.NAME_SELECTOR).last().waitFor();

                            Locator player = page.locator(playerNameSelector);
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

    public static List<Player> collectStats(List<String> teamLinks, String playerSelector) {
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
                            boolean hasTripleCaptain = page.getByText("Triple Captain").count() > 0;
                            boolean hasBenchBoost = page.getByText("Bench Boost").count() > 0;
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
            executorServicePool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            executorServicePool.shutdownNow();
        }

        List<Player> mergedPlayers = PlayerUtils.mergePlayers(allPlayers);
        System.out.printf("📊 Found %d unique players%n", mergedPlayers.size());

        return mergedPlayers;
    }
}
