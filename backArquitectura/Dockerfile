# Dockerfile multietapa para Spring Boot con Maven

# Etapa 1: Build
FROM maven:3.9.9-openjdk-17-slim AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (para aprovechar la cache de Docker)
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Descargar dependencias (esta capa se cachea si no cambia el pom.xml)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM openjdk:17-jdk-slim

# Crear usuario no-root para seguridad
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar la propiedad del archivo al usuario spring
RUN chown spring:spring app.jar

# Cambiar al usuario spring
USER spring

# Exponer el puerto dinámico de Render
EXPOSE $PORT

# Variables de entorno con valores por defecto para Render
ENV SPRING_PROFILES_ACTIVE=render
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando para ejecutar la aplicación con puerto dinámico de Render
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]