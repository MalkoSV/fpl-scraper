package fpl.mals.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
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
        int count = InputUtils.getEnteredNumber(InputUtils.DESCRIPTION_FOR_ENTER_PAGE_NUMBER, 0, 22);
        System.out.printf("✅ Your choice - %d%n", count);

        return count;
    }

    public static List<String> collectAllTeamLinks(int pageCount) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             BrowserContext context = browser.newContext();
             Page page = context.newPage())
        {
            int n = pageCount <= 20 ? pageCount : 1;

            return IntStream.rangeClosed(1, n)
                    .mapToObj(i -> SelectorUtils.getStandingsPageUrl(i, pageCount))
                    .map(url -> Utils.getTeamLinksFromPage(url, page))
                    .flatMap(Collection::stream)
                    .toList();
        }
    }

    public static List<String> getTeamLinksFromPage(String url, Page page) {
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            page.waitForSelector(SelectorUtils.RECORD_LINK_SELECTOR);

            return page.locator(SelectorUtils.RECORD_LINK_SELECTOR).all().stream()
                    .map(el -> SelectorUtils.getFullUrl(el.getAttribute("href")))
                    .toList();
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

                            page.navigate(link, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                            page.waitForSelector(SelectorUtils.PLAYER_POINTS_SELECTOR);

                            boolean foundCaptain = false;
                            boolean foundVice = false;

                            String teamName = page.locator(SelectorUtils.TEAM_NAME_SELECTOR).innerText();

                            Locator transfersLocator = page.locator(SelectorUtils.TRANSFERS_COUNT_SELECTOR);
                            int transfers = Integer.parseInt(transfersLocator.innerText());

                            Locator chip = page.locator(SelectorUtils.CHIP_SELECTOR);
                            boolean hasBenchBoost = false;
                            boolean hasTripleCaptain = false;
                            int freeHit = 0;
                            int wildcard = 0;

                            if (chip.first().isVisible()) {
                                hasBenchBoost = chip.getByText(SelectorUtils.BENCH_BOOST).first().isVisible();
                                if (!hasBenchBoost) {
                                    freeHit = chip.getByText(SelectorUtils.FREE_HIT).count();
                                    if (freeHit == 0) {
                                        hasTripleCaptain = chip.getByText(SelectorUtils.TRIPLE_CAPTAIN).first().isVisible();
                                        if (!hasTripleCaptain) {
                                            wildcard = 1;
                                        }
                                    }
                                }
                            }

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

                                    Locator pointsLocator = el.locator(SelectorUtils.PLAYER_POINTS_SELECTOR).first();
                                    int points = pointsLocator.isVisible()
                                            ? Integer.parseInt(pointsLocator.innerText())
                                            : -8888;

                                    Player currentPlayer = new Player(name, 1, points);

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

                                    currentPlayer.setAvailability(SelectorUtils.getAvailability(el));

                                    playerslist.add(currentPlayer);
                                    totalTeamPlayers++;
                                }
                            }

                            Team currentTeam = new Team(
                                    teamName,
                                    hasTripleCaptain ? 1 : 0,
                                    wildcard,
                                    hasBenchBoost ? 1 : 0,
                                    freeHit,
                                    transfers,
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

        List<Team> allTeamList = tasks.stream()
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

        return allTeamList;
    }

}

//                            String teamName = page.evaluate(
//                                    "sel => document.querySelector(sel)?.innerText || ''",
//                                    teamNameSelector
//                            ).toString();


