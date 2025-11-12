# Script de Testing Automatizado
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  TESTING AUTOMATIZADO - Sistema de Reservas" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8090"
$testResults = @()

# Función para registrar resultados
function Add-TestResult {
    param($Categoria, $Test, $Resultado, $Detalles)
    
    $script:testResults += [PSCustomObject]@{
        Categoria = $Categoria
        Test = $Test
        Resultado = $Resultado
        Detalles = $Detalles
    }
    
    $color = if ($Resultado -eq "✅ PASS") { "Green" } elseif ($Resultado -eq "❌ FAIL") { "Red" } else { "Yellow" }
    Write-Host "$Resultado $Categoria - $Test" -ForegroundColor $color
    if ($Detalles) {
        Write-Host "   → $Detalles" -ForegroundColor Gray
    }
}

# 1. VERIFICAR SERVIDOR
Write-Host "1. VERIFICANDO SERVIDOR..." -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/" -Method GET -TimeoutSec 5 -ErrorAction Stop
    Add-TestResult "Servidor" "Conectividad" "✅ PASS" "Puerto 8090 responde"
} catch {
    Add-TestResult "Servidor" "Conectividad" "❌ FAIL" $_.Exception.Message
    Write-Host ""
    Write-Host "❌ Error: El servidor no está corriendo. Inicia la aplicación primero." -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. VERIFICAR BASE DE DATOS
Write-Host "2. VERIFICANDO BASE DE DATOS..." -ForegroundColor Yellow
Write-Host ""

try {
    $usuarios = Invoke-RestMethod -Uri "$baseUrl/api/debug/usuarios" -Method GET -ErrorAction Stop
    Add-TestResult "Base de Datos" "Conexión" "✅ PASS" "BD H2 accesible"
    Add-TestResult "Base de Datos" "Usuarios precargados" "✅ PASS" "Total: $($usuarios.total) usuarios"
    
    # Verificar usuarios vendedores
    $vendedores = $usuarios.usuarios | Where-Object { $_.email -like "*@tgsmax.com" }
    if ($vendedores.Count -ge 6) {
        Add-TestResult "Base de Datos" "Datos iniciales" "✅ PASS" "$($vendedores.Count) vendedores cargados"
    } else {
        Add-TestResult "Base de Datos" "Datos iniciales" "⚠️ WARN" "Solo $($vendedores.Count) vendedores"
    }
    
} catch {
    Add-TestResult "Base de Datos" "Conexión" "❌ FAIL" $_.Exception.Message
}

Write-Host ""

# 3. VERIFICAR ROLES
Write-Host "3. VERIFICANDO ROLES..." -ForegroundColor Yellow
Write-Host ""

try {
    $stats = Invoke-RestMethod -Uri "$baseUrl/api/debug/stats" -Method GET -ErrorAction Stop
    
    if ($stats.usuarios_con_bcrypt -eq $stats.total_usuarios) {
        Add-TestResult "Seguridad" "Encriptación BCrypt" "✅ PASS" "Todas las contraseñas encriptadas"
    } else {
        Add-TestResult "Seguridad" "Encriptación BCrypt" "❌ FAIL" "Contraseñas sin encriptar: $($stats.total_usuarios - $stats.usuarios_con_bcrypt)"
    }
    
} catch {
    Add-TestResult "Seguridad" "Verificación" "❌ FAIL" $_.Exception.Message
}

Write-Host ""

# 4. VERIFICAR PÁGINAS PÚBLICAS
Write-Host "4. VERIFICANDO PÁGINAS PÚBLICAS..." -ForegroundColor Yellow
Write-Host ""

$paginasPublicas = @(
    @{Nombre="Home"; URL="/"},
    @{Nombre="Login"; URL="/login"},
    @{Nombre="Register"; URL="/register"},
    @{Nombre="H2 Console"; URL="/h2-console"}
)

foreach ($pagina in $paginasPublicas) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl$($pagina.URL)" -Method GET -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Add-TestResult "Páginas Públicas" $pagina.Nombre "✅ PASS" "HTTP 200"
        } else {
            Add-TestResult "Páginas Públicas" $pagina.Nombre "⚠️ WARN" "HTTP $($response.StatusCode)"
        }
    } catch {
        Add-TestResult "Páginas Públicas" $pagina.Nombre "❌ FAIL" $_.Exception.Message
    }
}

Write-Host ""

# 5. VERIFICAR PROTECCIÓN DE RUTAS
Write-Host "5. VERIFICANDO PROTECCIÓN DE RUTAS..." -ForegroundColor Yellow
Write-Host ""

$rutasProtegidas = @("/dashboard", "/profile", "/reservas")

foreach ($ruta in $rutasProtegidas) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl$ruta" -Method GET -MaximumRedirection 0 -ErrorAction Stop
        Add-TestResult "Seguridad" "Protección $ruta" "❌ FAIL" "Acceso sin login permitido"
    } catch {
        if ($_.Exception.Response.StatusCode -eq 302 -or $_.Exception.Response.StatusCode -eq 'Found') {
            $location = $_.Exception.Response.Headers.Location
            if ($location -like "*/login*") {
                Add-TestResult "Seguridad" "Protección $ruta" "✅ PASS" "Redirige a login"
            } else {
                Add-TestResult "Seguridad" "Protección $ruta" "⚠️ WARN" "Redirige a: $location"
            }
        } else {
            Add-TestResult "Seguridad" "Protección $ruta" "⚠️ WARN" $_.Exception.Message
        }
    }
}

