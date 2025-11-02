@echo off
echo ====================================
echo   KHOI DONG GIAO DIEN SINH VIEN
echo ====================================
echo.

REM Compile if needed
echo Compiling project...
call mvn clean compile -DskipTests

REM Run Student GUI
echo Starting Student GUI...
start "Student GUI" mvn exec:java -Dexec.mainClass="iuh.fit.se.gui.StudentApp"

echo.
echo Student GUI is starting...
echo.
pause
