# Changelog

Cambios de cada versión de VitaHabito.

## 1.1.0

### Añadido

- Pantalla de chequeo diario, accesible desde el botón "Chequeo diario" del menú principal. Lista los hábitos del usuario con una casilla para el día actual y guarda el estado de cada hábito en la tabla `habit_logs`.
- Hash de contraseñas con PBKDF2 (`PBKDF2WithHmacSHA1`, sal aleatoria de 16 bytes y 120 000 iteraciones) en la nueva clase `PasswordHasher`.
- Tests unitarios de `PasswordHasher` (7 tests de JUnit).
- Capturas de pantalla en `docs/screenshots/`.

### Cambiado

- Al crear un hábito, el nombre es obligatorio; si está vacío se muestra el aviso "El nombre no puede estar vacío" y no se guarda.
- El registro guarda el hash de la contraseña en lugar de la contraseña en texto plano, y el inicio de sesión verifica la contraseña contra ese hash.
- La base de datos pasa de la versión 5 a la 6. Como `onUpgrade` recrea todas las tablas, al actualizar desde la 1.0.0 se borran las cuentas y los hábitos existentes.
- `versionCode` 2 y `versionName` "1.1.0".

### Corregido

- Al eliminar un hábito se borran antes sus registros de `habit_logs`, en una única transacción, para no incumplir la clave foránea de esa tabla.
- En el chequeo diario, el estado de cada casilla se mantiene correcto cuando la lista reutiliza filas al desplazarse.

## 1.0.0 - 2026-11-10

Versión inicial.

- Registro de usuarios con validación de campos vacíos, contraseñas que no coinciden y nombres de usuario repetidos.
- Inicio de sesión.
- Menú principal.
- Hábitos con nombre, descripción y categoría: crear, listar, ver el detalle, editar y eliminar.
- Categorías por defecto: Salud, Productividad y Personal.
- Almacenamiento local en SQLite (versión 5 de la base de datos).
