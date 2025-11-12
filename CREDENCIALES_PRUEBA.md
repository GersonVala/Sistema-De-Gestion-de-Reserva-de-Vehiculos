# 🔐 Credenciales de Prueba - Sistema de Reserva de Vehículos

## 📋 Usuarios de Prueba

### 👨‍💼 Administrador
- **Email**: `admin@tgsmax.com`
- **Contraseña**: `password`
- **Acceso**: Panel de administración, crear vendedores

### 🏪 Vendedores
| Nombre | Email | Contraseña | Sucursal |
|--------|-------|------------|----------|
| Juan Pérez | `juan@tgsmax.com` | `password` | Buenos Aires |
| María González | `maria@tgsmax.com` | `password` | Córdoba |
| Carlos López | `carlos@tgsmax.com` | `password` | Mendoza |

**Permisos**: Aprobar/rechazar reservas, iniciar alquileres, completar devoluciones

### 👥 Clientes
| Nombre | Email | Contraseña |
|--------|-------|------------|
| Pedro Ramírez | `pedro@email.com` | `password` |
| Laura Fernández | `laura@email.com` | `password` |
| Roberto Silva | `roberto@email.com` | `password` |

**Permisos**: Crear reservas, ver mis reservas

## 🚀 Cómo Ejecutar

### Opción 1: Desde VS Code (Recomendado)
1. Abre el archivo `ProyectoReservaDeVehiculosApplication.java`
2. Presiona `F5` o haz clic en el botón "Run" (▶️)
3. Espera a que aparezca el mensaje: `Started ProyectoReservaDeVehiculosApplication`
4. Abre tu navegador en: `http://localhost:8090`

### Opción 2: Desde Terminal
```powershell
.\mvnw.cmd spring-boot:run
```

## 🌐 URLs de Acceso

- **Página Principal**: http://localhost:8090
- **Login**: http://localhost:8090/login
- **Dashboard Admin**: http://localhost:8090/admin/dashboard
- **Dashboard Vendedor**: http://localhost:8090/vendedor/dashboard
- **Dashboard Cliente**: http://localhost:8090/dashboard
- **Consola H2**: http://localhost:8090/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Usuario: `sa`
  - Contraseña: (dejar vacío)

## ✅ Datos Pre-cargados

Al iniciar la aplicación, se cargan automáticamente:
- ✅ 7 usuarios (1 admin + 3 vendedores + 3 clientes)
- ✅ 10 direcciones únicas
- ✅ 6 ciudades
- ✅ 3 sucursales (Buenos Aires, Córdoba, Mendoza)
- ✅ 3 tipos de vehículos
- ✅ 9 vehículos disponibles

## 🔍 Flujos de Prueba

### Flujo Admin:
1. Login con `admin@tgsmax.com` / `password`
2. Ir a "Gestión de Vendedores"
3. Crear un nuevo vendedor
4. Ver lista de vendedores

### Flujo Vendedor:
1. Login con `juan@tgsmax.com` / `password`
2. Ver métricas del dashboard
3. Aprobar/Rechazar reservas pendientes
4. Iniciar alquiler de reservas confirmadas
5. Completar devoluciones

### Flujo Cliente:
1. Login con `pedro@email.com` / `password`
2. Ver vehículos disponibles
3. Crear una nueva reserva
4. Ver mis reservas

## ⚠️ Nota Importante
- La base de datos es **H2 en memoria** (se reinicia al cerrar la aplicación)
- Todas las contraseñas están encriptadas con BCrypt
- **NO usar JavaScript** - Todo funciona con Thymeleaf + Bootstrap únicamente
