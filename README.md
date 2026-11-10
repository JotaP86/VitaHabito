# VitaHabito

Aplicación Android de gestión de hábitos saludables, desarrollada en Kotlin con Android Studio como proyecto del ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM).

Cada usuario crea su cuenta, inicia sesión y gestiona su propia lista de hábitos, clasificados por categorías. Los datos se guardan en local, en una base de datos SQLite del dispositivo.

## Funcionalidades

- **Registro de usuario:** nombre de usuario, contraseña y repetición de contraseña. Comprueba que ningún campo esté vacío, que las dos contraseñas coincidan y que el nombre de usuario no exista ya.
- **Inicio de sesión:** valida el usuario y la contraseña contra la base de datos local.
- **Menú principal:** acceso a "Añadir Hábito" y "Ver Mis Hábitos".
- **Crear hábito:** nombre, descripción y categoría, que se elige en un desplegable. El hábito queda asociado al usuario que ha iniciado sesión.
- **Listado de hábitos:** muestra los hábitos del usuario con su nombre, descripción y categoría, y se actualiza al volver a la pantalla.
- **Detalle de hábito:** muestra el nombre, la descripción y la categoría, con las opciones de editar y eliminar.
- **Editar hábito:** permite cambiar el nombre, la descripción y la categoría. El nombre es obligatorio.
- **Eliminar hábito:** borra el hábito y vuelve al listado.
- **Categorías por defecto:** al crear la base de datos se insertan Salud, Productividad y Personal.

## Tecnologías

| Elemento | Versión |
|---|---|
| Kotlin | 2.0.21 |
| Gradle (wrapper) | 8.13 |
| Android Gradle Plugin | 8.13.1 |
| compileSdk / targetSdk | 36 |
| minSdk | 24 (Android 7.0) |
| Java (source, target y jvmTarget) | 11 |
| AndroidX AppCompat | 1.6.1 |
| Material Components | 1.9.0 |

- **Interfaz:** layouts XML con `AppCompatActivity`, `ListView` con un `BaseAdapter` propio y `Spinner` para seleccionar la categoría. Tema `Theme.Material3.DayNight.NoActionBar`.
- **Persistencia:** SQLite mediante `SQLiteOpenHelper`, con las claves foráneas activadas.
- **Build:** Gradle con Kotlin DSL (`build.gradle.kts`) y catálogo de versiones (`gradle/libs.versions.toml`).

## Base de datos

La base de datos `vitahabito.db` (versión 5) se gestiona desde `DatabaseHelper` y tiene cuatro tablas:

| Tabla | Contenido |
|---|---|
| `users` | Usuarios registrados. El nombre de usuario es único. |
| `categories` | Categorías de hábitos. El nombre es único. |
| `habits` | Hábitos, con referencia al usuario (`user_id`) y a la categoría (`category_id`). |
| `habit_logs` | Registro diario de cada hábito: fecha, estado y notas, con un único registro por hábito y fecha. |

La tabla `habit_logs` y sus métodos de acceso (`insertOrUpdateLog` y `getLogsByHabit`) existen para el registro diario de hábitos, pero esta versión de la aplicación no incluye la pantalla que los utiliza.

El diagrama entidad-relación y el detalle de cada columna están en [docs/database.md](docs/database.md).

## Estructura del proyecto

```
VitaHabito/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/vitahabitoapp/
│       │   ├── LoginActivity.kt         Inicio de sesión (pantalla inicial)
│       │   ├── RegisterActivity.kt      Registro de usuarios
│       │   ├── MenuActivity.kt          Menú principal
│       │   ├── HabitFormActivity.kt     Alta de hábitos
│       │   ├── HabitListActivity.kt     Listado de hábitos del usuario
│       │   ├── HabitDetailActivity.kt   Detalle y borrado de un hábito
│       │   ├── HabitEditActivity.kt     Edición de un hábito
│       │   ├── HabitAdapter.kt          Adaptador del listado
│       │   ├── Habit.kt                 Modelo de hábito
│       │   ├── Category.kt              Modelo de categoría
│       │   ├── HabitLog.kt              Modelo de registro diario
│       │   └── DatabaseHelper.kt        Acceso a SQLite
│       └── res/
│           ├── layout/                  Pantallas en XML
│           ├── values/                  Colores, textos y tema
│           ├── drawable/, mipmap-*/     Iconos de la aplicación
│           └── xml/                     Reglas de copia de seguridad
├── docs/
│   └── database.md                      Modelo de datos
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── gradlew.bat
```

## Cómo ejecutarla

### Requisitos

- Android Studio compatible con Android Gradle Plugin 8.13
- Android SDK 36
- Emulador o dispositivo con Android 7.0 (API 24) o superior

### Con Android Studio

1. Clona el repositorio:
   ```bash
   git clone https://github.com/JotaP86/VitaHabito.git
   ```
2. En Android Studio, abre la carpeta del proyecto con **File > Open**.
3. Espera a que termine la sincronización de Gradle. Android Studio genera el fichero `local.properties` con la ruta del SDK.
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

## Uso

1. En la pantalla de inicio, pulsa **Crear Cuenta** y registra un usuario.
2. Inicia sesión con ese usuario.
3. En el menú, pulsa **Añadir Hábito**, rellena el nombre y la descripción, elige una categoría y guarda.
4. Pulsa **Ver Mis Hábitos** para consultar la lista.
5. Toca un hábito para ver su detalle y, desde ahí, editarlo o eliminarlo.

## Mejoras previstas

- Conectar el registro diario de hábitos a la interfaz.
- Guardar las contraseñas con hash y sal.
- Sustituir el borrado de tablas en `onUpgrade` por migraciones de base de datos que conserven los datos.
- Validar que el nombre no esté vacío al crear un hábito.
- Eliminar las dependencias de Jetpack Compose que no se usan.
- Añadir tests automatizados.

## Autor

Juan Pedro Torrecillas García - [GitHub](https://github.com/JotaP86)
