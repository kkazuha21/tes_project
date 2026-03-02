param(
  [string]$JMeterBin = "jmeter",
  [string]$Protocol = "http",
  [string]$Host = "localhost",
  [int]$Port = 8080,
  [int]$Threads = 20,
  [int]$RampUp = 10,
  [int]$Duration = 120,
  [int]$TimeoutMs = 10000,
  [int]$ThinkTimeMs = 200
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$jmx = Join-Path $root "SharedLab_API_LoadTest.jmx"
$outDir = Join-Path $root "results"
$ts = Get-Date -Format "yyyyMMdd_HHmmss"
$jtl = Join-Path $outDir "run_$ts.jtl"
$reportDir = Join-Path $outDir "report_$ts"

New-Item -ItemType Directory -Force -Path $outDir | Out-Null

& $JMeterBin -n -t $jmx -l $jtl -e -o $reportDir `
  -Jprotocol=$Protocol -Jhost=$Host -Jport=$Port `
  -Jthreads=$Threads -JrampUp=$RampUp -Jduration=$Duration `
  -JtimeoutMs=$TimeoutMs -JthinkTimeMs=$ThinkTimeMs

Write-Host "JTL: $jtl"
Write-Host "HTML Report: $reportDir"
