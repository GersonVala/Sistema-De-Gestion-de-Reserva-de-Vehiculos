# 🔍 GUÍA DE VERIFICACIÓN DE DATOS EN LA BASE DE DATOS

## 🚀 OPCIÓN 1: Script Automático (RECOMENDADO)

### Ejecuta el script de diagnóstico:

```powershell
.\diagnostico-login.ps1
```

Este script te mostrará:
- ✅ Total de usuarios registrados
- ✅ Estadísticas de contraseñas (BCrypt)
- ✅ Búsqueda de usuario específico
- ✅ Validación de datos
- ✅ Recomendaciones personalizadas

---

## 🌐 OPCIÓN 2: API REST (Navegador o Postman)

### Endpoints disponibles:

#### 1. Ver todos los usuarios
```
http://localhost:8090/api/debug/usuarios
```

**Respuesta esperada:**
```json
{
  "total": 1,
  "usuarios": [
    {
      "id": 1,
      "nombre": "Salo",
      "apellido": "Lopez",
      "email": "salolopez@gmail.com",
      "dni": "12345678",
      "telefono": "1234567890",
      "password_length": 60,
      "password_starts_with": "$2a$10$abc"
    }
  ]
}
```

#### 2. Buscar usuario por email
```
http://localhost:8090/api/debug/usuarios/email?email=salolopez@gmail.com
```

**Respuesta esperada:**
```json
{
  "email_buscado": "salolopez@gmail.com",
  "usuario": {
    "encontrado": true,
    "id": 1,
    "nombre_completo": "Salo Lopez",
    "email_en_bd": "salolopez@gmail.com",
    "dni": "12345678",
    "password_length": 60,
    "password_empieza_con": "$2a$10$abc",
    "password_es_bcrypt": true
  }
}
```

#### 3. Verificar si existe un email
```
http://localhost:8090/api/debug/usuarios/existe?email=salolopez@gmail.com
```

#### 4. Estadísticas
```
http://localhost:8090/api/debug/stats
```

---

## 💾 OPCIÓN 3: H2 Console (Interfaz Gráfica)

### Paso a paso:

1. **Abre el navegador en:**
   ```
   http://localhost:8090/h2-console
   ```

2. **Configuración de conexión:**
   ```
   Driver Class: org.h2.Driver
   JDBC URL: jdbc:h2:mem:testdb
   User Name: sa
   Password: (dejar vacío)
   ```

3. **Haz clic en "Connect"**

4. **Ejecuta estas consultas SQL:**

### Consulta A: Ver todos los usuarios
```sql
SELECT 
    id_usuario, 
    nombre_usuario, 
    apellido_usuario, 
    email_usuario, 
    dni_usuario, 
    LENGTH(contraseña) as password_length,
    SUBSTRING(contraseña, 1, 10) as password_preview
FROM usuarios;
```

### Consulta B: Buscar usuario específico
```sql
SELECT * 
FROM usuarios 
WHERE email_usuario = 'salolopez@gmail.com';
```

### Consulta C: Verificar contraseñas BCrypt
```sql
SELECT 
    email_usuario,
    LENGTH(contraseña) as longitud,
    SUBSTRING(contraseña, 1, 4) as prefijo,
    CASE 
        WHEN LENGTH(contraseña) = 60 AND SUBSTRING(contraseña, 1, 4) IN ('$2a$', '$2b$') 
        THEN 'BCrypt OK'
        ELSE 'ERROR'
    END as validacion
FROM usuarios;
```

### Consulta D: Ver roles asignados
```sql
SELECT 
    u.email_usuario,
    u.nombre_usuario,
    u.apellido_usuario,
    r.nombre_rol
FROM usuarios u
LEFT JOIN usuario_roles ur ON u.id_usuario = ur.id_usuario
LEFT JOIN roles r ON ur.id_rol = r.id_rol;
```

---

## ✅ QUÉ VERIFICAR

### 1. Usuario existe
- ✅ El usuario debe aparecer en la tabla `usuarios`
- ✅ El ID debe ser un número positivo

### 2. Email correcto
- ✅ Debe estar en **minúsculas**
- ✅ Sin espacios al inicio o final
- ✅ Debe coincidir exactamente con el que usas para login

### 3. Contraseña BCrypt
- ✅ Longitud: **exactamente 60 caracteres**
- ✅ Prefijo: debe empezar con `$2a$` o `$2b$`
- ✅ Ejemplo: `$2a$10$abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJ`

### 4. Rol asignado
- ✅ Debe tener al menos el rol `CLIENTE`
- ✅ Verificable en la tabla `usuario_roles`

---

## 🐛 PROBLEMAS COMUNES Y SOLUCIONES

### ❌ Problema: "No hay usuarios registrados"

**Causa:** La base de datos H2 está vacía o se reinició.

**Solución:**
1. Ve a `http://localhost:8090/register`
2. Registra un usuario nuevo
3. Verifica que aparezca en la BD

---

### ❌ Problema: "Usuario encontrado pero login no funciona"

**Verifica estos puntos:**

#### A. Email coincide exactamente
```
Email usado en login: salolopez@gmail.com
Email en BD:          salolopez@gmail.com
                      ✅ COINCIDEN
```

Si hay diferencia (mayúsculas, espacios), el login fallará.

