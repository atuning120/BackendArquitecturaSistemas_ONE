# 🌐 Deployment en Render - Backend Spring Boot

Esta guía te ayudará a desplegar el backend dockerizado en **Render** usando PostgreSQL como base de datos externa.

## 🏗️ Arquitectura de Deployment

```
Render Web Service (Backend) ──────► PostgreSQL Database (Render)
         ↕                                    ↕
    Frontend (Render)              Variables de Entorno
```

## 📋 Prerequisitos

1. **Cuenta en Render** (render.com)
2. **Repositorio en GitHub** con el código
3. **Credenciales de MercadoPago** para producción

## 🚀 Paso a Paso: Deployment en Render

### 1. Crear Base de Datos PostgreSQL

1. Ve a tu dashboard de Render
2. Clic en **"New +"** → **"PostgreSQL"**
3. Configura:
   - **Name**: `backend-arquitectura-db`
   - **Database**: `arquitectura_db`
   - **User**: (se genera automáticamente)
   - **Plan**: Free tier (para testing)
4. Espera a que se complete la creación
5. **Guarda la URL** de conexión que aparece en el dashboard

### 2. Crear Web Service para el Backend

1. En Render dashboard: **"New +"** → **"Web Service"**
2. Conecta tu repositorio de GitHub
3. Configura el servicio:

#### Configuración Básica
- **Name**: `backend-arquitectura`
- **Runtime**: `Docker`
- **Plan**: `Free` (para testing)
- **Branch**: `main`

#### Build & Deploy Settings
- **Build Command**: 
  ```bash
  cd backArquitectura && docker build -t backend .
  ```
- **Start Command**: 
  ```bash
  docker run -p $PORT:$PORT -e PORT=$PORT -e DATABASE_URL=$DATABASE_URL -e FRONTEND_URL=$FRONTEND_URL -e MERCADOPAGO_ACCESS_TOKEN=$MERCADOPAGO_ACCESS_TOKEN backend
  ```

### 3. Configurar Variables de Entorno

En la sección **"Environment"** del Web Service, agrega:

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SPRING_PROFILES_ACTIVE` | `render` | Activa el perfil de Render |
| `DATABASE_URL` | *[URL de tu PostgreSQL]* | URL completa de la BD |
| `FRONTEND_URL` | `https://tu-frontend.onrender.com` | URL de tu frontend |
| `MERCADOPAGO_ACCESS_TOKEN` | *[Tu token real]* | Token de acceso (backend) |

> **⚠️ Importante**: 
> - Render proporciona automáticamente `PORT` y puede proporcionar `DATABASE_URL` si vinculas la BD.
> - El **PUBLIC_KEY** de MercadoPago va en el **frontend**, no en el backend.

### 4. Vinculación Automática de Base de Datos

Para vincular automáticamente la PostgreSQL:

1. En tu **Web Service** → **"Environment"**
2. Clic en **"Add from Database"**
3. Selecciona tu base de datos PostgreSQL
4. Render agregará automáticamente `DATABASE_URL`

## 🔧 Configuración del Dockerfile para Render

El `Dockerfile` ya está optimizado para Render:

```dockerfile
# Puerto dinámico de Render
EXPOSE $PORT

# Variables de entorno para Render
ENV SPRING_PROFILES_ACTIVE=render

# Comando que usa el puerto dinámico
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
```

## 📝 Configuración de Spring para Render

El archivo `application-render.properties` está configurado para:

```properties
# Puerto dinámico
server.port=${PORT:8080}

# Base de datos externa
spring.datasource.url=${DATABASE_URL}

# CORS para frontend
cors.allowed-origins=${FRONTEND_URL:http://localhost:3000}

# MercadoPago (solo access token para backend)
mercadopago.access-token=${MERCADOPAGO_ACCESS_TOKEN}
```

## 🧪 Testing Local con Configuración de Render

Para probar localmente con la misma configuración:

```bash
# 1. Configura las variables en backArquitectura/.env
PORT=8080
DATABASE_URL=postgresql://user:password@localhost:5432/db_name
FRONTEND_URL=http://localhost:3000
MERCADOPAGO_ACCESS_TOKEN=tu_token_test

# 2. Ejecuta solo el backend
docker-compose up backend

# O con PostgreSQL local para desarrollo
docker-compose --profile dev up
```

## 🔍 Verificación del Deployment

### 1. Health Check
```bash
curl https://tu-backend.onrender.com/actuator/health
```

### 2. Verificar Conectividad
- **Backend**: `https://tu-backend.onrender.com`
- **Base de datos**: Verificar logs en Render dashboard
- **CORS**: Probar desde tu frontend

### 3. Logs en Render
- Ve a tu Web Service en Render
- Sección **"Logs"** para ver errores
- Buscar mensajes de Spring Boot startup

## 🚨 Troubleshooting

### Error: "Application failed to start"
```bash
# Verificar variables de entorno en Render dashboard
# Especialmente DATABASE_URL y MERCADOPAGO_ACCESS_TOKEN
```

### Error: "Connection refused to database"
```bash
# Verificar que DATABASE_URL esté correcta
# Formato: postgresql://user:password@host:port/database
```

### Error: "Port already in use"
```bash
# Render maneja esto automáticamente
# Verificar que uses $PORT en el Dockerfile
```

### Error: CORS
```bash
# Verificar FRONTEND_URL en variables de entorno
# Debe coincidir exactamente con la URL de tu frontend
```

## ⚡ Optimizaciones para Render

### 1. Dockerfile Multi-stage
Ya implementado para reducir tamaño de imagen:
```dockerfile
FROM maven:3.9.9-openjdk-17-slim AS build
# ... build stage

FROM openjdk:17-jdk-slim
# ... runtime stage
```

### 2. Health Checks
Configurados para que Render detecte cuando la app está lista:
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:$PORT/actuator/health"]
```

### 3. Usuario No-Root
Por seguridad:
```dockerfile
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring
```

## 📚 Recursos Adicionales

- [Render Documentation](https://render.com/docs)
- [Spring Boot Deployment Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [PostgreSQL on Render](https://render.com/docs/databases)

## 🆘 Soporte

Si tienes problemas:

1. **Revisar logs** en Render dashboard
2. **Verificar variables** de entorno
3. **Probar localmente** con la misma configuración
4. **Verificar conectividad** de la base de datos