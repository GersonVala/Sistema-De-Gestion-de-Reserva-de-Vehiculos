@echo off
echo Iniciando aplicación Spring Boot...
cd /d "%~dp0"

REM Intentar con mvnw primero
echo Intentando con Maven Wrapper...
call mvnw.cmd spring-boot:run
if %ERRORLEVEL% NEQ 0 (
    echo Maven Wrapper falló, intentando con Maven...
    mvn spring-boot:run
    if %ERRORLEVEL% NEQ 0 (
        echo No se pudo ejecutar con Maven, ejecutando con Java directamente...
        java -cp "target/classes" com.reservaDeVehiculos.ProyectoReservaDeVehiculos.ProyectoReservaDeVehiculosApplication
    )
)
pause