param(
    [string]$JavaHome = "C:\Program Files\Java\jdk-24"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $JavaHome)) {
    Write-Error "JAVA_HOME path not found: $JavaHome"
}

$javaExe = Join-Path $JavaHome "bin\java.exe"
if (-not (Test-Path $javaExe)) {
    Write-Error "java.exe not found under: $JavaHome\bin"
}

$env:JAVA_HOME = $JavaHome
if (-not $env:Path.Contains("$JavaHome\bin")) {
    $env:Path = "$JavaHome\bin;$env:Path"
}

Write-Host "JAVA_HOME=$env:JAVA_HOME"
& $javaExe -version

Write-Host "Starting Spring Boot with profile: local"
& ".\mvnw.cmd" spring-boot:run "-Dspring-boot.run.profiles=local"
