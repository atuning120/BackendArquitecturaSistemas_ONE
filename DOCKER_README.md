# 🐳 Backend Dockerizado - Arquitectura de Sistemas

Este documento contiene las instrucciones para ejecutar el backend de la aplicación usando Docker.

## 📋 Prerrequisitos

- Docker Desktop instalado y funcionando
- Docker Compose (incluido con Docker Desktop)
- Git (para clonar el repositorio)

## 🚀 Inicio Rápido

### 1. Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto copiando el archivo de ejemplo:

```bash
cp .env.example .env
```

Edita el archivo `.env` y configura las siguientes variables importantes:

```env
# Configuración de Base de Datos
DB_NAME=arquitectura_db
DB_USER=postgres
DB_PASSWORD=tu_password_seguro

# Configuración de MercadoPago (OBLIGATORIO)
MERCADOPAGO_ACCESS_TOKEN=tu_access_token_real
MERCADOPAGO_PUBLIC_KEY=tu_public_key_real
```

### 2. Ejecutar la Aplicación

#### Opción 1: Script Automático (Recomendado)

**En Windows:**
```cmd
start-docker.bat
```

**En Linux/Mac:**
```bash
chmod +x start-docker.sh
./start-docker.sh
```

#### Opción 2: Comandos Docker Compose

```bash
# Construir y levantar servicios
docker-compose up --build -d

# Ver logs
docker-compose logs -f backend

# Ver estado de servicios
docker-compose ps
```

### 3. Verificar la Instalación

- **Backend API**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Base de Datos**: localhost:5432

## 🛠️ Comandos Útiles

### Gestión de Servicios

```bash
# Iniciar servicios
docker-compose up -d

# Detener servicios
docker-compose down

# Reiniciar servicios
docker-compose restart

# Ver logs en tiempo real
docker-compose logs -f backend
docker-compose logs -f postgres

# Reconstruir imágenes
docker-compose build --no-cache
```

### Desarrollo y Debugging

```bash
# Conectarse al contenedor del backend
docker-compose exec backend bash

# Conectarse a la base de datos
docker-compose exec postgres psql -U postgres -d arquitectura_db

# Ver variables de entorno del contenedor
docker-compose exec backend env
```

### Limpieza

```bash
# Detener y eliminar contenedores, redes
docker-compose down

# Eliminar también volúmenes (⚠️ ELIMINA DATOS DE LA BD)
docker-compose down -v

# Limpiar imágenes no utilizadas
docker system prune
```

## 📁 Estructura de Archivos Docker

```
BackendArquitecturaSistemas_ONE/
├── docker-compose.yml          # Orquestación de servicios
├── .env.example               # Plantilla de variables de entorno
├── .env                      # Variables de entorno (crear desde .env.example)
├── start-docker.sh           # Script de inicio para Linux/Mac
├── start-docker.bat          # Script de inicio para Windows
└── backArquitectura/
    ├── Dockerfile            # Imagen del backend
    ├── .dockerignore        # Archivos a ignorar en build
    └── src/main/resources/
        └── application-docker.properties  # Config para Docker
```

## 🔧 Configuración Avanzada

### Variables de Entorno Disponibles

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_NAME` | Nombre de la base de datos | `arquitectura_db` |
| `DB_USER` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `password` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `BACKEND_PORT` | Puerto del backend | `8080` |
| `MERCADOPAGO_ACCESS_TOKEN` | Token de acceso de MercadoPago | *(requerido)* |
| `MERCADOPAGO_PUBLIC_KEY` | Clave pública de MercadoPago | *(requerido)* |

### Personalizar Configuraciones

1. **Modificar recursos de Java**: Edita `JAVA_OPTS` en `docker-compose.yml`
2. **Cambiar configuración de BD**: Modifica las variables de entorno en `.env`
3. **Personalizar aplicación**: Edita `application-docker.properties`

## 🐛 Solución de Problemas

### El backend no se conecta a la base de datos

1. Verifica que PostgreSQL esté corriendo:
   ```bash
   docker-compose ps postgres
   ```

2. Revisa los logs de PostgreSQL:
   ```bash
   docker-compose logs postgres
   ```

3. Verifica las variables de entorno:
   ```bash
   docker-compose exec backend env | grep DB_
   ```

### Error de permisos en Linux

```bash
sudo chown -R $USER:$USER .
chmod +x start-docker.sh
```

### Puerto en uso

Si el puerto 8080 está ocupado, cambia `BACKEND_PORT` en `.env`:
```env
BACKEND_PORT=8081
```

### Problemas con MercadoPago

Verifica que las credenciales en `.env` sean correctas:
- `MERCADOPAGO_ACCESS_TOKEN`
- `MERCADOPAGO_PUBLIC_KEY`

### Limpiar y empezar de nuevo

```bash
docker-compose down -v
docker system prune -f
docker-compose up --build
```

## 📈 Monitoreo y Logs

### Health Checks

Los servicios incluyen health checks automáticos:

- **Backend**: Verifica endpoint `/actuator/health`
- **PostgreSQL**: Verifica conexión con `pg_isready`

### Logs Estructurados

```bash
# Logs con timestamp
docker-compose logs -f -t backend

# Últimas 100 líneas
docker-compose logs --tail=100 backend

# Logs desde hace 1 hora
docker-compose logs --since=1h backend
```

## 🚀 Despliegue en Producción

Para producción, considera:

1. **Usar secrets para credenciales sensibles**
2. **Configurar reverse proxy (nginx)**
3. **Implementar SSL/TLS**
4. **Configurar backup automático de BD**
5. **Usar registros de contenedores privados**

---

## 📞 Soporte

Para problemas específicos:

1. Revisa los logs: `docker-compose logs backend`
2. Verifica el health check: `curl http://localhost:8080/actuator/health`
3. Consulta la documentación de Spring Boot
4. Revisa la configuración de MercadoPago