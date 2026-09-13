@echo off
setlocal enabledelayedexpansion

title KIDE Enterprise Application Launcher

echo ========================================================================
echo                 KIDE Enterprise Application Launcher
echo ========================================================================
echo.

:: 1. Check Python
where python >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=python
    goto :PYTHON_FOUND
)

where py >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=py -3
    goto :PYTHON_FOUND
)

where python3 >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=python3
    goto :PYTHON_FOUND
)

echo [ERROR] Python 3 was not found on your system PATH.
echo Please install Python 3.10+ from https://www.python.org/downloads/
echo Make sure to check "Add python.exe to PATH" during installation.
echo.
pause
exit /b 1

:PYTHON_FOUND
:: 2. Check Node.js and npm
where node >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Node.js was not found on your system PATH.
    echo Please install Node.js 18+ from https://nodejs.org/
    echo.
    pause
    exit /b 1
)

where npm >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] npm was not found on your system PATH.
    echo Please ensure npm is installed and added to PATH.
    echo.
    pause
    exit /b 1
)

:: 3. Run cross-platform runner script
%PYTHON_CMD% "%~dp0run.py" %*

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] KIDE Enterprise terminated with exit code %ERRORLEVEL%.
    pause
)

