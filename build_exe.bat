@echo off
chcp 65001 >nul

@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo      🏗️  FPL Scraper — Build & EXE Creator
echo ===================================================
echo.

REM === 1️⃣ Очистка та складання проєкту через Maven ===
echo 🔹 Building shaded JAR...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ❌ Maven build failed!
    pause
    exit /b 1
)

REM === 2️⃣ Знаходимо jar файл ===
set "JAR_PATH="
for /f "delims=" %%f in ('dir /b /s "target\FplScraper-*.jar" 2^>nul') do set "JAR_PATH=%%f"

if not exist "%JAR_PATH%" (
    echo ❌ JAR not found in target folder!
    dir target
    pause
    exit /b 1
)

echo ✅ Found JAR: %JAR_PATH%
echo.

REM === 3️⃣ Перевіряємо наявність Playwright-браузерів ===
if not exist browsers (
    echo 🌐 Installing Playwright browsers via Java...
    java -cp target\* fpl.arch.InstallPlaywrightBrowsers
) else (
    echo 🟢 Browsers already present, skipping installation.
)

REM === 4️⃣ Копіюємо браузери до ./browsers ===
set "SRC_DIR=%USERPROFILE%\AppData\Local\ms-playwright"
if not exist "%SRC_DIR%" (
    echo ⚠️  Source browsers directory not found: %SRC_DIR%
) else (
    echo 📦 Copying browsers to project folder...
    if not exist browsers mkdir browsers
    xcopy "%SRC_DIR%" browsers /E /I /Y >nul
    echo ✅ Browsers ready.
)

REM === 5️⃣ Створення JRE ===
if not exist jre (
    echo ⚙️ Creating Java runtime image...
    jlink --add-modules java.base,java.logging,java.desktop,java.xml,jdk.zipfs,jdk.unsupported ^
          --output jre
) else (
    echo 🟢 JRE already exists, skipping jlink.
)

REM === 6️⃣ Створення EXE через jpackage ===
echo 🚀 Packaging into EXE...
if not exist dist mkdir dist

jpackage ^
  --name fpl-scraper ^
  --app-version 2025.11 ^
  --input target ^
  --main-jar %JAR_PATH% ^
  --main-class fpl.arch.FplScraper ^
  --type app-image ^
  --icon fpl4.ico ^
  --dest dist ^
  --win-console ^
  --runtime-image .\jre ^
  --resource-dir .\browsers

if errorlevel 1 (
    echo ❌ jpackage failed!
    pause
    exit /b 1
)

echo.
echo ✅ Done! EXE created in dist\fpl-scraper.exe
echo ===================================================
pause
