# perfulandia-android
Repo del proyecto semestral de perfulandia para el ramo de apps moviles

# Perfulandia
## 1. Caso elegido y alcance
- **Caso:** Perfulandia SPA
  Definición: Perfulandia busca expandir su marca de perfumes hacia un modelo digital-first,
  donde los clientes puedan personalizar fragancias y recibir recomendaciones mediante
  algoritmos de preferencia. La empresa quiere integrar experiencias inmersivas de compra
  online con un sistema de gestión eficiente, manteniendo procesos sostenibles en empaques
  y distribución.
  Entidades:
  ● Perfume
  ● Categoría (Fragancia, Tamaño, Género)
  ● Cliente
  ● Pedido
  ● Reseña

- **Alcance EP3:** Diseño/UI, validaciones, navegación, estado, persistencia, recursos nativos.

## 2. Requisitos y ejecución
- **Stack:**
    - Jetpack Compose
    - Coil (carga de imágenes)
    - ViewModel
    - Navigation Compose
    - Retrofit y OkHttp (networking)
    - Kotlinx Coroutines
    - DataStore (persistencia local)
    - Accompanist Permissions (permisos)
- **Instalación:**
    1. Clonar el repositorio: `git clone https://github.com/Domingo-Chavez-duoc/perfulandia-android.git`
    2. Abrir el proyecto en Android Studio.
- **Ejecución:**
    - Ejecutar la aplicación desde Android Studio en un emulador o dispositivo físico.

## 3. Arquitectura y flujo
- **Estructura carpetas:**
  main/
  ├── java/
  │   └── com/
  │       └── domichav/
  │           └── perfulandia/                              //Paquete principal de la aplicación
  │               ├── data/                                 //Contiene todo lo relacionado a la gestión de datos
  │               │   ├── local/                            //Parte de la aplicación que maneja la base de datos local
  │               │   │   ├── dao/                          //Data Access Objects para interactuar con la base de datos local (vacío)
  │               │   │   ├── database/                     //Base de datos local Room (vacío)
  │               │   │   ├── entity/                       //Entidades de la base de datos local (vacío)
  │               │   │   ├── Account.kt                    //Manejo de datos del usuario autenticado
  │               │   │   └── SessionManager.kt             //Manejo de token, sesión y persistencia local usando Jetpack DataStore
  │               │   └── remote                            //Parte de la aplicación que maneja la comunicación con la API
  │               │       ├── api/                          //Definición de las rutas y endpoints de la API
  │               │       ├── dto/                          //Data Transfer Objects para mapear respuestas de la API
  │               │       ├── ApiService.kt                 //Define las peticiones que se le pueden hacer a la API
  │               │       ├── AuthInterceptor.kt            //Intercepta peticiones para agregar token de autenticación
  │               │       └── RetrofitClient.kt             //Dirección de URL correcta, manejo de tokens y conversion a JSON o .kt usando libreria Retrofit
  │               ├── model/                                //Parte del modelo MVVM (vacío)
  │               ├── repository/                           //Repositorios para manejar la lógica de datos
  │               ├── ui/                                   //Parte del modelo MVVM, contiene todo lo relacionado a la UI
  │               │   ├── components/                       //Componentes reutilizables de UI
  │               │   ├── navigation/                       //Navegación entre pantallas y urls
  │               │   ├── screens/                          //Pantallas de la aplicación
  │               │   ├── theme/                            //Temas de colores y tipografías
  │               │   └── utils/                            //Vacío
  │               ├── utils/                                //Con un archivo ValidationUtils.kt pero vacío
  │               └── viewmodel/                            //Parte del modelo MVVM, ViewModels para las pantallas
  │                   └── MainActivity.kt                   //Actividad principal que inicia la aplicación
  ├── res                                                   //Recursos de la aplicación, creamos un file_paths.xml para la ruta de imagenes
  ├── AndroidManifest.xml                                   //Archivo de configuración de la aplicación
  ├── test
  ├── .gitignore                                            
  └── build.gradle.kts                                      //Aqui se instalan las dependencias


