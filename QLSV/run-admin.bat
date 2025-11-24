@echo off
echo ====================================
echo   KHOI DONG GIAO DIEN ADMIN
echo ====================================
echo.

REM Compile if needed
echo Compiling project...
call mvn clean compile -DskipTests

REM Run Admin GUI
echo Starting Admin GUI...
start "Admin GUI" mvn exec:java -Dexec.mainClass="iuh.fit.se.gui.AdminApp"

echo.
echo Admin GUI is starting...
echo.
pause

