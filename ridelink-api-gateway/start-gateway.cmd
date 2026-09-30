@echo off
REM ============================================================
REM  start-gateway.cmd  —  RideLink API Gateway startup script
REM  Run from: IT3130-RideLink-integrated\ridelink-api-gateway\
REM ============================================================

echo.
echo  ================================================
echo   RideLink API Gateway — Starting on port 8080
echo  ================================================
echo.
echo  Make sure the four microservices are running:
echo    Account Service  :8081
echo    Driver Service   :8082
echo    Ride Service     :8083
echo    Fare/Payment Svc :8084
echo.
echo  Press Ctrl+C to stop the gateway.
echo.

REM If mvnw.cmd is not present, copy it from a sibling service (one-time setup)
IF NOT EXIST "mvnw.cmd" (
    echo  [INFO] mvnw.cmd not found. Copying from account-service...
    copy "..\account-service\mvnw.cmd" "mvnw.cmd" >nul 2>&1
    copy "..\account-service\mvnw" "mvnw" >nul 2>&1
    echo  [INFO] Done. You only need to do this once.
    echo.
)

REM Use mvnw.cmd if present, fall back to system mvn
IF EXIST "mvnw.cmd" (
    mvnw.cmd spring-boot:run
) ELSE (
    echo  [WARN] mvnw.cmd not found. Trying system mvn...
    mvn spring-boot:run
)
