@echo off
chcp 65001 >nul

@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo           🏗️  FPL Parser — Build EXE
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

REM === Find JAR (prefer shaded first) ===
set "JAR_NAME="

for %%f in (target\FplReportGenerator-*.jar) do (
    if /i not "%%~nxf"=="original-FplReportGenerator-1.0.0.jar" (
        set "JAR_NAME=%%~nxf"
    )
)

if not defined JAR_NAME (
    echo ❌ No real JAR found in /target!
    pause
    exit /b 1
)

echo ✅ Found JAR: %JAR_NAME%
echo.

REM === 5️⃣ Створення JRE ===
if not exist jre (
    echo ⚙️ Creating Java runtime image...
    jlink --add-modules java.base,java.logging,java.desktop,java.xml,java.net.http,jdk.zipfs,jdk.unsupported ^
      --output jre

          --output jre
) else (
    echo 🟢 JRE already exists, skipping jlink.
)

REM === 6️⃣ Створення EXE через jpackage ===
echo 🚀 Packaging into EXE...
if not exist dist mkdir dist

jpackage --name FPL-parser ^
  --app-version 2025.12 ^
  --input target ^
  --main-jar "%JAR_NAME%" ^
  --main-class fpl.app.FplReportGenerator ^
  --type app-image ^
  --icon fpl4.ico ^
  --dest dist ^
  --win-console ^
  --runtime-image .\jre

if errorlevel 1 (
    echo ❌ jpackage failed!
    pause
    exit /b 1
)

echo.
echo ✅ Done! EXE created in dist\FPL-parser
echo ===================================================
pause
