Tienda — API REST de e-commerce

API REST de una mini tienda construida con Java 21 y Spring Boot 4, con autenticación JWT, control de acceso por roles, persistencia en PostgreSQL y despliegue con Docker. Proyecto de aprendizaje backend con foco en buenas prácticas y diseño limpio.

Tecnologías
Java 21 (LTS)
Spring Boot 4.1 — Spring Web (MVC), Spring Data JPA, Spring Security
PostgreSQL 16 como base de datos
Hibernate como ORM
JWT (JJWT) para autenticación stateless
BCrypt para el hashing de contraseñas
JUnit 5 + Mockito para las pruebas
Docker y Docker Compose para la infraestructura
Maven como herramienta de construcción
Características
CRUD completo de productos con los verbos HTTP correctos y sus códigos de estado (200, 201, 204, 404).
Patrón DTO con record de Java: separa el modelo de la API del modelo de persistencia.
Validación declarativa de los datos de entrada en la frontera de la API.
Manejo centralizado de errores con @RestControllerAdvice: respuestas de error limpias y consistentes.
Autenticación con JWT: login stateless que emite un token firmado.
Autorización por roles: los usuarios pueden leer el catálogo, pero solo un ADMIN puede crear, actualizar o borrar productos.
Contraseñas hasheadas con BCrypt, nunca en texto plano.
Cobertura de pruebas: pruebas unitarias del servicio (Mockito) y de integración de la capa web (@WebMvcTest + MockMvc).
Arquitectura por capas organizada en paquetes por funcionalidad (producto, usuario, seguridad, shared).
Requisitos previos

Gracias a Docker, solo necesitas tener instalado:

Docker y Docker Compose

No necesitas instalar Java, Maven ni PostgreSQL en tu máquina: todo corre en contenedores.

Cómo ejecutarlo

Clona el repositorio y levanta todo el sistema con un solo comando:

bash
git clone https://github.com/Estivencr/Mi-tienda
cd Mi-tienda
docker compose up --build

Esto levanta dos contenedores: la aplicación Spring Boot y una base de datos PostgreSQL, conectados entre sí y listos para usar. La API queda disponible en http://localhost:8080.

Al arrancar, se crean automáticamente dos usuarios de prueba:

Rol	Email	Contraseña
ADMIN	admin@tienda.com	password123
USER	cliente@tienda.com	cliente123
Endpoints de la API
Autenticación
Verbo	Ruta	Descripción	Acceso
POST	/auth/login	Inicia sesión y devuelve un token JWT	Público
Productos
Verbo	Ruta	Descripción	Acceso
GET	/api/productos	Lista todos los productos	Autenticado
GET	/api/productos/{id}	Obtiene un producto por id	Autenticado
POST	/api/productos	Crea un producto	Solo ADMIN
PUT	/api/productos/{id}	Actualiza un producto	Solo ADMIN
DELETE	/api/productos/{id}	Elimina un producto	Solo ADMIN

Para los endpoints protegidos, incluye el token en la cabecera:

Authorization: Bearer <token>
Arquitectura

El proyecto sigue una arquitectura por capas (controlador → servicio → repositorio) y se organiza en paquetes por funcionalidad, donde cada paquete representa un área del dominio:

producto — gestión del catálogo de productos
usuario — entidad y repositorio de usuarios
seguridad — configuración de Spring Security, JWT y login
shared — código transversal (manejo de errores, excepciones comunes)

Esta organización mantiene las fronteras del dominio claras y sienta las bases para una eventual evolución hacia microservicios.

Próximas mejoras
Gestión de pedidos (con su propio agregado y reglas de negocio).
Externalización de secretos mediante variables de entorno.
Migraciones de base de datos versionadas con Flyway.
Documentación interactiva de la API con OpenAPI / Swagger.
Autor

Estiven Cano — https://github.com/Estivencr
