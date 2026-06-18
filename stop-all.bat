@echo off
chcp 65001 >nul
title Weblog Project Stopper

echo ========================================
echo   Weblog Blog Project - Stop Script
echo ========================================
echo.

echo [1/2] Stopping Frontend (Vue3)...
taskkill /f /im node.exe 2>nul
if %errorlevel% equ 0 (
    echo [OK] Frontend stopped
) else (
    echo [INFO] No running frontend found
)
echo.

echo [2/2] Stopping Backend (Spring Boot)...
taskkill /f /im java.exe 2>nul
if %errorlevel% equ 0 (
    echo [OK] Backend stopped
) else (
    echo [INFO] No running backend found
)
echo.

echo [Optional] Stopping Maven processes...
taskkill /f /im mvn.exe 2>nul
taskkill /f /fi "WINDOWTITLE eq Weblog-*" 2>nul
echo.

echo ========================================
echo   Stop Complete!
echo ========================================
echo.
echo Tips:
echo   - If processes still running, manually close the cmd windows
echo   - Or use Task Manager to end: node.exe, java.exe, mvn.exe
echo.
pause
