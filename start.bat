@echo off
echo Starting Avis de Vol application...
echo.

echo Building and starting all services...
docker-compose up --build -d

echo.
echo Waiting for services to start...
timeout /t 30 /nobreak > nul

echo.
echo Services are starting up. You can access:
echo - Frontend: http://localhost:80
echo - Backend API: http://localhost:8080/api
echo - Health Check: http://localhost:8080/actuator/health
echo.

echo To view logs, run: docker-compose logs -f
echo To stop services, run: docker-compose down
echo.

pause
