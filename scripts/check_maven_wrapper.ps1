<#
check_maven_wrapper.ps1

Purpose: Diagnose the Maven wrapper error you observed and optionally repair the missing wrapper files.

Usage examples (PowerShell):
  # dry run diagnostic
  .\scripts\check_maven_wrapper.ps1

  # attempt to auto-fix missing wrapper files (requires internet access)
  .\scripts\check_maven_wrapper.ps1 -Fix

  # attempt fallback build using system mvn if available
  .\scripts\check_maven_wrapper.ps1 -RunBuild

Notes:
- The script checks for:
  - java and javac availability and versions
  - $Env:JAVA_HOME and where java is found
  - existence of .mvn\wrapper\maven-wrapper.properties and maven-wrapper.jar
  - connectivity to repo1.maven.org (Maven Central)
- If you pass -Fix the script will try to download a known takari maven-wrapper jar and write a basic maven-wrapper.properties.
  If your network blocks downloads, the auto-fix will fail and the script will print manual instructions.
#>

[CmdletBinding()]
param(
    [switch]$Fix,
    [switch]$RunBuild
)

function Write-Info { param($m) Write-Host "[INFO]   $m" -ForegroundColor Cyan }
function Write-Warn { param($m) Write-Host "[WARN]   $m" -ForegroundColor Yellow }
function Write-OK   { param($m) Write-Host "[OK]     $m" -ForegroundColor Green }
function Write-Err  { param($m) Write-Host "[ERROR]  $m" -ForegroundColor Red }

