# 🎓 Guía Git para el Equipo - Proyecto Reserva de Vehículos

## 📌 Información del Proyecto

**Repositorio:**
```
https://github.com/GersonVala/Sistema-De-Gestion-de-Reserva-de-Vehiculos.git
```

**Líder del Proyecto:** Gerson Vala (@GersonVala)

---

## 🎯 ¿Qué es Git y por qué lo usamos?

Git es un sistema que nos permite:
- ✅ Trabajar en el mismo proyecto sin pisarnos
- ✅ Ver quién hizo cada cambio y cuándo
- ✅ Volver atrás si algo se rompe
- ✅ Revisar el código antes de agregarlo al proyecto principal

**Piensa en Git como un Google Docs para código, pero mucho mejor.**

---

## 🌳 Estructura de Ramas (Branches)

```
main (producción) ← Código 100% funcional, nadie toca directamente
  ↑
develop (desarrollo) ← Aquí se junta el trabajo de todos
  ↑
feature/mi-tarea ← Aquí trabajas TÚ en tu tarea específica
```

### ⚠️ REGLA DE ORO

**NUNCA, JAMÁS, BAJO NINGUNA CIRCUNSTANCIA hacer push directo a `main` o `develop`**

Siempre trabajas en tu propia rama `feature/`.

---

## 🚀 PARTE 1: Configuración Inicial (Solo la primera vez)

### Paso 1: Instalar Git

**Windows:**
1. Descargar desde: https://git-scm.com/download/win
2. Instalar con opciones por defecto
3. Reiniciar la computadora

**Verificar instalación:**
```bash
git --version
```

Deberías ver: `git version 2.43.0` (o similar)

---

### Paso 2: Configurar tu identidad

⚠️ **IMPORTANTE:** Usa tu nombre real y email real.

```bash
git config --global user.name "Tu Nombre Completo"
git config --global user.email "tu.email@gmail.com"
```

**Ejemplo:**
```bash
git config --global user.name "Juan Perez"
git config --global user.email "juan.perez@gmail.com"
```

**Verificar que se guardó:**
```bash
git config --global --list
```

---

### Paso 3: Crear cuenta en GitHub

1. Ir a: https://github.com
2. Click en **Sign up**
3. Crear cuenta con tu email
4. **Enviar tu usuario de GitHub a Gerson** para que te agregue al proyecto

---

### Paso 4: Aceptar invitación al proyecto

1. Revisa tu email
2. Busca email de GitHub: "You've been added to a repository"
3. Click en **View invitation**
4. Click en **Accept invitation**

---

### Paso 5: Clonar el proyecto en tu computadora

#### 5.1. Abrir Git Bash (Terminal)

**Windows:** 
- Busca "Git Bash" en el menú inicio
- Click derecho → Ejecutar como administrador

#### 5.2. Crear carpeta para tus proyectos

```bash
# Ir al escritorio
cd ~/Desktop

# Crear carpeta (opcional, puedes usar otra ubicación)
mkdir ProyectosProgramacion
cd ProyectosProgramacion
```

#### 5.3. Clonar el repositorio

```bash
git clone https://github.com/GersonVala/Sistema-De-Gestion-de-Reserva-de-Vehiculos.git
```

⏳ **Espera a que descargue...** (puede tardar 1-2 minutos)

#### 5.4. Entrar a la carpeta del proyecto

```bash
cd Sistema-De-Gestion-de-Reserva-de-Vehiculos
```

#### 5.5. Verificar que estás en la rama correcta

```bash
git branch
```

**Deberías ver:**
```
* develop
```

**Si ves `main` en lugar de `develop`:**
```bash
git checkout develop
```

---

## 🎯 PARTE 2: Flujo Diario de Trabajo

### 📋 Resumen rápido (memoriza esto):

```bash
# 1. Actualizar develop
git checkout develop
git pull origin develop

# 2. Crear tu rama
git checkout -b feature/nombre-de-tu-tarea

# 3. Trabajar en tu código...

# 4. Guardar cambios
git add .
git commit -m "feat: Descripción de lo que hiciste"

# 5. Subir tu rama
git push origin feature/nombre-de-tu-tarea

# 6. Crear Pull Request en GitHub
```

---

### 📝 Flujo detallado paso a paso

