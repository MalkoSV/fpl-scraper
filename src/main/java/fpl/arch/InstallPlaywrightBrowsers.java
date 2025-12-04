package fpl.arch;

import com.microsoft.playwright.*;

/**
 * Простий утилітарний клас для одноразової інсталяції браузерів Playwright.
 * <p>
 * Виконує завантаження Chromium у системну теку Playwright (зазвичай: %USERPROFILE%\AppData\Local\ms-playwright).
 * <p>
 * Можна викликати з BAT-скрипта або вручну:
 *   java -cp target\* fpl.mals.InstallPlaywrightBrowsers
 */
public class InstallPlaywrightBrowsers {
    public static void main(String[] args) {
        System.out.println("🌐 Starting Playwright browser installation...");

        try (Playwright playwright = Playwright.create()) {
            playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            System.out.println("✅ Chromium browser installed successfully!");
            System.out.println("✅ Playwright setup complete!");
        } catch (Exception e) {
            System.err.println("❌ Failed to install browsers: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
