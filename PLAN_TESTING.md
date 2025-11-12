# 🧪 PLAN DE TESTING COMPLETO - Sistema de Reserva de Vehículos

## 📋 CHECKLIST DE FUNCIONALIDADES A TESTEAR

---

## 1️⃣ AUTENTICACIÓN Y SESIONES

### ✅ Registro de Usuario
- [ ] Registrar usuario con datos válidos
- [ ] Intentar registrar con email duplicado
- [ ] Intentar registrar con DNI duplicado
- [ ] Registrar con contraseñas que no coinciden
- [ ] Registrar con campos vacíos
- [ ] Registrar con email inválido
- [ ] Verificar que la contraseña se encripte (60 caracteres)
- [ ] Verificar que el rol CLIENTE se asigne automáticamente

**URL:** `http://localhost:8090/register`

**Datos de prueba:**
```
Nombre: Test
Apellido: Usuario
Email: test{numero}@test.com
DNI: 1234567{numero}
Teléfono: 1122334455
Contraseña: test1234
```

---

### ✅ Login
- [ ] Login con credenciales correctas
- [ ] Login con email incorrecto
- [ ] Login con contraseña incorrecta
- [ ] Login con email en mayúsculas (debe funcionar)
- [ ] Login con espacios en el email (debe funcionar)
- [ ] Verificar redirección a /dashboard
- [ ] Verificar que la sesión se cree correctamente
- [ ] Verificar mensaje de bienvenida

**URL:** `http://localhost:8090/login`

---

### ✅ Logout
- [ ] Hacer logout estando logueado
- [ ] Verificar que la sesión se destruya
- [ ] Verificar redirección a la home
- [ ] Intentar acceder a /dashboard después del logout (debe redirigir a /login)

**URL:** Click en botón "Cerrar Sesión" en el dashboard

---

### ✅ Protección de Rutas
- [ ] Intentar acceder a /dashboard sin login (debe redirigir a /login)
- [ ] Intentar acceder a /profile sin login
- [ ] Intentar acceder a /reservas sin login
- [ ] Acceder a /login estando ya logueado (debe redirigir a /dashboard)
- [ ] Acceder a /register estando ya logueado

---

### ✅ Sesiones
- [ ] Verificar timeout de sesión (30 minutos)
- [ ] Verificar que la sesión persista al navegar entre páginas
- [ ] Verificar información de sesión en el dashboard

---

## 2️⃣ DASHBOARD

### ✅ Vista del Dashboard
- [ ] Ver información del usuario logueado
- [ ] Ver estadísticas de reservas
- [ ] Ver reservas recientes (últimas 3)
- [ ] Ver nombre completo del usuario
- [ ] Ver email del usuario

**URL:** `http://localhost:8090/dashboard`

---

## 3️⃣ PERFIL DE USUARIO

### ✅ Ver Perfil
- [ ] Ver datos del usuario
- [ ] Verificar que muestre nombre, apellido, email, teléfono, DNI

**URL:** `http://localhost:8090/profile`

---

### ✅ Editar Perfil
- [ ] Actualizar nombre
- [ ] Actualizar apellido
- [ ] Actualizar teléfono
- [ ] Intentar cambiar email a uno ya existente (debe fallar)
- [ ] Verificar que DNI no se pueda cambiar
- [ ] Verificar que los cambios se guarden en BD
- [ ] Verificar que la sesión se actualice con nuevos datos

---

## 4️⃣ RESERVAS (Si está implementado)

### ✅ Listar Reservas
- [ ] Ver todas las reservas del usuario
- [ ] Filtrar por estado (pendiente, confirmada, cancelada, completada)
- [ ] Ver detalles de una reserva específica

**URL:** `http://localhost:8090/reservas`

---

### ✅ Crear Reserva
- [ ] Crear reserva con fechas válidas
- [ ] Intentar crear reserva con fecha pasada
- [ ] Intentar crear reserva con fecha fin anterior a fecha inicio
- [ ] Seleccionar vehículo disponible
- [ ] Seleccionar sucursal
- [ ] Verificar cálculo de precio

**URL:** `http://localhost:8090/reservation` o `/reservas/nueva`

---

### ✅ Cancelar Reserva
- [ ] Cancelar reserva pendiente
- [ ] Verificar cambio de estado
- [ ] Intentar cancelar reserva ya completada

---

## 5️⃣ VEHÍCULOS (Si está implementado)

### ✅ Listar Vehículos
- [ ] Ver todos los vehículos disponibles
- [ ] Filtrar por tipo (auto, camioneta, moto)
- [ ] Filtrar por estado (disponible, reservado, etc.)
- [ ] Ver detalles de un vehículo

---

## 6️⃣ BASE DE DATOS

### ✅ Integridad de Datos
- [ ] Verificar que usuarios se guardan correctamente
- [ ] Verificar encriptación de contraseñas (BCrypt)
- [ ] Verificar asignación de roles
- [ ] Verificar relaciones entre tablas
- [ ] Verificar datos iniciales (vendedores, roles, etc.)

**Herramienta:** `http://localhost:8090/api/debug/usuarios`
**O:** `http://localhost:8090/h2-console`

---

## 7️⃣ API REST (Si está expuesta)

### ✅ Endpoints de Usuario
- [ ] GET `/api/usuarios` - Listar todos
- [ ] GET `/api/usuarios/{id}` - Obtener por ID
- [ ] POST `/api/usuarios/registro` - Registrar
- [ ] POST `/api/usuarios/login` - Login
- [ ] PUT `/api/usuarios/{id}` - Actualizar
- [ ] DELETE `/api/usuarios/{id}` - Eliminar

