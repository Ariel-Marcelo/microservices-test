# 🏦 Sistema de Transacciones Bancarias (API REST)

Este proyecto es una solución backend para un sistema financiero que gestiona **Clientes**, **Cuentas** y **Movimientos** bancarios. Fue desarrollado utilizando **Java 17** y **Spring Boot 3**, implementando principios de **Arquitectura Limpia**, patrones de diseño y buenas prácticas de desarrollo de software.

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 17 (OpenJDK)
* **Framework:** Spring Boot 3.2.x
* **Base de Datos:** PostgreSQL 15
* **Contenerización:** Docker & Docker Compose
* **Mapeo de Objetos:** MapStruct
* **Reducción de Código:** Lombok
* **Testing:** JUnit 5, Mockito & H2 Database (In-Memory)
* **Documentación:** SpringDoc OpenAPI (Swagger)

---

## 🚀 Ejecución Microservicios RabbitMq 

A modo de demostración se realizo una versión con comunicación de microservicios por medio de colas.

### Prerrequisitos
* Docker y Docker Compose instalados.
* Bases de datos postgresql tr_cuentas y tr_clientes creadas usuario  'postgres' y contraseña 'admin' (Configurable)

### Pasos
1.  Clona el repositorio y ubícate en la raíz del proyecto.
2.  Ejecuta el siguiente comando para levantar rabbitmq con docker 

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

## Ejecución con Maven

```bash
## Correr Solución
mvn spring-boot:run

## Correr Pruebas
mvn test
```

## Reglas de Negocio

La creación o actualización de una cuenta implicará un registro de movimiento para mantener el histórico de las transacciones
Un movimiento no puede ser eliminado, solo reversado lo que implica generar otro movimiento automático que se contraponga al reversado
Un movimiento solo puede ser actualizado si es el último realizado en la cuenta para que mantenga el histórico de transacciones