#### Paso 1: Antes de empezar CUALQUIER tarea nueva

```bash
# Ir a la rama develop
git checkout develop

# Traer los últimos cambios del equipo
git pull origin develop
```

**¿Por qué?** Para asegurarte de tener la versión más reciente del código.

---

#### Paso 2: Crear tu rama de trabajo

```bash
git checkout -b feature/nombre-descriptivo
```

**Ejemplos de nombres de ramas:**
- `feature/crear-modelo-vehiculo`
- `feature/agregar-controlador-reservas`
- `feature/disenar-pagina-login`
- `fix/corregir-error-fecha`

**Reglas para nombres:**
- ✅ Todo en minúsculas
- ✅ Separar palabras con guiones `-`
- ✅ Usar `feature/` para nuevas funcionalidades
- ✅ Usar `fix/` para correcciones de errores
- ❌ No usar espacios
- ❌ No usar tildes ni ñ

---

#### Paso 3: Trabajar en tu código

Abre el proyecto en IntelliJ IDEA y haz tus cambios:
- Crear archivos nuevos
- Modificar código existente
- Agregar dependencias en `pom.xml`
- etc.

**💡 Consejo:** Haz commits pequeños y frecuentes, no esperes a terminar todo.

---

#### Paso 4: Ver qué archivos cambiaste

```bash
git status
```

Verás algo como:
```
Changes not staged for commit:
  modified:   src/main/java/entity/Vehiculo.java
  
Untracked files:
  src/main/java/repository/VehiculoRepository.java
```

**Significado de colores:**
- 🔴 **Rojo** = Archivos modificados o nuevos (aún no guardados)
- 🟢 **Verde** = Archivos listos para commit

---

#### Paso 5: Agregar archivos al "staging area"

```bash
# Agregar TODOS los archivos modificados
git add .

# O agregar archivos específicos
git add src/main/java/entity/Vehiculo.java
```

**Verificar que se agregaron (deberían estar en verde):**
```bash
git status
```

---

#### Paso 6: Hacer commit (guardar cambios localmente)

```bash
git commit -m "feat: Agregar entidad Vehiculo"
```

**Convención de mensajes:**
- `feat:` → Nueva funcionalidad
- `fix:` → Corrección de bug
- `docs:` → Documentación
- `refactor:` → Refactorizar código sin cambiar funcionalidad
- `test:` → Agregar tests

**Ejemplos de buenos commits:**
```bash
git commit -m "feat: Agregar modelo Vehiculo con atributos básicos"
git commit -m "fix: Corregir validación de fecha en Reserva"
git commit -m "docs: Actualizar README con instrucciones de instalación"
```

**Ejemplos de malos commits:**
```bash
git commit -m "cambios"  ❌ No dice qué cambió
git commit -m "fix"      ❌ No dice qué arregló
git commit -m "asdfgh"   ❌ No tiene sentido
```

---

#### Paso 7: Subir tu rama a GitHub

```bash
git push origin feature/nombre-de-tu-tarea
```

**Ejemplo:**
```bash
git push origin feature/crear-modelo-vehiculo
```

**⚠️ Primera vez:** Te pedirá usuario y contraseña de GitHub.

**Si pide contraseña cada vez:** Puedes configurar cache de credenciales:
```bash
git config --global credential.helper cache
```

---

#### Paso 8: Crear Pull Request en GitHub

##### 8.1. Abrir GitHub

Ve a: https://github.com/GersonVala/Sistema-De-Gestion-de-Reserva-de-Vehiculos

##### 8.2. Verás un banner amarillo

```
feature/tu-rama had recent pushes
[Compare & pull request]
```

Haz click en **Compare & pull request**

##### 8.3. Verificar configuración

**Asegúrate que diga:**
- **Base:** `develop` ← **Compare:** `feature/tu-rama`

**Si dice `main` en base, cámbialo a `develop`**

##### 8.4. Llenar el formulario del PR

**Título:**
```
feat: Agregar modelo Vehiculo
```

**Descripción:**
```markdown
## ¿Qué hace este PR?
- Crea la entidad Vehiculo con sus atributos
- Agrega validaciones básicas
- Incluye getters y setters

## ¿Cómo probarlo?
1. Compilar el proyecto
2. Verificar que no hay errores

## Checklist
- [x] El código compila sin errores
- [x] Probé mis cambios
- [x] Actualicé la documentación si era necesario
```

