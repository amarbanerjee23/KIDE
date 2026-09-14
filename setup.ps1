# ========================================================================
# KIDE Enterprise Infrastructure Setup & Runner (PowerShell)
# ========================================================================

[CmdletBinding()]
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$ScriptArgs
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host "     KIDE Enterprise Infrastructure Setup & Runner (PowerShell)" -ForegroundColor Cyan
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Prepend bundled portable Node.js if available
$BundledNode = Join-Path $ScriptDir "node-v20.11.1-win-x64"
if (Test-Path $BundledNode) {
    $env:PATH = "$BundledNode;$($env:PATH)"
}

# 2. Locate working Python 3.10+
$PythonCmd = $null
$VenvPython = Join-Path $ScriptDir "kide-enterprise\backend\.venv\Scripts\python.exe"
if (Test-Path $VenvPython) {
    try {
        & $VenvPython -c "import sys" | Out-Null
        $PythonCmd = $VenvPython
    } catch {}
}

if (-not $PythonCmd) {
    foreach ($cand in @("python", "py", "python3")) {
        if (Get-Command $cand -ErrorAction SilentlyContinue) {
            try {
                & $cand -c "import sys; sys.exit(0 if sys.version_info >= (3, 10) else 1)" | Out-Null
                $PythonCmd = $cand
                break
            } catch {}
        }
    }
}

if (-not $PythonCmd) {
    Write-Host "[ERROR] Python 3.10+ was not found on your system PATH." -ForegroundColor Red
    Write-Host "Please install Python 3.10+ from https://www.python.org/downloads/" -ForegroundColor Yellow
    exit 1
}

# 3. Verify Node.js and npm
if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Node.js was not found on your system PATH." -ForegroundColor Red
    Write-Host "Please install Node.js 18+ from https://nodejs.org/" -ForegroundColor Yellow
    exit 1
}

if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] npm was not found on your system PATH." -ForegroundColor Red
    exit 1
}

$SetupPyPath = Join-Path $ScriptDir "setup.py"

# 4. Launch setup.py with forwarded arguments
& $PythonCmd $SetupPyPath @ScriptArgs

