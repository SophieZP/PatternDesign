# Documentación detallada del proyecto glud-patterns

## Resumen general

El proyecto glud-patterns implementa los 23 patrones de diseño GoF en un contexto narrativo de Airbnb. La solución quedó organizada como un monorepo Maven con tres módulos principales:

- `shared`: contratos y estructuras JSON compartidas entre cliente y servidor.
- `server`: backend con un `PatternRouter` que ejecuta la lógica de cada patrón.
- `client`: interfaz web en HTML, CSS y JavaScript servida por el propio backend.

La decisión más importante del cambio fue reemplazar el cliente de consola por una interfaz web ligera. El backend sigue resolviendo los patrones y ahora además sirve una aplicación estática en `http://localhost:8080`, lo que permite interactuar con los casos de uso desde navegador sin depender de una UI Java adicional.

## Cambios realizados

### 1. Backend

El servidor fue convertido en un servidor HTTP simple usando `com.sun.net.httpserver.HttpServer`. Desde allí se exponen tres capacidades:

- `/` sirve la interfaz web.
- `/api/patterns` devuelve el catálogo completo de patrones en JSON.
- `/api/patterns/run` ejecuta el patrón solicitado y responde con un `JsonMessage`.

El router centraliza el despacho de los 23 casos usando el código del patrón (`01` a `23`) o su nombre textual. Cada handler encapsula la lógica de negocio del patrón y utiliza sus clases `model` correspondientes.

### 2. Cliente web

El cliente dejó de ser una aplicación de consola y pasó a ser una interfaz HTML con JavaScript. La interfaz permite:

- elegir un patrón desde un selector,
- enviar una entrada opcional,
- ejecutar el backend desde el navegador,
- visualizar la respuesta JSON,
- consultar el catálogo de patrones cargado desde un archivo JSON.

### 3. Módulo shared

Se definió un contrato compartido para la comunicación entre cliente y servidor:

- `PatternType`: enum con los 23 patrones y sus metadatos.
- `JsonMessage`: estructura de request/response.
- `JsonParser`: serialización y deserialización manual.
- `IPatternHandler`: contrato funcional para handlers del lado servidor.

### 4. Documentación y configuración

El `pom.xml` raíz quedó configurado como proyecto multi-módulo y el módulo `server` quedó preparado para compilar y ejecutar el backend. El proyecto fue validado con `javac --release 25` y el servidor responde correctamente a la API y al frontend web.

## Arquitectura final

### Flujo de ejecución

1. El usuario abre la interfaz web desde el navegador.
2. El frontend carga el catálogo de patrones desde un JSON estático.
3. El usuario elige un patrón y pulsa ejecutar.
4. JavaScript envía una petición `POST` a `/api/patterns/run`.
5. `PatternServer` recibe el JSON, lo transforma en `JsonMessage` y lo valida.
6. `PatternRouter` identifica el patrón y delega en el handler correspondiente.
7. El handler ejecuta la lógica GoF.
8. La respuesta vuelve como JSON y la interfaz la muestra en pantalla.

### Estructura de carpetas

```text
glud-patterns/
├── client/
│   └── src/main/resources/
│       ├── public/
│       │   ├── index.html
│       │   ├── styles.css
│       │   └── app.js
│       └── data/
│           ├── patterns.json
│           └── sample-request.json
├── server/
│   └── src/main/java/co/airbnb/server/
│       ├── PatternServer.java
│       ├── PatternRouter.java
│       └── patterns/
└── shared/
		└── src/main/java/co/airbnb/shared/
```

## Módulo shared

`shared` concentra el contrato de comunicación para evitar duplicidad entre frontend y backend.

### `PatternType`

Representa los 23 patrones con:

- código corto (`01` a `23`),
- nombre legible,
- descripción breve.

Esto permite que el frontend muestre un catálogo sin duplicar lógica de negocio.

### `JsonMessage`

Modelo simple para requests y responses. Sus campos principales son:

