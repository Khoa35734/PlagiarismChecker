# Minimal helper: detects common project types and runs basic install + start steps.
param()

$root = Split-Path -Path $MyInvocation.MyCommand.Path -Parent
Set-Location $root

Write-Host "Detecting project type in $root ..." -ForegroundColor Cyan

if (Test-Path ".\package.json") {
    Write-Host "Node project detected." -ForegroundColor Green
    if (Get-Command npm -ErrorAction SilentlyContinue) {
        Write-Host "Running: npm ci" -ForegroundColor Yellow
        npm ci
        Write-Host "Running: npm start (if available)" -ForegroundColor Yellow
        npm start
    } else {
        Write-Host "Node/npm not found. Install Node.js and try again." -ForegroundColor Red
    }
    exit
}

if (Test-Path ".\requirements.txt" -or Test-Path ".\pyproject.toml" -or Test-Path ".\manage.py") {
    Write-Host "Python project detected." -ForegroundColor Green
    if (Get-Command python -ErrorAction SilentlyContinue) {
        Write-Host "Creating virtual environment .venv" -ForegroundColor Yellow
        python -m venv .venv
        Write-Host "Activating .venv and installing requirements" -ForegroundColor Yellow
        & .\.venv\Scripts\Activate
        if (Test-Path ".\requirements.txt") {
            pip install -r requirements.txt
        } elseif (Test-Path ".\pyproject.toml") {
            pip install -U pip
            pip install build
        }
        # Try common entry points
        if (Test-Path ".\manage.py") {
            Write-Host "Running Django dev server: python manage.py runserver" -ForegroundColor Yellow
            python manage.py runserver
        } else {
            Write-Host "Try running the project's main file (e.g. python main.py)" -ForegroundColor Yellow
        }
    } else {
        Write-Host "Python not found. Install Python and try again." -ForegroundColor Red
    }
    exit
}

if (Test-Path ".\pom.xml") {
    Write-Host "Maven/Java project detected." -ForegroundColor Green
    if (Get-Command mvn -ErrorAction SilentlyContinue) {
        Write-Host "Running: mvn spring-boot:run" -ForegroundColor Yellow
        mvn spring-boot:run
    } else {
        Write-Host "Maven not found. Install Maven and a JDK." -ForegroundColor Red
    }
    exit
}

if (Get-ChildItem -Filter *.sln -ErrorAction SilentlyContinue | Measure-Object | Select-Object -ExpandProperty Count) {
    Write-Host ".NET solution detected." -ForegroundColor Green
    if (Get-Command dotnet -ErrorAction SilentlyContinue) {
        Write-Host "Running: dotnet restore" -ForegroundColor Yellow
        dotnet restore
        Write-Host "Running: dotnet run (first project)" -ForegroundColor Yellow
        dotnet run
    } else {
        Write-Host ".NET SDK not found. Install the .NET SDK." -ForegroundColor Red
    }
    exit
}

Write-Host "No known project files detected. Inspect the repository and run the appropriate commands manually." -ForegroundColor Red
Write-Host "See RUN_INSTRUCTIONS.md for manual steps." -ForegroundColor Cyan

