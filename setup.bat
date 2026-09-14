@echo off
setlocal enabledelayedexpansion

:: Force working directory to the directory where this script resides
cd /d "%~dp0"

:: Set console title and UTF-8 encoding
title KIDE Enterprise Infrastructure Setup and Runner
chcp 65001 >nul 2>&1
set PYTHONIOENCODING=utf-8

echo ========================================================================
echo         KIDE Enterprise Infrastructure Setup and Runner
echo ========================================================================
echo.

:: 1. Prepend bundled portable Node.js to PATH if present
if exist "%~dp0node-v20.11.1-win-x64\node.exe" (
    set "PATH=%~dp0node-v20.11.1-win-x64;!PATH!"
)

:: 2. Locate a verified, working Python 3 executable
set PYTHON_CMD=

:: Check if backend virtual environment Python exists and executes
if exist "%~dp0kide-enterprise\backend\.venv\Scripts\python.exe" (
    "%~dp0kide-enterprise\backend\.venv\Scripts\python.exe" -c "import sys" >nul 2>&1
    if !ERRORLEVEL! equ 0 (
        set "PYTHON_CMD=%~dp0kide-enterprise\backend\.venv\Scripts\python.exe"
        goto :PYTHON_FOUND
    )
)

:: Check standard python in PATH
python -c "import sys; sys.exit(0 if sys.version_info[0] >= 3 else 1)" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=python
    goto :PYTHON_FOUND
)

:: Check Python launcher py -3
py -3 -c "import sys" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=py -3
    goto :PYTHON_FOUND
)

:: Check python3 in PATH
python3 -c "import sys" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set PYTHON_CMD=python3
    goto :PYTHON_FOUND
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
            goto :PYTHON_FOUND
        )
    )
)

echo [ERROR] No working Python 3.10+ installation found.
echo.
echo Please install Python 3.10+ from https://www.python.org/downloads/
echo Make sure to check the box: "Add python.exe to PATH"
echo.
pause
exit /b 1

:PYTHON_FOUND
:: 3. Launch the deployment infrastructure setup & runner
echo [LAUNCH] Initializing deployment infrastructure setup...
"%PYTHON_CMD%" "%~dp0setup.py" %*

set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% neq 0 (
    echo.
    echo [ERROR] Infrastructure setup terminated with exit code %EXIT_CODE%.
    echo Review the messages above for details.
    echo.
    pause
)