Push-Location (Get-Location)
try {
    $root = Resolve-Path -Path "." -ErrorAction Stop
    Write-Info "Project root: $root"

    Write-Info "Checking Java runtime..."
    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
    if ($null -ne $javaCmd) {
        Write-OK "Found java at: $($javaCmd.Source)"
        try { & java -version 2>&1 | ForEach-Object { Write-Host "    $_" } } catch { Write-Warn "Unable to run java -version: $_" }
    } else {
        Write-Err "java not found in PATH. Ensure JDK/JRE is installed and add to PATH or set JAVA_HOME."
    }

    Write-Info "Checking javac (Java compiler)..."
    $javacCmd = Get-Command javac -ErrorAction SilentlyContinue
    if ($null -ne $javacCmd) {
        Write-OK "Found javac at: $($javacCmd.Source)"
        try { & javac -version 2>&1 | ForEach-Object { Write-Host "    $_" } } catch { Write-Warn "Unable to run javac -version: $_" }
    } else {
        Write-Warn "javac not found in PATH. If you only have a JRE, install a JDK for builds."
    }

    Write-Info "Environment variables:"
    Write-Host "    JAVA_HOME = $Env:JAVA_HOME"
    Write-Host "    PATH contains java? -> $([bool](Get-Command java -ErrorAction SilentlyContinue))"

    # Check mvn wrapper files
    $wrapperDir = Join-Path -Path (Get-Location) -ChildPath ".mvn\wrapper"
    $propsFile = Join-Path $wrapperDir "maven-wrapper.properties"
    $jarFile   = Join-Path $wrapperDir "maven-wrapper.jar"

    $propsExists = Test-Path $propsFile
    $jarExists   = Test-Path $jarFile

    Write-Info ".mvn\wrapper status:"
    Write-Host "    maven-wrapper.properties -> $propsExists"
    Write-Host "    maven-wrapper.jar       -> $jarExists"

    if (-not $propsExists -or -not $jarExists) {
        Write-Warn "Maven wrapper files are missing or incomplete. This will cause the error you saw (ClassNotFoundException for MavenWrapperMain)."
    } else {
        Write-OK "Wrapper files exist. The error may be something else (check the wrapper script output)."
    }

    # Check mvnw/mvnw.cmd presence
    $mvnw = Join-Path (Get-Location) "mvnw"
    $mvnwCmd = Join-Path (Get-Location) "mvnw.cmd"
    Write-Info "Wrapper scripts in project root:"
    Write-Host "    mvnw -> $(Test-Path $mvnw)"
    Write-Host "    mvnw.cmd -> $(Test-Path $mvnwCmd)"

    # Check system mvn
    $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($null -ne $mvnCmd) {
        Write-OK "Found system mvn at: $($mvnCmd.Source)"
        try { & mvn -v 2>&1 | ForEach-Object { Write-Host "    $_" } } catch { Write-Warn "Unable to run mvn -v: $_" }
    } else {
        Write-Warn "System 'mvn' not found in PATH. If wrapper cannot be repaired, install Maven or use an IDE with Maven built-in."
    }

    # Check connectivity to Maven Central
    Write-Info "Testing connectivity to Maven Central (repo1.maven.org)..."
    $reachable = $false
    try {
        # Use TcpClient ping as an alternative to Head request if Invoke-WebRequest restricted
        $uri = 'repo1.maven.org'
        $tcp = New-Object System.Net.Sockets.TcpClient
        $async = $tcp.BeginConnect($uri, 443, $null, $null)
        $wait = $async.AsyncWaitHandle.WaitOne(5000) # 5s timeout
        if ($wait) { $tcp.EndConnect($async); $tcp.Close(); $reachable = $true }
    } catch { $reachable = $false }

    if ($reachable) { Write-OK "Network to Maven Central seems reachable (port 443)." } else { Write-Warn "Cannot reach Maven Central (repo1.maven.org:443). If you're behind a proxy or corporate firewall, wrapper auto-download will fail." }

    # If files missing and user asked to fix, attempt to download
    if (($Fix) -and (-not $propsExists -or -not $jarExists)) {
        if (-not $reachable) {
            Write-Err "Network not reachable; cannot auto-download wrapper files. Try from a network that can access Maven Central or use system Maven (see instructions below)."
        } else {
            Write-Info "Attempting to create .mvn\wrapper and download maven-wrapper.jar + write maven-wrapper.properties"
            if (-not (Test-Path $wrapperDir)) { New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null }

            # Download takari wrapper jar (stable known version)
            $jarUrl = 'https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar'
            $propsContent = @"
distributionUrl=https\://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.8.8/apache-maven-3.8.8-bin.zip
wrapperUrl=https\://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar
"@
            try {
                Write-Info "Downloading maven-wrapper.jar from $jarUrl"
                Invoke-WebRequest -Uri $jarUrl -OutFile $jarFile -UseBasicParsing -TimeoutSec 120
                Write-OK "Downloaded maven-wrapper.jar -> $jarFile"
            } catch {
                Write-Err "Failed to download maven-wrapper.jar: $_"
            }

            try {
                Write-Info "Writing maven-wrapper.properties"
                $propsFile | Split-Path | % { if (-not (Test-Path $_)) { New-Item -ItemType Directory -Path $_ -Force | Out-Null } }
                $propsContent | Out-File -FilePath $propsFile -Encoding UTF8 -Force
                Write-OK "Wrote maven-wrapper.properties -> $propsFile"
            } catch {
                Write-Err "Failed to write maven-wrapper.properties: $_"
            }

            if (Test-Path $jarFile -and Test-Path $propsFile) {
                Write-OK "Wrapper files repaired. Try running: .\mvnw.cmd -DskipTests package"
            } else {
                Write-Err "Auto-fix did not complete successfully. See manual instructions below."
            }
        }
    }

    # If RunBuild requested - prefer wrapper if present, else system mvn
    if ($RunBuild) {
        if (Test-Path (Join-Path (Get-Location) 'mvnw.cmd')) {
            Write-Info "Running project build via wrapper (mvnw.cmd)..."
            try {
                & .\mvnw.cmd -DskipTests package
                Write-OK "Wrapper build completed (check output above)."
            } catch {
                Write-Err "Wrapper build failed: $_"
            }
        } elseif ($null -ne $mvnCmd) {
            Write-Info "Running project build via system mvn..."
            try {
                & mvn -DskipTests package
                Write-OK "System mvn build completed (check output above)."
            } catch {
                Write-Err "System mvn build failed: $_"
            }
        } else {
            Write-Err "No build tool available: wrapper invalid and system mvn not found. Install Maven or repair wrapper."
        }
    }

    Write-Host "`n--- Summary / Manual next steps ---`n"
    if (-not (Test-Path $propsFile) -or -not (Test-Path $jarFile)) {
        Write-Host "1) If auto-fix failed, you can manually download two files into the project:"
        Write-Host "   - $jarUrl  -> save as .mvn\wrapper\maven-wrapper.jar"
        Write-Host "   - Create .mvn\wrapper\maven-wrapper.properties with content similar to:"
        Write-Host "     distributionUrl=https://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.8.8/apache-maven-3.8.8-bin.zip"
        Write-Host "   (Adjust Maven version if you need a specific one.)"
        Write-Host "2) Alternatively, install system Maven and run: mvn -DskipTests package"
        Write-Host "   - Download Maven: https://maven.apache.org/download.cgi"
        Write-Host "   - Unzip and set PATH to include the Maven 'bin' folder or use an IDE's Maven support."
    } else {
        Write-Host "Wrapper files present. Try: .\mvnw.cmd -DskipTests package"
    }

} finally {
    Pop-Location
}

# exit with 0; script prints errors with color but not nonzero exit codes so you can inspect.
exit 0
