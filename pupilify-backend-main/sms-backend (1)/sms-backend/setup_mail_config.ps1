# JavaMailSender Configuration Setup Script for PowerShell
# यह script mail environment variables set करता है

Write-Host "==========================================" -ForegroundColor Green
Write-Host "JavaMailSender Setup Script" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""

# Check current values
$currentUsername = [Environment]::GetEnvironmentVariable("MAIL_USERNAME", "User")
$currentPassword = [Environment]::GetEnvironmentVariable("MAIL_PASSWORD", "User")

if ($currentUsername) {
    Write-Host "MAIL_USERNAME already set: $currentUsername" -ForegroundColor Yellow
} else {
    Write-Host "MAIL_USERNAME not set" -ForegroundColor Red
}

Write-Host ""

# Prompt for credentials
$mailUsername = Read-Host "Enter your Gmail/Email address"
$mailPassword = Read-Host "Enter your Gmail App Password (16 characters)"

Write-Host ""
Write-Host "Setting environment variables..." -ForegroundColor Cyan

# Set environment variables for current user
[Environment]::SetEnvironmentVariable("MAIL_USERNAME", $mailUsername, "User")
[Environment]::SetEnvironmentVariable("MAIL_PASSWORD", $mailPassword, "User")

Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "Setup Complete!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Write-Host "MAIL_USERNAME = $mailUsername" -ForegroundColor Green
Write-Host "MAIL_PASSWORD = (hidden for security)" -ForegroundColor Green
Write-Host ""
Write-Host "Important: Please restart your IDE/PowerShell for changes to take effect!" -ForegroundColor Yellow
Write-Host ""
Write-Host "For Gmail:" -ForegroundColor Cyan
Write-Host "  - Generate App Password from: https://myaccount.google.com/apppasswords" -ForegroundColor Cyan
Write-Host "  - Use 16-character password, NOT your regular password" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