##### 8.5. Asignar revisor

En la columna derecha:
- **Reviewers:** Selecciona a **GersonVala** (o al compañero que te asignaron)
- **Assignees:** Selecciónate a ti mismo

##### 8.6. Crear el PR

Click en **Create pull request**

---

#### Paso 9: Esperar aprobación

1. Recibirás notificación por email cuando revisen tu código
2. Puede que te pidan hacer cambios
3. Si te piden cambios, haz lo siguiente:

```bash
# Hacer los cambios en tu código...

# Agregar y hacer commit
git add .
git commit -m "fix: Corregir validaciones según feedback"

# Subir cambios (se actualizará automáticamente el PR)
git push origin feature/tu-rama
```

---

#### Paso 10: Después de que aprueben y mergeen tu PR

```bash
# Volver a develop
git checkout develop

# Traer cambios (incluye tu código ya mergeado)
git pull origin develop

# Borrar tu rama local (ya no la necesitas)
git branch -d feature/tu-rama
```

---

## 🆘 Comandos de Emergencia

### ❓ No sé en qué rama estoy

```bash
git branch
```

El que tenga `*` es donde estás.

---

### 🔄 Quiero volver a develop

```bash
git checkout develop
```

---

### 📥 Quiero traer los cambios más recientes

```bash
git checkout develop
git pull origin develop
```

---

### 👀 ¿Qué archivos modifiqué?

```bash
git status
```

---

### ❌ Quiero deshacer cambios en un archivo

```bash
# Deshacer cambios en un archivo específico
git checkout -- NombreDelArchivo.java

# Ejemplo:
git checkout -- src/main/java/entity/Vehiculo.java
```

---

### 🗑️ Quiero borrar TODO y empezar de cero

⚠️ **CUIDADO:** Esto borra TODOS tus cambios locales.

```bash
git checkout develop
git reset --hard origin/develop
git clean -fd
```

---

### 🔍 Ver historial de commits

```bash
git log --oneline
```

Para salir del log, presiona `q`.

---

### 📋 Ver diferencias antes de hacer commit

```bash
git diff
```

---

## 🚨 Errores Comunes y Soluciones

### Error: "Your branch is behind 'origin/develop'"

**Solución:**
```bash
git pull origin develop
```

---

### Error: "You have unmerged paths"

**Significa:** Tienes un conflicto de merge.

**Solución:**
1. Abre los archivos que marca como conflicto
2. Busca las líneas con `<<<<<<<`, `=======`, `>>>>>>>`
3. Edita manualmente y deja solo el código correcto
4. Luego:
```bash
git add .
git commit -m "Resolver conflictos"
```

---

### Error: "fatal: not a git repository"

**Significa:** No estás dentro de la carpeta del proyecto.

**Solución:**
```bash
cd ~/Desktop/ProyectosProgramacion/Sistema-De-Gestion-de-Reserva-de-Vehiculos
```

---

### Error: "Permission denied (publickey)"

**Significa:** Problema de autenticación.

**Solución:** Usa HTTPS en lugar de SSH:
```bash
git remote set-url origin https://github.com/GersonVala/Sistema-De-Gestion-de-Reserva-de-Vehiculos.git
```

---

## 📊 Ejemplo Completo de Una Tarea

### Tarea: Crear la entidad Cliente

```bash
# 1. Actualizar develop
git checkout develop
git pull origin develop

# 2. Crear rama
git checkout -b feature/crear-entidad-cliente

# 3. Crear el archivo Cliente.java en IntelliJ
# (Escribir el código...)

# 4. Ver qué cambió
git status

# 5. Agregar archivos
git add .

# 6. Hacer commit
git commit -m "feat: Agregar entidad Cliente con atributos básicos"

# 7. Subir rama
git push origin feature/crear-entidad-cliente

# 8. Ir a GitHub y crear Pull Request
# (Seguir pasos del Paso 8 arriba)

# 9. Esperar aprobación...

# 10. Después de mergear
git checkout develop
git pull origin develop
git branch -d feature/crear-entidad-cliente
```

---

## 📞 ¿Necesitas Ayuda?

### Antes de preguntar, intenta:

