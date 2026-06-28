@echo off
setlocal enabledelayedexpansion

REM Set JAVA_HOME
set JAVA_HOME=C:\Users\nikun\.jdks\ms-21.0.9
set PATH=%JAVA_HOME%\bin;%PATH%

REM Kill any existing Java processes on port 8080
echo Killing any existing processes on port 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080"') do taskkill /PID %%a /F 2>nul

REM Wait a moment
timeout /t 2 /nobreak

REM Run the application
echo Starting application on port 8080...
cd /d "C:\Users\nikun\Downloads\sms-backend (1)\sms-backend\sms-backend"
java -jar target\sms-backend-0.0.1-SNAPSHOT.jar

pause