- `pattern`
- `input`
- `result`
- `success`
- `error`

También incluye constructores de ayuda para respuestas exitosas y fallidas.

### `JsonParser`

Se encarga de convertir los objetos compartidos a JSON y viceversa sin librerías externas. Además, construye el catálogo de patrones como JSON para que el navegador lo consuma directamente.

### `IPatternHandler`

Contrato funcional para estandarizar el tratamiento de un request en el lado del servidor.

## Módulo server

### `PatternServer`

Actúa como entrada principal de la aplicación. Tiene dos modos:

- si recibe argumentos por consola, ejecuta un patrón directo;
- si no recibe argumentos, levanta un servidor HTTP en el puerto `8080`.

Ese servidor expone la API y los archivos estáticos del frontend.

### `PatternRouter`

Es el punto central de despacho. Traduce el identificador del patrón a la clase handler correspondiente. Esta capa es la que mantiene desacoplado el endpoint del detalle de cada patrón.

### Implementación de los patrones

Cada patrón quedó agrupado en su propio paquete dentro de `server/src/main/java/co/airbnb/server/patterns/`. La lógica se implementó de forma autocontenida, sin depender de interfaz gráfica. Los modelos viven en subpaquetes `model` cuando el patrón lo requiere.

## Cliente web

### `index.html`

Presenta una interfaz visual simple y clara con:

- título del proyecto,
- estado del backend,
- formulario para ejecutar un patrón,
- panel de salida JSON,
- catálogo de patrones.

### `styles.css`

Define una identidad visual oscura con gradientes, paneles translúcidos y disposición responsive. La intención fue dar una interfaz más intencional que un layout genérico.

### `app.js`

Contiene tres responsabilidades:

- cargar el catálogo de patrones,
- consultar el estado del backend,
- ejecutar el patrón seleccionado contra la API.

## Estado de los 23 patrones

### Patrón 01 — Singleton

Implementa una única instancia de configuración global mediante `ConfigManager`.

### Patrón 02 — Factory Method

Encapsula la creación de notificaciones `Email`, `Push` y `SMS`.

### Patrón 03 — Abstract Factory

Separa la familia de componentes de UI para web y móvil.

### Patrón 04 — Builder

Construye `Listing` paso a paso mediante `ListingBuilder`.

### Patrón 05 — Prototype

Clona una plantilla base de listing y modifica solo los atributos necesarios.

### Patrón 06 — Adapter

Unifica pasarelas externas de pago como Stripe y PayPal detrás de `PaymentGateway`.

### Patrón 07 — Bridge

Separa los filtros de búsqueda de Airbnb de su implementación concreta.

### Patrón 08 — Composite

Modela habitaciones y grupos de propiedades bajo una jerarquía uniforme.

### Patrón 09 — Decorator

Agrega amenities de forma dinámica a un listing.

### Patrón 10 — Facade

Centraliza el proceso de reserva, pago y notificación.

### Patrón 11 — Flyweight

Reutiliza iconos de amenidades para ahorrar memoria.

### Patrón 12 — Proxy

Controla el acceso a perfiles sensibles según el rol.

### Patrón 13 — Chain of Responsibility

Aplica filtros encadenados para revisar reseñas.

### Patrón 14 — Command

Encapsula operaciones de reserva como objetos ejecutables.

### Patrón 15 — Interpreter

Interpreta búsquedas como `ciudad:Barcelona AND precio:<100`.

### Patrón 16 — Iterator

Permite recorrer colecciones sin exponer su estructura interna.

### Patrón 17 — Mediator

Centraliza el intercambio de mensajes entre usuarios de chat.

### Patrón 18 — Memento

Guarda y restaura estados previos de una reserva.

### Patrón 19 — Observer

Notifica a huéspedes cuando cambia el precio de un listing.

### Patrón 20 — State

Modifica el comportamiento de una reserva según su estado.

### Patrón 21 — Strategy

Intercambia algoritmos de cálculo de precio.

### Patrón 22 — Template Method

