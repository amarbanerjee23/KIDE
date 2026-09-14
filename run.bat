@echo off
setlocal enabledelayedexpansion

:: 1. Force working directory to the directory where this script resides
cd /d "%~dp0"

:: 2. Set console title and UTF-8 encoding
title KIDE Enterprise Application Launcher
chcp 65001 >nul 2>&1
set PYTHONIOENCODING=utf-8

echo ========================================================================
echo                 KIDE Enterprise Application Launcher
echo ========================================================================
echo.

:: 3. Prepend bundled portable Node.js to PATH if present
if exist "%~dp0node-v20.11.1-win-x64\node.exe" (
    set "PATH=%~dp0node-v20.11.1-win-x64;!PATH!"
)

:: 4. Locate a verified, working Python 3 executable
set PYTHON_CMD=

:: Check if backend virtualenv Python already exists and executes
if exist "%~dp0kide-enterprise\backend\.venv\Scripts\python.exe" (
    "%~dp0kide-enterprise\backend\.venv\Scripts\python.exe" -c "import sys" >nul 2>&1
    if !ERRORLEVEL! equ 0 (
        set "PYTHON_CMD=%~dp0kide-enterprise\backend\.venv\Scripts\python.exe"
        goto :VERIFY_NODE
    )
)

:: Check standard python in PATH (verify it executes code and is not just a Store alias)
python -c "import sys; sys.exit(0 if sys.version_info[0] >= 3 else 1)" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=python
    goto :VERIFY_NODE
)

:: Check Python launcher py -3
py -3 -c "import sys" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=py -3
    goto :VERIFY_NODE
)

:: Check python3 in PATH
python3 -c "import sys" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=python3
    goto :VERIFY_NODE
)

:: Search known Python installation directories on Windows
for %%P in (
    "%LOCALAPPDATA%\Programs\Python\Python314\python.exe"
    "%LOCALAPPDATA%\Programs\Python\Python313\python.exe"
    "%LOCALAPPDATA%\Programs\Python\Python312\python.exe"
    "%LOCALAPPDATA%\Programs\Python\Python311\python.exe"
    "%LOCALAPPDATA%\Programs\Python\Python310\python.exe"
    "%LOCALAPPDATA%\Python\bin\python.exe"
    "C:\Python314\python.exe"
    "C:\Python313\python.exe"
    "C:\Python312\python.exe"
    "C:\Python311\python.exe"
    "C:\Python310\python.exe"
    "C:\Program Files\Python314\python.exe"
    "C:\Program Files\Python313\python.exe"
    "C:\Program Files\Python312\python.exe"
    "C:\Program Files\Python311\python.exe"
    "C:\Program Files\Python310\python.exe"
) do (
    if exist "%%~P" (
        "%%~P" -c "import sys" >nul 2>&1
        if !ERRORLEVEL! equ 0 (
            set "PYTHON_CMD=%%~P"
            goto :VERIFY_NODE
        )
    )
)

echo [ERROR] No working Python 3.10+ installation found.
echo.
echo Please install Python 3 from https://www.python.org/downloads/
echo Make sure to check the box: "Add python.exe to PATH"
echo.
pause
exit /b 1

:VERIFY_NODE
:: 5. Verify Node.js and npm
where node >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Node.js was not found on your system PATH.
    echo Please install Node.js 18+ from https://nodejs.org/
    echo.
    pause
    exit /b 1
)

where npm >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] npm was not found on your system PATH.
    echo Please ensure npm is installed and added to PATH.
    echo.
    pause
    exit /b 1
)

:: 6. Launch KIDE Enterprise via run.py with full argument forwarding
"%PYTHON_CMD%" "%~dp0run.py" %*

set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% neq 0 (
    echo.
    echo [ERROR] KIDE Enterprise terminated with exit code %EXIT_CODE%.
    echo Review the messages above for details.
    echo.
    pause
)