Write-Host ""

# 6. TEST DE REGISTRO (OPCIONAL)
Write-Host "6. TEST DE REGISTRO (Opcional)..." -ForegroundColor Yellow
$ejecutarTestRegistro = Read-Host "¿Deseas probar el registro de un usuario de prueba? (s/n)"

if ($ejecutarTestRegistro -eq 's' -or $ejecutarTestRegistro -eq 'S') {
    Write-Host ""
    Write-Host "Abre el navegador en: $baseUrl/register" -ForegroundColor Cyan
    Write-Host "Registra un usuario con estos datos:" -ForegroundColor White
    Write-Host "  Email: testuser$(Get-Random -Minimum 100 -Maximum 999)@test.com" -ForegroundColor Gray
    Write-Host "  DNI: $(Get-Random -Minimum 10000000 -Maximum 99999999)" -ForegroundColor Gray
    Write-Host "  Contraseña: test1234" -ForegroundColor Gray
    Write-Host ""
    Read-Host "Presiona Enter cuando hayas completado el registro"
    
    # Verificar que el usuario se haya registrado
    try {
        $usuariosDespues = Invoke-RestMethod -Uri "$baseUrl/api/debug/usuarios" -Method GET
        if ($usuariosDespues.total -gt $usuarios.total) {
            Add-TestResult "Funcionalidad" "Registro de usuario" "✅ PASS" "Usuario registrado correctamente"
        } else {
            Add-TestResult "Funcionalidad" "Registro de usuario" "❌ FAIL" "Usuario no se guardó en BD"
        }
    } catch {
        Add-TestResult "Funcionalidad" "Registro de usuario" "❌ FAIL" $_.Exception.Message
    }
}

Write-Host ""

# 7. RESUMEN DE RESULTADOS
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  RESUMEN DE TESTING" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$totalTests = $testResults.Count
$passTests = ($testResults | Where-Object { $_.Resultado -eq "✅ PASS" }).Count
$failTests = ($testResults | Where-Object { $_.Resultado -eq "❌ FAIL" }).Count
$warnTests = ($testResults | Where-Object { $_.Resultado -eq "⚠️ WARN" }).Count

Write-Host "Total de tests: $totalTests" -ForegroundColor White
Write-Host "Pasados: $passTests" -ForegroundColor Green
Write-Host "Fallidos: $failTests" -ForegroundColor Red
Write-Host "Advertencias: $warnTests" -ForegroundColor Yellow
Write-Host ""

$porcentaje = [math]::Round(($passTests / $totalTests) * 100, 2)
Write-Host "Tasa de éxito: $porcentaje%" -ForegroundColor $(if ($porcentaje -ge 80) { "Green" } elseif ($porcentaje -ge 60) { "Yellow" } else { "Red" })

Write-Host ""

# Mostrar tabla de resultados
Write-Host "DETALLES:" -ForegroundColor Cyan
Write-Host ""
$testResults | Format-Table -AutoSize

# 8. TESTS FALLIDOS
if ($failTests -gt 0) {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host "  TESTS FALLIDOS - REQUIEREN ATENCIÓN" -ForegroundColor Red
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host ""
    
    $testResults | Where-Object { $_.Resultado -eq "❌ FAIL" } | ForEach-Object {
        Write-Host "❌ $($_.Categoria) - $($_.Test)" -ForegroundColor Red
        Write-Host "   Detalles: $($_.Detalles)" -ForegroundColor Gray
        Write-Host ""
    }
}

# 9. RECOMENDACIONES
Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  PRÓXIMOS PASOS" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

if ($failTests -eq 0 -and $warnTests -eq 0) {
    Write-Host "✅ ¡Excelente! Todos los tests básicos pasaron." -ForegroundColor Green
    Write-Host ""
    Write-Host "Pruebas manuales recomendadas:" -ForegroundColor White
    Write-Host "  1. Flujo completo de registro y login" -ForegroundColor Gray
    Write-Host "  2. Navegación por el dashboard" -ForegroundColor Gray
    Write-Host "  3. Edición de perfil" -ForegroundColor Gray
    Write-Host "  4. Creación de reservas (si está implementado)" -ForegroundColor Gray
} else {
    Write-Host "⚠️  Se encontraron problemas. Revisa los tests fallidos." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Acciones sugeridas:" -ForegroundColor White
    Write-Host "  1. Revisa los logs de la aplicación" -ForegroundColor Gray
    Write-Host "  2. Verifica la configuración de BD (application.properties)" -ForegroundColor Gray
    Write-Host "  3. Asegúrate de que DataInitializer se ejecute correctamente" -ForegroundColor Gray
}

Write-Host ""
Write-Host "Para testing manual detallado, consulta: PLAN_TESTING.md" -ForegroundColor Cyan
Write-Host ""
