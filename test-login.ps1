# Script para probar el login después de las correcciones
# CORRECCIÓN DEFINITIVA: Deshabilitado Spring Security formLogin
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  TEST DE LOGIN - Sistema de Reservas" -ForegroundColor Cyan
Write-Host "  CORRECCIÓN: Spring Security deshabilitado" -ForegroundColor Yellow
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "⚠️  IMPORTANTE: Debes REINICIAR la aplicación Spring Boot" -ForegroundColor Red
Write-Host "   Los cambios en SecurityConfig requieren reinicio completo" -ForegroundColor Red
Write-Host ""

# Configuración
$baseUrl = "http://localhost:8090"
$email = "salolopez@gmail.com"

Write-Host "1. Probando conexión al servidor..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/" -Method GET -TimeoutSec 5
    Write-Host "   ✅ Servidor respondiendo en puerto 8090" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Error: Servidor no responde. Asegúrate de que Spring Boot esté corriendo." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "2. Verificando endpoint /login (GET)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/login" -Method GET -TimeoutSec 5
    Write-Host "   ✅ Formulario de login accesible" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Error: No se puede acceder al formulario de login" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "3. Verificando H2 Console..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/h2-console" -Method GET -TimeoutSec 5
    Write-Host "   ✅ H2 Console accesible" -ForegroundColor Green
} catch {
    Write-Host "   ⚠️  Advertencia: H2 Console no accesible" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  INSTRUCCIONES DE PRUEBA" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Abre el navegador en: $baseUrl/login" -ForegroundColor White
Write-Host "2. Ingresa el email: $email" -ForegroundColor White
Write-Host "3. Ingresa la contraseña que usaste al registrarte" -ForegroundColor White
Write-Host "4. Haz clic en 'Iniciar Sesión'" -ForegroundColor White
Write-Host ""
Write-Host "5. Revisa la consola de Spring Boot para ver los logs:" -ForegroundColor White
Write-Host "   - 🔵 Procesando login para email..." -ForegroundColor Cyan
Write-Host "   - 🔍 Llamando a usuarioService.login()..." -ForegroundColor Cyan
Write-Host "   - ✅ Login exitoso, creando sesión..." -ForegroundColor Green
Write-Host "   - ✅ Sesión creada exitosamente..." -ForegroundColor Green
Write-Host "   - 🔀 Redirigiendo a /dashboard" -ForegroundColor Green
Write-Host ""
Write-Host "Si ves estos logs, el problema está resuelto ✅" -ForegroundColor Green
Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
