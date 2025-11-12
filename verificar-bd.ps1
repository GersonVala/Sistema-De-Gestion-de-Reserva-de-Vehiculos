# Script para verificar datos en H2 Database
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  VERIFICACIÓN DE BASE DE DATOS H2" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Configuración
$baseUrl = "http://localhost:8090"

Write-Host "1. Verificando servidor Spring Boot..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/" -Method GET -TimeoutSec 5 -ErrorAction Stop
    Write-Host "   ✅ Servidor respondiendo" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Error: Servidor no responde. Inicia la aplicación primero." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  INSTRUCCIONES PARA H2 CONSOLE" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Abre tu navegador en: $baseUrl/h2-console" -ForegroundColor White
Write-Host ""
Write-Host "2. Configuración de conexión:" -ForegroundColor White
Write-Host "   Driver Class: org.h2.Driver" -ForegroundColor Cyan
Write-Host "   JDBC URL: jdbc:h2:mem:testdb" -ForegroundColor Yellow
Write-Host "   User Name: sa" -ForegroundColor Cyan
Write-Host "   Password: (dejar vacío)" -ForegroundColor Cyan
Write-Host ""
Write-Host "3. Haz clic en 'Connect'" -ForegroundColor White
Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  CONSULTAS SQL PARA EJECUTAR" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "A. Ver TODOS los usuarios registrados:" -ForegroundColor Yellow
Write-Host ""
Write-Host "SELECT id_usuario, nombre_usuario, apellido_usuario, email_usuario, " -ForegroundColor White
Write-Host "       dni_usuario, telefono_usuario, LENGTH(contraseña) as password_length" -ForegroundColor White
Write-Host "FROM usuarios;" -ForegroundColor White
Write-Host ""

Write-Host "B. Buscar usuario específico por email:" -ForegroundColor Yellow
Write-Host ""
Write-Host "SELECT * FROM usuarios WHERE email_usuario = 'salolopez@gmail.com';" -ForegroundColor White
Write-Host ""

Write-Host "C. Verificar que la contraseña esté encriptada (debe ser 60 caracteres):" -ForegroundColor Yellow
Write-Host ""
Write-Host "SELECT email_usuario, LENGTH(contraseña) as longitud_password, " -ForegroundColor White
Write-Host "       SUBSTRING(contraseña, 1, 10) as primeros_10_chars" -ForegroundColor White
Write-Host "FROM usuarios;" -ForegroundColor White
Write-Host ""

Write-Host "D. Contar cuántos usuarios hay:" -ForegroundColor Yellow
Write-Host ""
Write-Host "SELECT COUNT(*) as total_usuarios FROM usuarios;" -ForegroundColor White
Write-Host ""

Write-Host "E. Ver todos los roles asignados:" -ForegroundColor Yellow
Write-Host ""
Write-Host "SELECT u.email_usuario, r.nombre_rol" -ForegroundColor White
Write-Host "FROM usuarios u" -ForegroundColor White
Write-Host "JOIN usuario_roles ur ON u.id_usuario = ur.id_usuario" -ForegroundColor White
Write-Host "JOIN roles r ON ur.id_rol = r.id_rol;" -ForegroundColor White
Write-Host ""

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  QUÉ VERIFICAR" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ El usuario debe existir en la tabla" -ForegroundColor Green
Write-Host "✅ El email debe estar en minúsculas" -ForegroundColor Green
Write-Host "✅ La contraseña debe tener exactamente 60 caracteres" -ForegroundColor Green
Write-Host "✅ La contraseña debe empezar con `$2a`$" -ForegroundColor Green
Write-Host "✅ El usuario debe tener al menos un rol asignado" -ForegroundColor Green
Write-Host ""

Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Presiona cualquier tecla para abrir H2 Console en el navegador..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

Start-Process "$baseUrl/h2-console"
