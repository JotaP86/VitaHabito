# VitaHabito

Aplicación Android de gestión de hábitos saludables, desarrollada en Kotlin con Android Studio como proyecto del ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM).

Cada usuario crea su cuenta, inicia sesión y gestiona su propia lista de hábitos. Los datos se guardan en local, en una base de datos SQLite del dispositivo.

## Funcionalidades

- **Registro de usuario:** nombre de usuario, contraseña y repetición de contraseña. Comprueba que no haya campos vacíos, que las contraseñas coincidan y que el usuario no exista ya.
- **Inicio de sesión:** valida usuario y contraseña contra la base de datos local.
- **Menú principal:** acceso a "Añadir hábito" y "Ver mis hábitos".
- **Crear hábito:** formulario con nombre y descripción, asociado al usuario que ha iniciado sesión.
- **Listado de hábitos:** muestra solo los hábitos del usuario actual y se actualiza al volver a la pantalla.
- **Detalle de hábito:** muestra nombre y descripción, con opciones para editar y eliminar.
- **Editar y eliminar hábitos:** actualización del nombre y la descripción, y borrado del hábito.

## Tecnologías

- **Lenguaje:** Kotlin 2.0.21
- **IDE:** Android Studio
- **Build:** Gradle 8.13 (wrapper incluido), Android Gradle Plugin 8.13.1, Kotlin DSL (`build.gradle.kts`) y catálogo de versiones (`gradle/libs.versions.toml`)
- **SDK:** minSdk 24 (Android 7.0), targetSdk 36 y compileSdk 36
- **Java:** 11
- **Interfaz:** layouts XML con `AppCompatActivity`, `ListView` y un `BaseAdapter` propio
- **Estilos:** Material Components (tema `Theme.MaterialComponents.DayNight.NoActionBar`)
- **Persistencia:** SQLite mediante `SQLiteOpenHelper`, con claves foráneas activadas
- **Tests:** JUnit 4 y AndroidX Test / Espresso (plantillas de ejemplo)

## Base de datos

Fichero `vitahabito.db`, gestionado por `DatabaseHelper`:

| Tabla    | Campos                                                         |
|----------|----------------------------------------------------------------|
| `users`  | `id` (PK, autoincremental), `username` (único), `password`     |
| `habits` | `id` (PK, autoincremental), `name`, `description`, `user_id` (FK a `users.id`, `ON DELETE CASCADE`) |

## Estructura del proyecto

```
VitaHabito/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/vitahabitoapp/
│       │   │   ├── LoginActivity.kt         Pantalla inicial: inicio de sesión
│       │   │   ├── RegisterActivity.kt      Registro de usuarios
│       │   │   ├── MenuActivity.kt          Menú principal
│       │   │   ├── HabitFormActivity.kt     Alta de hábitos
│       │   │   ├── HabitListActivity.kt     Listado de hábitos del usuario
│       │   │   ├── HabitDetailActivity.kt   Detalle y borrado de un hábito
│       │   │   ├── HabitEditActivity.kt     Edición de un hábito
│       │   │   ├── HabitAdapter.kt          Adaptador del ListView
│       │   │   ├── Habit.kt                 Modelo de datos
│       │   │   └── DatabaseHelper.kt        Acceso a SQLite (usuarios y hábitos)
│       │   └── res/
│       │       ├── layout/                  Pantallas en XML
│       │       ├── values/                  Colores, textos y tema
│       │       ├── drawable/ y mipmap-*/    Iconos de la aplicación
│       │       └── xml/                     Reglas de copia de seguridad
│       ├── test/                            Tests unitarios
│       └── androidTest/                     Tests instrumentados
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── docs/
│   └── screenshots/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── gradlew.bat
```

## Cómo ejecutarla

### Requisitos

- Android Studio reciente, compatible con Android Gradle Plugin 8.13
- Android SDK 36 instalado
- Un emulador o dispositivo con Android 7.0 (API 24) o superior

### Con Android Studio

1. Clona el repositorio:
   ```bash
   git clone https://github.com/JotaP86/VitaHabito.git
   ```
2. En Android Studio, elige **File > Open** y selecciona la carpeta `VitaHabito`.
3. Espera a que termine la sincronización de Gradle. Android Studio crea automáticamente el fichero `local.properties` con la ruta de tu SDK.
4. Selecciona un emulador o dispositivo y pulsa **Run**.

### Desde la línea de comandos

En Linux o macOS:

```bash
./gradlew assembleDebug
```

En Windows:

```bash
gradlew.bat assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/`.

### Uso

1. En la pantalla de inicio, pulsa **Crear Cuenta** y registra un usuario.
2. Inicia sesión con ese usuario.
3. Desde el menú, añade hábitos o consulta tu lista.
4. Toca un hábito de la lista para ver su detalle, editarlo o eliminarlo.

## Capturas de pantalla

Las capturas se añadirán en la carpeta [`docs/screenshots/`](docs/screenshots/).

<!--
Ejemplo para cuando estén disponibles:
![Inicio de sesión](docs/screenshots/login.png)
![Menú principal](docs/screenshots/menu.png)
![Lista de hábitos](docs/screenshots/lista.png)
-->

## Mejoras previstas

- Guardar las contraseñas con hash y sal en lugar de en texto plano.
- Añadir migraciones de base de datos para conservar los datos al cambiar de versión.
- Eliminar las dependencias de Jetpack Compose que no se usan, ya que la interfaz está hecha con layouts XML.

## Estado de las pruebas

La aplicación no se ha probado en un emulador desde el entorno en el que se preparó este repositorio.

## Autor

Juan Pedro Torrecillas García - [GitHub](https://github.com/JotaP86)
