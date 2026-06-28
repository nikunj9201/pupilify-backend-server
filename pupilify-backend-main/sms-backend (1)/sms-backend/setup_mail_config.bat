@echo off
REM JavaMailSender Configuration Setup Script for Windows
REM यह script mail environment variables set करता है

echo ==========================================
echo JavaMailSender Setup Script
echo ==========================================
echo.

REM Check if environment variables are already set
if defined MAIL_USERNAME (
    echo MAIL_USERNAME already set: %MAIL_USERNAME%
) else (
    echo MAIL_USERNAME not set
    set /p MAIL_USERNAME="Enter your Gmail/Email address: "
)

if defined MAIL_PASSWORD (
    echo MAIL_PASSWORD already set
) else (
    echo MAIL_PASSWORD not set
    set /p MAIL_PASSWORD="Enter your Gmail App Password (or SMTP password): "
)

echo.
echo Setting environment variables...

REM Set environment variables
setx MAIL_USERNAME "%MAIL_USERNAME%"
setx MAIL_PASSWORD "%MAIL_PASSWORD%"

echo.
echo ==========================================
echo Setup Complete!
echo ==========================================
echo.
echo MAIL_USERNAME = %MAIL_USERNAME%
echo MAIL_PASSWORD = (hidden for security)
echo.
echo Important: Please restart your IDE/Command Line for changes to take effect!
echo.
echo For Gmail:
echo - Generate App Password from: https://myaccount.google.com/apppasswords
echo - Use 16-character password, NOT your regular password
echo.
pause