1. ✅ Leer el error completo en la terminal
2. ✅ Buscar el error en Google: "git [error que aparece]"
3. ✅ Revisar esta guía nuevamente

### Si aún necesitas ayuda:

1. Toma captura de pantalla del error
2. Copia el comando que ejecutaste
3. Envía en el grupo de WhatsApp/Telegram:
   ```
   🆘 Ayuda con Git
   
   Comando que ejecuté:
   git push origin feature/mi-rama
   
   Error que me sale:
   [Pegar captura o texto del error]
   
   ¿Qué puedo hacer?
   ```

---

## ✅ Checklist Personal

### Primera vez (configuración):
- [ ] Instalé Git
- [ ] Configuré mi nombre y email
- [ ] Creé cuenta en GitHub
- [ ] Acepté invitación al repositorio
- [ ] Cloné el proyecto
- [ ] Verifiqué que estoy en rama `develop`

### Antes de cada tarea:
- [ ] `git checkout develop`
- [ ] `git pull origin develop`
- [ ] `git checkout -b feature/mi-tarea`

### Después de trabajar:
- [ ] `git add .`
- [ ] `git commit -m "mensaje descriptivo"`
- [ ] `git push origin feature/mi-tarea`
- [ ] Crear Pull Request en GitHub
- [ ] Asignar a revisor

### Después de mergear:
- [ ] `git checkout develop`
- [ ] `git pull origin develop`
- [ ] `git branch -d feature/mi-tarea`

---

## 🎓 Recursos para Aprender Más

### Videos recomendados (YouTube):
- **"Git y GitHub para Principiantes"** - Fazt Code (30 min)
- **"Git en 15 minutos"** - Platzi
- **"Pull Request explicado fácil"** - MoureDev

### Lecturas:
- Documentación oficial de Git: https://git-scm.com/doc
- GitHub Guides: https://guides.github.com/

---

## 📝 Glosario de Términos

- **Repository (Repo):** El proyecto completo en GitHub
- **Clone:** Descargar una copia del repositorio a tu computadora
- **Branch (Rama):** Una versión paralela del código
- **Commit:** Guardar cambios localmente con un mensaje
- **Push:** Subir commits de tu computadora a GitHub
- **Pull:** Traer cambios de GitHub a tu computadora
- **Pull Request (PR):** Solicitud para que tu código se agregue a `develop`
- **Merge:** Integrar tu código a otra rama
- **Conflict (Conflicto):** Cuando dos personas editaron la misma línea de código

---

## 🎯 Reglas del Equipo

1. ✅ **Siempre trabajar en rama `feature/`**
2. ✅ **Hacer commits descriptivos**
3. ✅ **Crear Pull Request cuando termines**
4. ✅ **Esperar aprobación antes de mergear**
5. ✅ **Actualizar `develop` antes de empezar nueva tarea**
6. ✅ **Borrar ramas locales después de mergear**
7. ❌ **NUNCA hacer push directo a `main` o `develop`**
8. ❌ **NUNCA hacer `git push --force`**
9. ❌ **NUNCA borrar ramas de otros compañeros**

---

## 📅 Workflow Semanal Recomendado

### Lunes:
- Reunión de equipo: asignar tareas de la semana
- Cada uno crea su rama `feature/`

### Martes - Jueves:
- Trabajar en tu tarea
- Hacer commits frecuentes
- Crear PR cuando termines

### Viernes:
- Revisar PRs pendientes
- Mergear código aprobado
- Actualizar `develop`
- Planeación de la siguiente semana

---

## 🏆 Buenas Prácticas

### Commits:
- ✅ Hacer commits pequeños y frecuentes
- ✅ Un commit = una funcionalidad/cambio lógico
- ✅ Mensaje claro que explique QUÉ cambió

### Ramas:
- ✅ Una rama por tarea
- ✅ Nombres descriptivos
- ✅ Borrar después de mergear

### Pull Requests:
- ✅ Agregar descripción detallada
- ✅ Probar antes de crear el PR
- ✅ Responder a comentarios de revisión

### Código:
- ✅ Probar que compila antes de hacer commit
- ✅ Seguir convenciones del proyecto
- ✅ Comentar código complejo

---

**¡Éxito en el proyecto! 🚀**

*Última actualización: Octubre 2024*
*Autor: Gerson Vala*