Define pasos comunes del proceso de check-in con variantes por tipo de reserva.

### Patrón 23 — Visitor

Permite calcular ingresos u otras operaciones sobre propiedades sin modificar sus clases.

## Cómo ejecutar el proyecto

### Backend

1. Compilar el módulo `server`.
2. Ejecutar `co.airbnb.server.PatternServer`.
3. Abrir `http://localhost:8080`.

### API principal

- `GET /api/patterns` para ver el catálogo.
- `POST /api/patterns/run` para ejecutar un patrón.

### Formato de request

```json
{
	"pattern": "01",
	"input": "demo"
}
```

### Formato de response

```json
{
	"pattern": "01",
	"input": "demo",
	"result": "Singleton: db.airbnb.local | pay-key-123 | 6",
	"success": true
}
```

## Observaciones finales

El proyecto ya no depende de una interfaz de consola. El backend queda preparado para ser consumido por la interfaz web y por cualquier otro frontend futuro. La documentación refleja ahora tanto el enunciado original de los patrones como la implementación real que quedó en el repositorio.

## Patrón 01 — Singleton
La aplicación necesita una única instancia de ConfigManager que mantenga la configuración global: host de base de datos, API key de pagos, número máximo de huéspedes por reserva. Si se crearan múltiples instancias, diferentes partes del sistema podrían trabajar con configuraciones inconsistentes.

## Patrón 02 — Factory Method
Airbnb envía notificaciones a sus usuarios por distintos canales: email, push notification y SMS. La lógica de creación de cada tipo de notificación debe estar encapsulada. Agregar un nuevo canal (por ejemplo, WhatsApp) no debe modificar el código existente.

## Patrón 03 — Abstract Factory
La aplicación web y la aplicación móvil de Airbnb necesitan componentes de UI diferentes: los botones y tarjetas tienen distinto estilo en web versus móvil, pero la lógica de construcción debe ser la misma. La Abstract Factory permite cambiar toda la familia de componentes con un solo cambio.

## Patrón 04 — Builder
Crear un Listing (anuncio de propiedad) implica muchos atributos opcionales: título, descripción, precio por noche, capacidad, lista de amenities, fotos, reglas de la casa, política de cancelación. El Builder permite construir el objeto paso a paso sin un constructor con 15 parámetros.

## Patrón 05 — Prototype
Un host tiene varias propiedades similares (por ejemplo, varios apartamentos en el mismo edificio). En lugar de configurar cada Listing desde cero, puede clonar una plantilla existente y solo modificar los detalles que cambian (número de habitación, piso, precio).

## Patrón 06 — Adapter
Airbnb integra múltiples pasarelas de pago. Stripe y PayPal tienen APIs completamente diferentes. El Adapter traduce la interfaz común PaymentGateway que usa Airbnb a la API específica de cada proveedor, sin que el resto del sistema sepa con cuál está hablando.

## Patrón 07 — Bridge
El motor de búsqueda de Airbnb tiene filtros (precio, ubicación, tipo de propiedad) y distintas implementaciones de cómo aplicarlos (búsqueda exacta, búsqueda aproximada, búsqueda por rango). El Bridge separa la abstracción del filtro de su implementación concreta.

## Patrón 08 — Composite
Airbnb necesita representar una jerarquía de propiedades donde una propiedad puede ser una habitación individual o un grupo de propiedades (como un apartamento con varias habitaciones). El Composite permite tratar de manera uniforme tanto a las propiedades individuales como a los grupos de propiedades.

## Patrón 09 — Decorator
Airbnb quiere agregar dinámicamente responsabilidades a un objeto, como agregar amenities (Wi-Fi, piscina, desayuno) a un listing existente sin modificar su estructura. El Decorator permite agregar comportamientos adicionales a un objeto de forma dinámica.

## Patrón 10 — Facade
Airbnb posee varios subsistemas complejos: servicio de pagos, servicio de reservas, servicio de notificaciones. El Facade proporciona una interfaz unificada y sencilla para que los clientes interactúen con estos subsistemas sin necesidad de conocer sus detalles internos.

