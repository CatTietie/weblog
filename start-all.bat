@echo off
chcp 65001 >nul
title Weblog Project Restart

echo ========================================
echo   Weblog Blog Project - Restart Script
echo ========================================
echo.

set PROJECT_ROOT=%~dp0
set BACKEND_DIR=%PROJECT_ROOT%weblog-springboot
set FRONTEND_DIR=%PROJECT_ROOT%weblog-vue3\weblog-vue3
set JAR_FILE=%BACKEND_DIR%\weblog-web\target\weblog-web-0.0.1-SNAPSHOT.jar

echo [1/5] Stopping existing processes...
echo.

REM Stop Frontend
echo [1/5.1] Stopping Frontend (Vue3)...
taskkill /f /im node.exe >nul 2>&1
if %errorlevel% equ 0 (
    echo   [OK] Frontend stopped
) else (
    echo   [INFO] No running frontend found
)

REM Stop Backend
echo [1/5.2] Stopping Backend (Spring Boot)...
taskkill /f /im java.exe >nul 2>&1
if %errorlevel% equ 0 (
    echo   [OK] Backend stopped
) else (
    echo   [INFO] No running backend found
)

REM Wait for processes to fully terminate
timeout /t 2 /nobreak >nul
echo.

echo [2/5] Checking project directories...
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

echo [3/5] Checking backend JAR file...
if exist "%JAR_FILE%" (
    echo [OK] Found JAR file, skipping build...
) else (
    echo [INFO] JAR not found, building the project first...
    cd /d "%BACKEND_DIR%"
    call mvn clean package -DskipTests -pl weblog-web -am
    if errorlevel 1 (
        echo [ERROR] Build failed! Please check the errors above.
        pause
        exit /b 1
    )
    echo [OK] Build successful
)
echo.

echo [4/5] Starting Backend (Spring Boot)...
echo [INFO] Starting weblog-web JAR in a new window...
start "Weblog-Backend" cmd /k "cd /d "%BACKEND_DIR%\weblog-web\target" && java -jar weblog-web-0.0.1-SNAPSHOT.jar"

echo [OK] Backend startup command executed
echo.

timeout /t 2 /nobreak > nul

echo [5/5] Starting Frontend (Vue3)...
echo [INFO] Frontend will start in a new window...
start "Weblog-Frontend" cmd /k "cd /d "%FRONTEND_DIR%" && npm run dev"

echo [OK] Frontend startup command executed
echo.

echo ========================================
echo   Restart Complete!
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
echo   3. Old processes have been automatically stopped
echo   4. To stop services, run stop-all.bat
echo.
echo   - If you see "npm" is not recognized, please install Node.js first
echo   - If you see "java" is not recognized, please install JDK 8+ first
echo.
pause
