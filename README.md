# Sistema de Gestión para Alquiler de Equipos Deportivos en Destinos Turísticos

Este proyecto implementa un sistema de gestión completo para el alquiler de equipos deportivos en destinos turísticos de Colombia. Permite la administración del inventario, reservas, clientes, destinos y tipos de equipos deportivos, con un sistema de autenticación basado en roles.

## Características

- **Gestión de Inventario**: Administración completa de equipos deportivos (agregar, modificar, eliminar, consultar)
- **Gestión de Reservas**: Creación y manejo de reservas de equipos para clientes en destinos específicos
- **Gestión de Clientes**: Registro y administración de información de clientes
- **Gestión de Destinos**: Administración de destinos turísticos donde se ofrecen los servicios
- **Gestión de Usuarios**: Sistema de control de acceso con roles (administrador y trabajador)
- **Interfaz Gráfica**: Interfaz amigable desarrollada con Java Swing

## Tecnologías Utilizadas

- **Lenguaje**: Java 8
- **Base de Datos**: MySQL
- **Arquitectura**: Modelo-Vista-Controlador (MVC)
- **Persistencia**: JDBC para conexión a base de datos
- **Interfaz Gráfica**: Swing

## Estructura del Proyecto

```
src/main/java/com/deportur/
├── App.java (Punto de entrada de la aplicación)
├── config/
│   └── ConexionDB.java (Configuración de conexión a la base de datos)
├── controlador/
│   ├── InventarioController.java
│   ├── ReservasController.java
│   └── UsuarioController.java
├── dao/
│   ├── ClienteDAO.java
│   ├── DestinoTuristicoDAO.java
│   ├── DetalleReservaDAO.java
│   ├── EquipoDeportivoDAO.java
│   ├── ReservaDAO.java
│   ├── TipoEquipoDAO.java
│   └── UsuarioDAO.java
├── modelo/
│   ├── Cliente.java
│   ├── DestinoTuristico.java
│   ├── DetalleReserva.java
│   ├── EquipoDeportivo.java
│   ├── Reserva.java
│   ├── TipoEquipo.java
│   └── Usuario.java
├── servicio/
│   ├── GestionInventarioService.java
│   ├── GestionReservasService.java
│   └── GestionUsuariosService.java
└── vista/
    ├── LoginFrame.java
    ├── MainFrame.java
    ├── PanelClientes.java
    ├── PanelDestinos.java
    ├── PanelInventario.java
    ├── PanelReservas.java
    ├── PanelTiposEquipo.java
    └── PanelUsuarios.java
```

## Diagrama de Base de Datos

El sistema utiliza las siguientes tablas:

- **tipo_equipo**: Categorías de equipos deportivos
- **destino_turistico**: Lugares donde se ofrecen los servicios
- **equipo_deportivo**: Inventario de equipos disponibles
- **cliente**: Información de clientes
- **reserva**: Registro de reservas
- **detalle_reserva**: Detalles de equipos incluidos en cada reserva
- **usuario**: Usuarios del sistema con sus roles

## Configuración e Instalación

1. **Requisitos Previos**:
   - Java 8 o superior
   - MySQL 5.7 o superior
   - Maven 3.6 o superior

2. **Configuración de la Base de Datos**:
   - Ejecutar el script SQL `deportur_db.sql` para crear la base de datos y tablas
   - Configurar las credenciales de conexión en `config/config.properties`

3. **Compilación**:
   ```
   mvn clean package
   ```

4. **Ejecución**:
   ```
   java -jar target/alquiler-equipos-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Acceso al Sistema

- **Usuario Administrador por Defecto**:
  - Usuario: admin
  - Contraseña: Admin@123

## Roles de Usuario

- **Administrador**: Acceso completo a todas las funcionalidades, incluyendo gestión de usuarios
- **Trabajador**: Acceso a funcionalidades operativas (sin gestión de usuarios)

## Autores

- Juan Perea
- Kevin Beltran
- Carlos Rincon

## Licencia

Este proyecto es propiedad de sus autores y no puede ser utilizado, copiado, modificado o distribuido sin su autorización explícita.

## Contacto

Para más información o consultas, contactar a los autores:
- juan.perea@estudiantesunibague.edu.co
- kevin.beltran@estudiantesunibague.edu.co
- carlos.rincon@estudiantesunibague.edu.co
---

© 2025 - Sistema de Alquiler de Equipos Deportivos - Todos los derechos reservados