## Patrón 11 — Flyweight
Airbnb necesita representar muchos objetos similares, como iconos de amenidades (Wi-Fi, cocina, aire acondicionado) que se reutilizan en múltiples listings. El Flyweight comparte partes comunes del estado entre múltiples objetos para ahorrar memoria.

## Patrón 12 — Proxy
Airbnb necesita controlar el acceso a objetos sensibles, como perfiles de usuario que contienen información personal. Un Proxy actúa como intermediario que controla el acceso al objeto real, añadiendo funcionalidades como lazy loading, control de acceso o registro.

## Patrón 13 — Chain of Responsibility
Airbnb procesa reseñas de huéspedes mediante una serie de validaciones (filtro de spam, filtro de groserías, filtro de longitud). En lugar de acoplar el remitente de la reseña a cada validador, se usa una cadena donde cada validador decide si procesa la solicitud o la pasa al siguiente.

## Patrón 14 — Command
Airbnb necesita encapsular solicitudes de reserva como objetos para poder parametrizar operaciones (crear, cancelar, modificar), encolarlas para procesamiento posterior o soportar operaciones deshacer/rehacer.

## Patrón 15 — Interpreter
Airbnb permite a los usuarios buscar propiedades usando expresiones como "ciudad:Barcelona AND precio:<100". El Interpreter define una gramática para estas expresiones de búsqueda y un intérprete que las evalúa.

## Patrón 16 — Iterator
Airbnb necesita recorrer colecciones de objetos (listings, usuarios, reservas) sin exponer su representación interna. El Iterator proporciona una forma de acceder secuencialmente a los elementos de un objeto agregado.

## Patrón 17 — Mediator
Airbnb tiene un sistema de chat donde usuarios (huéspedes y anfitriones) se comunican. En lugar de que los usuarios se refieran directamente entre sí, un Mediador centraliza la comunicación, reduciendo las dependencias entre ellos.

## Patrón 18 — Memento
Airbnb necesita permitir a los usuarios deshacer cambios en una reserva (por ejemplo, cambiar fechas) y volver a un estado anterior. El Memento captura el estado interno de una reserva sin violar el encapsulamiento, y un Caretaker se encarga de almacenarlo y restaurarlo.

## Patrón 19 — Observer
Airbnb debe notificar a los huéspedes interesados cuando el precio de un listing que están siguiendo baja o cambia. El Observer define una dependencia uno-a-muchos donde el sujeto (listing) notifica a sus observadores (huéspedes) de cualquier cambio de estado.

## Patrón 20 — State
Airbnb maneja reservas que pueden estar en distintos estados (pendiente, confirmada, cancelada). El comportamiento de una reserva (por ejemplo, permitir pago o cancelación) depende de su estado. El State permite que un objeto cambie su comportamiento cuando su estado interno cambia.

## Patrón 21 — Strategy
Airbnb necesita aplicar diferentes algoritmos de cálculo de precios según la temporada, duración de la estancia o promociones. El Strategy define una familia de algoritmos encapsulados y los hace intercambiables, permitiendo que el algoritmo varíe independientemente de los clientes que lo usan.

## Patrón 22 — Template Method
Airbnb tiene un proceso de check-in con pasos comunes (verificación de identidad, asignación de habitación, entrega de llaves) pero algunas etapas varían según el tipo de reserva (estándar vs lujo). El Template Method define el esqueleto del algoritmo y permite a las subclases redefinir ciertos pasos sin cambiar la estructura general.

## Patrón 23 — Visitor
Airbnb necesita realizar operaciones sobre una estructura de objetos de propiedades (apartamentos, villas) que dependen de la clase concreta de cada elemento (por ejemplo, cálculo de ingresos basado en tipo de propiedad). El Visitor permite definir una nueva operación sin cambiar las clases de los elementos sobre los que opera.
