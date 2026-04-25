@echo off
chcp 65001 >nul
title Weblog Project Starter

echo ========================================
echo   Weblog Blog Project - Startup Script
echo ========================================
echo.

set PROJECT_ROOT=%~dp0
set BACKEND_DIR=%PROJECT_ROOT%weblog-springboot
set FRONTEND_DIR=%PROJECT_ROOT%weblog-vue3\weblog-vue3

echo [1/3] Checking project directories...
if not exist "%BACKEND_DIR%" (
    echo [ERROR] Backend directory not found: %BACKEND_DIR%
    pause
    exit /b 1
)

if not exist "%FRONTEND_DIR%" (
    echo [ERROR] Frontend directory not found: %FRONTEND_DIR%
    pause
    exit /b 1
)

echo [OK] Directory check passed
echo.

echo [2/3] Starting Backend (Spring Boot)...
echo [INFO] Backend will start in a new window. First run needs compilation, please wait...
start "Weblog-Backend" cmd /k "cd /d "%BACKEND_DIR%" && java -jar weblog-web\target\weblog-web-0.0.1-SNAPSHOT.jar"

echo [OK] Backend startup command executed
echo.

timeout /t 3 /nobreak > nul

echo [3/3] Starting Frontend (Vue3)...
echo [INFO] Frontend will start in a new window...
start "Weblog-Frontend" cmd /k "cd /d "%FRONTEND_DIR%" && npm run dev"

echo [OK] Frontend startup command executed
echo.

echo ========================================
echo   Startup Complete!
echo ========================================
echo.
echo Access URLs:
echo   - Frontend: http://localhost:5173 (or other port assigned by Vite)
echo   - Backend API: http://localhost:8080
echo   - API Docs: http://localhost:8080/doc.html
echo.
echo Notes:
echo   1. Make sure MySQL database is running
echo   2. Make sure database 'weblog-cat' is created
echo   3. First run may take longer for Maven compilation
echo   4. To stop services, run stop-all.bat
echo.
echo   - If you see "npm" is not recognized, please install Node.js first
echo   - If you see "java" is not recognized, please install JDK 8+ first
echo.
pause