- **Gestión de estado**: Se utiliza StateFlow, MutableStateFlow, y LiveData en los ViewModels para manejar el estado de la UI, y en las pantallas se usan collectAsState() para observar los cambios de estado y Data Classes como LoginUiState o ProfileUiState.
- **Navegación**: Se utiliza Jetpack Navigation Compose para la navegación entre pantallas, con NavHost en el MainActivity.kt para asociar pantallas a rutas ya definidas y usando metodos como NavigateTo().

## 4. Funcionalidades
- Formulario validado (registro/login):
  - Validacion en tiempo real (contraseña corta, email inválido)
  - Estado del botón según validez del formulario
  - Visibilidad de contraseña

- Navegación y backstack
  - Navegación entre pantallas
  - Manejo de backstack (botón atrás y up)

- Gestión de estado (carga/éxito/error)
  - Flujo de datos con StateFlow y LiveData unidireccional
  - isLoading (carga)
  - errorMessage (error)
  - Datos de usuario (éxito)

- **Persistencia local** (CRUD) y **almacenamiento de imagen de perfil**
  - DataStore para token y datos de usuario entre reinicios
  - Autenticacion automática si hay token válido con AuthInterceptor

- **Recursos nativos**: cámara/galería (permisos y fallback)
  - Acceso a cámara y galería con Accompanist Permissions
  - Manejo de permisos y fallback si no se otorgan

- **Animaciones** con propósito
  - Transiciones entre pantallas
  - Cambios de estado (carga/éxito/error)
  - Animaciones de botones y formularios

- **Consumo de API** (incluye `/me`)
  - Retrofit y OkHttp para comunicarse con la API
  - Intercepcion con AuthInterceptor y HttpLoggingInterceptor
  - Asincronía con Coroutines
  - Manejo de respuestas y errores

## 5. Endpoints
**Base URL:** `https://x8ki-letl-twmt.n7.xano.io/api:Rfm_61dW`

| Método | Ruta | Body | Respuesta |
| ------ | ------------ | --------------------------------- | ------------------------------|
| POST | /auth/signup | `{ email, password, name? }` | `201 { authToken, user: { id, ...} }` |
| POST | /auth/login | `{ email, password }` | `200 { authToken, user: { id, ...} }` |
| GET | /auth/me | - (requiere header Authorization) | `200 { id, email, name, avatarUrl }` |

## 6. User flows
- Registro de usuario
  1. Usuario abre la app y ve la pantalla de bienvenida.
  2. Usuario navega a la pantalla de registro.
  3. Usuario completa el formulario de registro con email, contraseña y nombre.
  4. Usuario presiona el botón "Registrarse".
  5. App valida los datos y muestra errores si es necesario.
  6. Si los datos son válidos, app envía solicitud a la API.
  7. Si el registro es exitoso, app guarda el token y datos del usuario en DataStore.
  8. Usuario es redirigido a la pantalla de perfil automaticamente.

- Inicio de sesión
  1. Usuario abre la app y ve la pantalla de bienvenida.
  2. Usuario navega a la pantalla de inicio de sesión.
  3. Usuario completa el formulario de inicio de sesión con email y contraseña.
  4. Usuario presiona el botón "Iniciar sesión".
  5. App valida los datos y muestra errores si es necesario.
  6. Si los datos son válidos, app envía solicitud a la API.
  7. Si el inicio de sesión es exitoso, app guarda el token y datos del usuario en DataStore.
  8. Usuario es redirigido a la pantalla de perfil automaticamente.

- Perfil de usuario
  1. Usuario inicia sesión o se registra exitosamente.
  2. Usuario es redirigido a la pantalla de perfil o accede manualmente.
  3. App muestra los datos del usuario obtenidos de DataStore o la API.
  4. Usuario puede actualizar su foto de perfil desde cámara o galería.
  5. App solicita permisos si es necesario y maneja fallback si no se otorgan.
  6. Usuario ve la foto actualizada en su perfil.