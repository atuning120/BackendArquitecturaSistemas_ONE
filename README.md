# BackendArquitecturaSistemas_ONE

## 🚀 Instrucciones de Ejecución

Para ejecutar este backend de Spring Boot, utiliza el siguiente comando en la terminal desde el directorio del proyecto:

```bash
.\mvnw.cmd spring-boot:run
```

### Requisitos previos:
- Java 17 o superior
- PostgreSQL instalado y ejecutándose
- Archivo `.env` configurado con las credenciales de la base de datos

### Configuración:
1. Asegúrate de tener PostgreSQL ejecutándose en tu sistema
2. Configura las variables de entorno en el archivo `.env` (ubicado en la raíz del proyecto)
3. Ejecuta el comando de arriba
4. La aplicación estará disponible en `http://localhost:8080`

## 📚 API Endpoints

### Base URL: `http://localhost:8080`

| Método | Endpoint | Descripción | Respuesta |
|--------|----------|-------------|-----------|
| `GET` | `/users` | Obtener todos los usuarios | `200 OK` |
| `GET` | `/users/{id}` | Obtener usuario por ID | `200 OK` / `404 Not Found` |
| `POST` | `/users` | Crear nuevo usuario | `201 Created` |
| `PUT` | `/users/{id}` | Actualizar usuario | `200 OK` / `404 Not Found` |
| `DELETE` | `/users/{id}` | Eliminar usuario | `204 No Content` / `404 Not Found` |

### Estructura del UserDTO:
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@example.com"
}
```

### Ejemplos rápidos:
```bash
# Obtener todos los usuarios
curl http://localhost:8080/users

# Crear usuario
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana López","email":"ana@example.com"}'

# Obtener usuario por ID
curl http://localhost:8080/users/1

# Actualizar usuario
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana López Updated","email":"ana.updated@example.com"}'

# Eliminar usuario
curl -X DELETE http://localhost:8080/users/1
```