#### B. Contraseña está encriptada
```
Longitud: 60 ✅
Empieza con: $2a$ ✅
```

Si la contraseña NO está encriptada (longitud diferente a 60), el login fallará.

#### C. Revisión de logs
Busca en la consola de Spring Boot:

```
=== DEBUG LOGIN ===
Email limpio: [salolopez@gmail.com]
✅ Usuario encontrado: Salo Lopez
¿Contraseña coincide? false  ← AQUÍ ESTÁ EL PROBLEMA
```

Si dice `false`, la contraseña que ingresas no es la correcta.

---

### ❌ Problema: "Contraseña incorrecta"

**Causas posibles:**

1. **Estás usando una contraseña diferente a la que registraste**
   - Solución: Intenta recordar la contraseña o registra un usuario nuevo

2. **La contraseña no se guardó encriptada correctamente**
   - Verifica en BD: longitud debe ser 60
   - Si no es 60, hay un problema con BCrypt
   - Solución: Registra un usuario nuevo

3. **Hay espacios en la contraseña**
   - Al registrar: "mipassword "
   - Al hacer login: "mipassword"
   - Solución: Usa la misma contraseña exacta

---

### ❌ Problema: "Usuario no se guarda en BD"

**Diagnóstico:**

Busca en los logs de Spring Boot al registrar:

```
=== DEBUG REGISTRO ===
Email limpio: [salolopez@gmail.com]
¿Email existe en BD? false
¿DNI existe en BD? false
Validaciones pasadas, creando usuario...
Contraseña encriptada (primeros 10 chars): [$2a$10$abc...]
Longitud contraseña encriptada: 60
Guardando usuario en BD...
✅ Usuario guardado con ID: 1
```

Si NO ves estos logs, el problema está en el registro.

Si ves los logs pero luego el usuario no existe, puede ser:
- Base de datos H2 en memoria se reinició
- Error en transacción (rollback)

**Solución:** Verifica inmediatamente después de registrar con:
```
http://localhost:8090/api/debug/usuarios
```

---

## 📊 FLUJO DE VERIFICACIÓN RECOMENDADO

### Paso 1: Registrar usuario
```
1. Ve a http://localhost:8090/register
2. Completa el formulario
3. Anota el email y contraseña que usas
4. Click en "Registrar"
```

### Paso 2: Verificar inmediatamente
```powershell
.\diagnostico-login.ps1
# O en navegador:
http://localhost:8090/api/debug/usuarios
```

### Paso 3: Confirmar datos
```
✅ Usuario aparece en la lista
✅ Email está en minúsculas
✅ Contraseña tiene 60 caracteres
✅ Contraseña empieza con $2a$
```

### Paso 4: Intentar login
```
1. Ve a http://localhost:8090/login
2. Usa el MISMO email (con minúsculas)
3. Usa la MISMA contraseña
4. Click en "Iniciar Sesión"
```

### Paso 5: Revisar logs
```
Busca en consola de Spring Boot:
🔵 Procesando login para email: ...
✅ Usuario encontrado: ...
¿Contraseña coincide? true  ← DEBE SER TRUE
✅ LOGIN EXITOSO
```

---

## 🎯 DEBUGGING AVANZADO

### Si el usuario existe pero el login falla:

1. **Ejecuta el diagnóstico:**
   ```powershell
   .\diagnostico-login.ps1
   ```

2. **Anota los datos exactos:**
   - Email en BD: `[copiar exactamente]`
   - Longitud password: `[debe ser 60]`
   - Prefijo password: `[debe ser $2a$ o $2b$]`

3. **Prueba login con esos datos EXACTOS:**
   - Email: copia y pega el email de la BD
   - Contraseña: usa la que registraste

4. **Revisa logs en tiempo real:**
   - Observa la consola de Spring Boot
   - Busca el mensaje "¿Contraseña coincide?"
   - Si dice `false`, la contraseña es incorrecta

5. **Si persiste el problema:**
   - Registra un usuario NUEVO
   - Usa email diferente (ej: `test@test.com`)
   - Usa contraseña simple (ej: `test1234`)
   - Verifica inmediatamente con el script
   - Intenta login de nuevo

---

## 🔧 COMANDOS ÚTILES

### PowerShell:

```powershell
# Diagnóstico completo
.\diagnostico-login.ps1

# Ver BD en navegador
Start-Process "http://localhost:8090/h2-console"

# Ver usuarios vía API
Start-Process "http://localhost:8090/api/debug/usuarios"
```

### Navegador:

```
Todos los usuarios:
http://localhost:8090/api/debug/usuarios

Buscar por email:
http://localhost:8090/api/debug/usuarios/email?email=TU_EMAIL

Estadísticas:
http://localhost:8090/api/debug/stats

H2 Console:
http://localhost:8090/h2-console
```

---

## 📝 RESUMEN

1. ✅ Ejecuta `.\diagnostico-login.ps1` para verificar
2. ✅ Confirma que el usuario existe con contraseña BCrypt
3. ✅ Usa el email exacto que aparece en la BD
4. ✅ Revisa los logs de Spring Boot al hacer login
5. ✅ Si falla, registra un usuario nuevo de prueba

---

**¿Necesitas ayuda adicional?** 
Comparte la salida del script `diagnostico-login.ps1` y los logs de la consola.
