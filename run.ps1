#!/usr/bin/env pwsh
<#
Simple helper to compile and run the Task API without Maven.
Requires Java 11+ on PATH.

Usage:
  .\run.ps1
#>

$src = Join-Path $PSScriptRoot 'src/main/java'
$out = Join-Path $PSScriptRoot 'out'
Remove-Item -Recurse -Force $out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $out | Out-Null

$files = Get-ChildItem -Path $src -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }

Write-Host "Compiling ${($files.Count)} Java files..."
javac -d $out $files
if ($LASTEXITCODE -ne 0) { Write-Error "javac failed"; exit 1 }

Write-Host "Running server..."
java -cp $out dev.taskapi.ApiServer