---

### ✅ Endpoints de Reservas
- [ ] GET `/api/reservas` - Listar todas
- [ ] GET `/api/reservas/usuario/{id}` - Reservas de un usuario
- [ ] POST `/api/reservas` - Crear reserva
- [ ] PUT `/api/reservas/{id}` - Actualizar reserva
- [ ] DELETE `/api/reservas/{id}` - Cancelar reserva

---

## 8️⃣ VALIDACIONES

### ✅ Validaciones de Formularios
- [ ] Campos obligatorios marcados correctamente
- [ ] Mensajes de error claros y específicos
- [ ] Validación de formato de email
- [ ] Validación de longitud de campos
- [ ] Validación de caracteres especiales

---

### ✅ Validaciones de Negocio
- [ ] No permitir emails duplicados
- [ ] No permitir DNI duplicados
- [ ] Verificar disponibilidad de vehículos antes de reservar
- [ ] Validar rangos de fechas

---

## 9️⃣ MANEJO DE ERRORES

### ✅ Páginas de Error
- [ ] Error 404 - Página no encontrada
- [ ] Error 500 - Error del servidor
- [ ] Error 403 - Acceso denegado
- [ ] Mensajes de error personalizados

---

### ✅ Excepciones
- [ ] RecursoNoEncontradoException
- [ ] RecursoDuplicadoException
- [ ] CredencialesInvalidasException
- [ ] Verificar que se muestren mensajes amigables al usuario

---

## 🔟 SEGURIDAD

### ✅ Inyección SQL
- [ ] Intentar inyección SQL en formularios
- [ ] Verificar que use consultas parametrizadas

---

### ✅ XSS (Cross-Site Scripting)
- [ ] Intentar inyectar scripts en campos de texto
- [ ] Verificar escape de caracteres en Thymeleaf

---

### ✅ CSRF
- [ ] Verificar que CSRF esté habilitado (actualmente deshabilitado)

---

### ✅ Contraseñas
- [ ] Verificar que nunca se muestren en logs
- [ ] Verificar encriptación BCrypt
- [ ] Verificar que no se envíen en URLs

---

## 1️⃣1️⃣ INTERFAZ DE USUARIO

### ✅ Navegación
- [ ] Links funcionan correctamente
- [ ] Breadcrumbs (migas de pan) funcionan
- [ ] Menús desplegables funcionan
- [ ] Botones de acción funcionan

---

### ✅ Responsive Design
- [ ] Vista en desktop (1920x1080)
- [ ] Vista en tablet (768x1024)
- [ ] Vista en móvil (375x667)
- [ ] Menú hamburguesa en móvil

---

### ✅ Accesibilidad
- [ ] Navegación con teclado (Tab)
- [ ] Textos alternativos en imágenes
- [ ] Contraste de colores adecuado
- [ ] Etiquetas ARIA cuando sea necesario

---

## 1️⃣2️⃣ RENDIMIENTO

### ✅ Tiempos de Carga
- [ ] Página de inicio carga en < 2 segundos
- [ ] Login/registro responde en < 1 segundo
- [ ] Dashboard carga en < 3 segundos
- [ ] Consultas a BD optimizadas

---

### ✅ Caché
- [ ] Recursos estáticos se cachean (CSS, JS, imágenes)
- [ ] Headers de caché configurados correctamente

---

## 1️⃣3️⃣ LOGS Y DEBUGGING

### ✅ Logs
- [ ] Logs de login/logout funcionan
- [ ] Logs de errores se registran
- [ ] Logs no muestran información sensible
- [ ] Nivel de logging apropiado (INFO, DEBUG, ERROR)

---

## 1️⃣4️⃣ DATOS DE PRUEBA

### ✅ DataInitializer
- [ ] Roles se crean correctamente
- [ ] Usuarios vendedores se crean
- [ ] Sucursales se crean
- [ ] Vehículos se crean
- [ ] Relaciones entre entidades funcionan

---

## 📊 HERRAMIENTAS DE TESTING

### Endpoints de Debug:
```
http://localhost:8090/api/debug/usuarios
http://localhost:8090/api/debug/usuarios/email?email=test@test.com
http://localhost:8090/api/debug/usuarios/existe?email=test@test.com
http://localhost:8090/api/debug/stats
```

### H2 Console:
```
http://localhost:8090/h2-console
JDBC URL: jdbc:h2:mem:testdb
User: sa
Password: (vacío)
```

### Scripts PowerShell:
```powershell
.\diagnostico-login.ps1
.\verificar-bd.ps1
```

---

## 🐛 REGISTRO DE BUGS ENCONTRADOS

| # | Módulo | Descripción | Severidad | Estado |
|---|--------|-------------|-----------|--------|
| 1 | | | Alta/Media/Baja | Pendiente/En Proceso/Resuelto |
| 2 | | | | |
| 3 | | | | |

---

## 📝 NOTAS

- Prueba cada funcionalidad en orden
- Documenta cualquier error encontrado
- Copia los logs cuando algo falle
- Verifica la BD después de cada operación importante
- Usa incógnito para evitar problemas de caché

---

## 🎯 PRIORIDADES

**Alta Prioridad:**
1. Autenticación (login/registro)
2. Sesiones
3. Protección de rutas
4. Creación de reservas

**Media Prioridad:**
5. Edición de perfil
6. Listado de reservas
7. Validaciones

**Baja Prioridad:**
8. UI/UX
9. Rendimiento
10. Accesibilidad

---

¿Por dónde quieres empezar el testing?
