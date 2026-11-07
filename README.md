# Camerino Sports | Aplicación Android

Aplicación móvil de tienda deportiva desarrollada en Java para Android. Presenta un catálogo y permite gestionar un carrito y pedidos mediante una API independiente.

## Funcionalidades

- Registro e inicio de sesión, consulta y edición de perfil.
- Catálogo y detalle de productos, carrito, promociones y consejos.
- Flujo de pedido con datos de entrega, selección de método de pago y consulta de pedidos y su seguimiento.
- Pantallas administrativas para productos, pedidos, promociones y consejos.
- Selección de ubicación de entrega sobre mapa y componentes de ubicación de Google Play Services.
- Código de integración para tokenización con Culqi, incluida una variante para Yape. La disponibilidad y finalización de pagos dependen de la configuración y del servicio compatible; no se debe asumir que generar un token equivale a efectuar un cobro.

## Tecnologías y arquitectura

Java 11, Android SDK (mínimo API 28), Gradle Kotlin DSL, AndroidX, Material Components, Retrofit, Gson y Google Play Services Maps/Location.

- `app/src/main/java/.../`: actividades de las pantallas.
- `model/`, `adapter/`: entidades y adaptadores de interfaz.
- `viewmodel/`, `repository/`, `network/`: estado, acceso a datos y contratos HTTP.
- `res/`: vistas, imágenes y configuración de recursos.

La implementación histórica `database/DatabaseHelper.java` está comentada; la ruta vigente de datos utiliza Retrofit y el servidor. `ApiCliente` permite configurar URL alternativas y reintenta bajo condiciones concretas.

## Puesta en marcha

1. Abre el proyecto en Android Studio y sincroniza Gradle. Instala el SDK de Android requerido por `app/build.gradle.kts`.
2. Configura la URL principal y alternativas de la API en los recursos `api_base_url` y `api_base_urls`; comprueba que el emulador o dispositivo pueda alcanzar el servidor. Revisa también la configuración de Maps y los permisos de ubicación del manifiesto.
3. Inicia el backend compatible, selecciona un emulador o dispositivo con Android 9 (API 28) o posterior y ejecuta `app`.

El proyecto relacionado [CamerinoSportsServicios](https://github.com/einar2014idsis/CamerinoSportsServicios) implementa operaciones de productos, usuarios, pedidos, promociones y consejos. Comprueba los contratos de ambas versiones: el cliente declara rutas como `Pedidos/culqi` y `Health` que no aparecen entre los controladores del backend revisado. Las credenciales y claves de integraciones deben configurarse de forma segura.
