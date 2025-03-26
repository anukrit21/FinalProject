@echo off
SETLOCAL EnableDelayedExpansion

REM Color definitions
SET BLUE=[94m
SET GREEN=[92m
SET RED=[91m
SET YELLOW=[93m
SET NC=[0m

ECHO %GREEN%DemoApp Package Name Standardization Script%NC%
ECHO ----------------------------------------

REM Get the directory where this script is located
SET "SCRIPT_DIR=%~dp0"
CD /d "%SCRIPT_DIR%"

REM Define patterns to replace
SET "PATTERN_LOWER=com.demoapp."
SET "PATTERN_CORRECT=com.demoApp."
SET "GROUP_ID_LOWER=<groupId>com.demoapp</groupId>"
SET "GROUP_ID_CORRECT=<groupId>com.demoApp</groupId>"

ECHO %BLUE%This script will update package names and Maven groupIds to use the correct format: %PATTERN_CORRECT%%NC%
ECHO %RED%Warning: This operation will modify source files. Make sure you have a backup.%NC%
ECHO.

REM Ask for confirmation
SET /P CONFIRM=Do you want to continue? (Y/N): 
IF /I NOT "%CONFIRM%"=="Y" (
    ECHO %YELLOW%Operation cancelled.%NC%
    EXIT /B 0
)

ECHO.
ECHO %BLUE%Scanning for Java files...%NC%

REM Create a temporary directory for storing backup files
IF NOT EXIST "backup" MKDIR "backup"
IF NOT EXIST "backup\java" MKDIR "backup\java"
IF NOT EXIST "backup\xml" MKDIR "backup\xml"

REM Use PowerShell to find all Java files and process them
powershell -Command "Get-ChildItem -Path . -Recurse -Filter *.java | ForEach-Object { $content = Get-Content $_.FullName -Raw; if ($content -match 'package\s+com\.demoapp\.') { $newContent = $content -replace 'package\s+com\.demoapp\.', 'package com.demoApp.'; Copy-Item $_.FullName -Destination ('backup\java\' + $_.Name); Set-Content -Path $_.FullName -Value $newContent -NoNewline; Write-Host ('Updated Java file: ' + $_.FullName) } }"

ECHO.
ECHO %BLUE%Scanning for Maven pom.xml files...%NC%

REM Use PowerShell to find all pom.xml files and process them
powershell -Command "Get-ChildItem -Path . -Recurse -Filter pom.xml | ForEach-Object { $content = Get-Content $_.FullName -Raw; if ($content -match '<groupId>com\.demoapp</groupId>') { $newContent = $content -replace '<groupId>com\.demoapp</groupId>', '<groupId>com.demoApp</groupId>'; Copy-Item $_.FullName -Destination ('backup\xml\' + $_.Name + '.' + (Get-Date -Format 'yyyyMMddHHmmss')); Set-Content -Path $_.FullName -Value $newContent -NoNewline; Write-Host ('Updated XML file: ' + $_.FullName) } }"

ECHO.
ECHO %GREEN%Package name standardization completed!%NC%
ECHO Original files have been backed up to the 'backup' directory.
ECHO.
ECHO Note: You may need to rebuild your project to apply the changes.
ECHO. 