# Script completo de diagnóstico de Login
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  DIAGNÓSTICO COMPLETO DE LOGIN" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8090"

# Función para hacer peticiones
function Invoke-ApiRequest {
    param($Url, $Description)
    
    Write-Host "➡️  $Description" -ForegroundColor Yellow
    Write-Host "   URL: $Url" -ForegroundColor Gray
    
    try {
        $response = Invoke-RestMethod -Uri $Url -Method GET -ErrorAction Stop
        Write-Host "   ✅ Respuesta recibida:" -ForegroundColor Green
        $response | ConvertTo-Json -Depth 10 | Write-Host -ForegroundColor White
        Write-Host ""
        return $response
    } catch {
        Write-Host "   ❌ Error: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        return $null
    }
}

# 1. Verificar servidor
Write-Host "1. Verificando servidor Spring Boot..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/" -Method GET -TimeoutSec 5 -ErrorAction Stop
    Write-Host "   ✅ Servidor corriendo en puerto 8090" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "   ❌ Servidor no responde. Inicia la aplicación primero." -ForegroundColor Red
    exit 1
}

# 2. Estadísticas generales
Write-Host "2. Obteniendo estadísticas de la base de datos..." -ForegroundColor Cyan
$stats = Invoke-ApiRequest -Url "$baseUrl/api/debug/stats" -Description "Estadísticas generales"

# 3. Ver todos los usuarios
Write-Host "3. Listando todos los usuarios..." -ForegroundColor Cyan
$usuarios = Invoke-ApiRequest -Url "$baseUrl/api/debug/usuarios" -Description "Todos los usuarios"

# 4. Pedir email para buscar
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "4. Verificar usuario específico" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
$email = Read-Host "Ingresa el email del usuario a verificar (ejemplo: salolopez@gmail.com)"

if ($email) {
    Write-Host ""
    Write-Host "Buscando usuario con email: $email" -ForegroundColor Yellow
    Write-Host ""
    
    # Verificar si existe
    $existe = Invoke-ApiRequest -Url "$baseUrl/api/debug/usuarios/existe?email=$email" -Description "¿Usuario existe?"
    
    # Obtener detalles
    $detalles = Invoke-ApiRequest -Url "$baseUrl/api/debug/usuarios/email?email=$email" -Description "Detalles del usuario"
    
    # Análisis
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host "  ANÁLISIS DEL USUARIO" -ForegroundColor Cyan
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host ""
    
    if ($detalles.encontrado -eq $true) {
        Write-Host "✅ Usuario ENCONTRADO en la base de datos" -ForegroundColor Green
        Write-Host ""
        Write-Host "Detalles:" -ForegroundColor White
        Write-Host "  • ID: $($detalles.usuario.id)" -ForegroundColor Cyan
        Write-Host "  • Nombre: $($detalles.usuario.nombre_completo)" -ForegroundColor Cyan
        Write-Host "  • Email en BD: $($detalles.usuario.email_en_bd)" -ForegroundColor Cyan
        Write-Host "  • DNI: $($detalles.usuario.dni)" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Contraseña:" -ForegroundColor White
        Write-Host "  • Longitud: $($detalles.usuario.password_length) caracteres" -ForegroundColor Cyan
        Write-Host "  • Empieza con: $($detalles.usuario.password_empieza_con)..." -ForegroundColor Cyan
        Write-Host "  • Es BCrypt: $(if ($detalles.usuario.password_es_bcrypt) {'✅ SÍ'} else {'❌ NO'})" -ForegroundColor $(if ($detalles.usuario.password_es_bcrypt) {'Green'} else {'Red'})
        Write-Host ""
        
        # Verificaciones
        Write-Host "Verificaciones:" -ForegroundColor White
        $checks = @()
        
        if ($detalles.usuario.password_length -eq 60) {
            Write-Host "  ✅ Longitud de contraseña correcta (60 caracteres)" -ForegroundColor Green
        } else {
            Write-Host "  ❌ Longitud de contraseña incorrecta (debe ser 60, es $($detalles.usuario.password_length))" -ForegroundColor Red
        }
        
        if ($detalles.usuario.password_es_bcrypt) {
            Write-Host "  ✅ Contraseña encriptada con BCrypt" -ForegroundColor Green
        } else {
            Write-Host "  ❌ Contraseña NO está encriptada con BCrypt" -ForegroundColor Red
        }
        
        if ($detalles.usuario.email_en_bd -eq $email.ToLower().Trim()) {
            Write-Host "  ✅ Email coincide (normalizado a minúsculas)" -ForegroundColor Green
        } else {
            Write-Host "  ⚠️  Email en BD: [$($detalles.usuario.email_en_bd)]" -ForegroundColor Yellow
            Write-Host "  ⚠️  Email buscado: [$($email.ToLower().Trim())]" -ForegroundColor Yellow
        }
        
    } else {
        Write-Host "❌ Usuario NO ENCONTRADO en la base de datos" -ForegroundColor Red
        Write-Host ""
        Write-Host "Posibles causas:" -ForegroundColor Yellow
        Write-Host "  1. El usuario no se registró correctamente" -ForegroundColor White
        Write-Host "  2. El email tiene espacios o mayúsculas diferentes" -ForegroundColor White
        Write-Host "  3. La base de datos H2 se reinició (es en memoria)" -ForegroundColor White
        Write-Host ""
        Write-Host "Usuarios disponibles:" -ForegroundColor Yellow
        if ($stats.emails_registrados) {
            $stats.emails_registrados | ForEach-Object { Write-Host "  • $_" -ForegroundColor Cyan }
        }
    }
}

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  RECOMENDACIONES" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

if ($stats.total_usuarios -eq 0) {
    Write-Host "❌ No hay usuarios registrados" -ForegroundColor Red
    Write-Host ""
    Write-Host "Solución:" -ForegroundColor Yellow
    Write-Host "  1. Ve a: $baseUrl/register" -ForegroundColor White
    Write-Host "  2. Registra un usuario nuevo" -ForegroundColor White
    Write-Host "  3. Vuelve a ejecutar este script" -ForegroundColor White
} elseif ($stats.total_usuarios -gt 0 -and $stats.usuarios_con_bcrypt -eq $stats.total_usuarios) {
    Write-Host "✅ Todos los usuarios tienen contraseñas BCrypt correctas" -ForegroundColor Green
    Write-Host ""
    Write-Host "Si el login no funciona, verifica:" -ForegroundColor Yellow
    Write-Host "  1. Que estés usando el email EXACTO que aparece en la BD" -ForegroundColor White
    Write-Host "  2. Que la contraseña sea la correcta" -ForegroundColor White
    Write-Host "  3. Revisa los logs de la consola de Spring Boot" -ForegroundColor White
} else {
    Write-Host "⚠️  Algunos usuarios tienen problemas con sus contraseñas" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Usuarios con BCrypt correcto: $($stats.usuarios_con_bcrypt)/$($stats.total_usuarios)" -ForegroundColor White
}

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  HERRAMIENTAS ADICIONALES" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Ver todos los usuarios:" -ForegroundColor Yellow
Write-Host "  $baseUrl/api/debug/usuarios" -ForegroundColor Cyan
Write-Host ""
Write-Host "H2 Console (interfaz gráfica):" -ForegroundColor Yellow
Write-Host "  $baseUrl/h2-console" -ForegroundColor Cyan
Write-Host "  JDBC URL: jdbc:h2:mem:testdb" -ForegroundColor Gray
Write-Host "  User: sa" -ForegroundColor Gray
Write-Host "  Password: (vacío)" -ForegroundColor Gray
Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
