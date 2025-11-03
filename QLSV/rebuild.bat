@echo off
echo ========================================
echo REBUILDING QLSV PROJECT
echo ========================================
echo.

echo Step 1: Cleaning target directory...
if exist target rmdir /s /q target
echo Target directory cleaned!
echo.

echo Step 2: Compiling project with Maven...
call mvn clean compile
echo.

echo Step 3: Packaging project...
call mvn package -DskipTests
echo.

echo ========================================
echo REBUILD COMPLETE!
echo ========================================
echo.
echo Now you can run: java -jar target\QLSV-1.0-SNAPSHOT.jar
echo Or run App.java from your IDE
echo.
pause

