param(
    [string]$JavaHome = $env:JAVA_HOME,
    [string]$TomcatHome = $env:TOMCAT_HOME,
    [int]$Port = 8080
)

if (-not $JavaHome) {
    $JavaHome = Read-Host "JAVA_HOME not set. Enter full path to JDK to use for this project (e.g. E:\\jdk-24)"
}
if (-not (Test-Path $JavaHome)) {
    Write-Error "JAVA_HOME path '$JavaHome' does not exist. Set a valid JDK path and re-run.";
    exit 1
}

if (-not $TomcatHome) {
    $TomcatHome = Read-Host "TOMCAT_HOME not set. Enter full path to extracted Tomcat directory (e.g. E:\\tomcat9\\apache-tomcat-9.0.x)"
}
if (-not (Test-Path $TomcatHome)) {
    Write-Error "TOMCAT_HOME path '$TomcatHome' does not exist. Install/point to Tomcat and re-run.";
    exit 1
}

Write-Host "Using JAVA_HOME = $JavaHome"
Write-Host "Using TOMCAT_HOME = $TomcatHome"
Write-Host "Using HTTP port = $Port"

# Set environment for this session only
$env:JAVA_HOME = $JavaHome
$env:TOMCAT_HOME = $TomcatHome

# Ensure catalina.bat exists
if (-not (Test-Path (Join-Path $TomcatHome 'bin\catalina.bat'))) {
    Write-Error "catalina.bat not found under TOMCAT_HOME\bin. Check your Tomcat installation.";
    exit 1
}

# Optional: set logging manager to Tomcat's juli implementation (helps with ClassLoaderLogManager issues)
$env:CATALINA_OPTS = '-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager'

Write-Host "Starting Tomcat in console mode (so you can see startup errors). Press Ctrl+C to stop.`n" -ForegroundColor Green
# Run catalina in 'run' mode so logs appear in console. This will block until you Ctrl+C.
& "$TomcatHome\bin\catalina.bat" run

# If catalina.bat fails, check logs under $TomcatHome\logs and paste stacktrace here for further debugging.
Write-Host "Tomcat process exited. Check $TomcatHome\logs for details." -ForegroundColor Yellow
