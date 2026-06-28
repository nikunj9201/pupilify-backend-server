# Set JAVA_HOME
$env:JAVA_HOME = 'C:\Users\nikun\.jdks\ms-21.0.9'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "================================" -ForegroundColor Green
Write-Host "SMS Backend Application Starter" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""

# Kill existing Java processes
Write-Host "Step 1: Stopping any existing processes..." -ForegroundColor Yellow
Get-Process | Where-Object {$_.ProcessName -eq "java"} | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

# Check port
Write-Host "Step 2: Checking port 8080..." -ForegroundColor Yellow
$port = netstat -ano 2>$null | Select-String ":8080"
if ($port) {
    Write-Host "Port 8080 is still in use! Forcing kill..." -ForegroundColor Red
    $pid = $port[0].ToString().Split()[-1]
    taskkill /PID $pid /F 2>$null
    Start-Sleep -Seconds 2
} else {
    Write-Host "Port 8080 is FREE ✓" -ForegroundColor Green
}

# Start application
Write-Host ""
Write-Host "Step 3: Starting application..." -ForegroundColor Yellow
Write-Host "Java Home: $env:JAVA_HOME" -ForegroundColor Cyan
Write-Host "Working Directory: C:\Users\nikun\Downloads\sms-backend (1)\sms-backend\sms-backend" -ForegroundColor Cyan
Write-Host ""

cd "C:\Users\nikun\Downloads\sms-backend (1)\sms-backend\sms-backend"

# Run JAR
& "$env:JAVA_HOME\bin\java.exe" -jar target\sms-backend-0.0.1-SNAPSHOT.jar

Write-Host ""
Write-Host "Application stopped" -ForegroundColor Red

