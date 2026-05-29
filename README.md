# GLUD Patterns

Proyecto académico en Java que implementa los 23 patrones GoF con una arquitectura dividida en tres módulos: `shared`, `server` y `client`.

La aplicación expone un backend HTTP local, una interfaz web para explorar cada patrón, ejecución de ejemplos por patrón y diagramas PlantUML exactos derivados del código fuente.

## Contenido

- [Descripción general](#descripción-general)
- [Requisitos](#requisitos)
- [Descarga del proyecto](#descarga-del-proyecto)
- [Compilación](#compilación)
- [Ejecución](#ejecución)
- [Pruebas](#pruebas)
- [Estructura del proyecto](#estructura-del-proyecto)
- [API y rutas](#api-y-rutas)
- [Diagramas](#diagramas)
- [Detalles de implementación](#detalles-de-implementación)

## Descripción general

El objetivo del proyecto es mostrar, de forma visual y práctica, cómo se comportan los patrones de diseño clásicos dentro de un contexto tipo Airbnb.

Cada patrón tiene:

- una implementación en Java dentro de `server/src/main/java/co/airbnb/server/patterns`
- un caso de uso que devuelve una salida de demostración
- un diagrama PlantUML en `docs/diagrams`
- una versión renderizada en PNG en `docs/diagrams/rendered`
- una tarjeta y vista asociada en el frontend

## Requisitos

- Java 25
- Maven 3.9 o superior
- Navegador moderno
- PlantUML instalado solo si deseas regenerar manualmente las imágenes

## Descarga del proyecto

Si aún no tienes el repositorio localmente:

```bash
git clone <URL_DEL_REPOSITORIO>
cd glud-patterns
```

Si ya lo tienes descargado:

```bash
cd glud-patterns
git pull
```

## Compilación

El proyecto es multimódulo y usa Maven con el padre en la raíz.

### Compilar todo el proyecto

```bash
mvn clean package
```

### Compilar solo el backend

```bash
mvn -pl server -am clean package
```

### Nota sobre la versión de Java

El proyecto compila con `release 25`, así que la instalación de Java debe coincidir con esa versión o ser compatible.

## Ejecución

### Ejecutar el servidor

La forma recomendada es levantar el módulo `server`.

```bash
mvn -pl server exec:java
```

El servidor queda disponible en:

```text
http://localhost:8080
```

### Abrir la interfaz web

Al iniciar el servidor, abre en el navegador:

```text
http://localhost:8080/
```

Desde allí puedes:

- explorar el catálogo de patrones
- abrir el panel embebido de Airbnb
- ejecutar un patrón desde el formulario
- ver la respuesta JSON
- revisar el diagrama renderizado y el bloque PlantUML

### Ejecutar un patrón desde consola

El servidor también acepta un argumento de patrón para imprimir la demo directamente:

```bash
mvn -pl server exec:java -Dexec.args=19
```

## Pruebas

El módulo `server` incluye pruebas unitarias para el router y los patrones.

```bash
mvn test
```

Si quieres ejecutar solo el módulo servidor:

```bash
mvn -pl server test
```

## Estructura del proyecto

```text
glud-patterns/
├── client/
│   └── src/main/resources/public/
│       ├── index.html
│       ├── app.js
│       └── styles.css
├── server/
│   └── src/main/java/co/airbnb/server/
│       ├── PatternServer.java
│       ├── PatternRouter.java
│       └── patterns/
│           ├── abstractfactory/
│           ├── adapter/
│           ├── bridge/
│           ├── builder/
│           ├── chainofresponsibility/
│           ├── command/
│           ├── composite/
│           ├── decorator/
│           ├── facade/
│           ├── factorymethod/
│           ├── flyweight/
│           ├── interpreter/
│           ├── iterator/
│           ├── mediator/
│           ├── memento/
│           ├── observer/
│           ├── prototype/
│           ├── proxy/
│           ├── singleton/
│           ├── state/
│           ├── strategy/
│           ├── templatemethod/
│           └── visitor/
├── shared/
│   └── src/main/java/co/airbnb/shared/
├── docs/
│   ├── diagrams/
│   │   ├── 01.puml ... 23.puml
│   │   └── rendered/
│   │       ├── 01.png ... 23.png
│   └── documentation/
└── pom.xml
```

## API y rutas

### Backend

- `GET /api/patterns` devuelve el catálogo de patrones.
- `POST /api/patterns/run` ejecuta un patrón y devuelve una respuesta JSON.

### Recursos estáticos

- `GET /` sirve el frontend.
- `GET /styles.css` sirve estilos.
- `GET /app.js` sirve la lógica del cliente.
- `GET /data/patterns.json` sirve el catálogo para la interfaz.
- `GET /diagrams/{codigo}.puml` sirve el PlantUML en texto.
- `GET /diagrams/rendered/{codigo}.png` sirve la imagen renderizada del diagrama.

## Diagramas

Cada patrón tiene un diagrama PlantUML que refleja la estructura real del backend.

Ubicaciones:

- PlantUML fuente: `docs/diagrams/01.puml` a `docs/diagrams/23.puml`
- Imágenes renderizadas: `docs/diagrams/rendered/01.png` a `docs/diagrams/rendered/23.png`

Para regenerar manualmente las imágenes con PlantUML:

```bash
cd docs/diagrams
plantuml -tpng -o rendered *.puml
```

## Detalles de implementación

### Módulo `shared`

Contiene el contrato común entre cliente y servidor:

- `IPatternHandler`
- `JsonMessage`
- `JsonParser`
- `PatternType`

### Módulo `server`

Incluye:

- `PatternServer`: servidor HTTP embebido
- `PatternRouter`: enrutador que resuelve los 23 patrones
- `patterns/*`: implementación de cada patrón GoF
- tests del router y de los patrones

### Módulo `client`

Incluye una interfaz estática con:

- catálogo de patrones
- detalle del patrón seleccionado
- mini app Airbnb embebida
- ejecución del backend
- visualización de respuesta JSON
- visualización del diagrama PlantUML y su PNG renderizado

## Notas útiles

- Si Maven no está disponible en tu entorno, puedes compilar y ejecutar con `javac` y `java` manualmente.
- Si cambias un patrón, recuerda actualizar también su `.puml` y su PNG renderizado.
- La UI está pensada para mostrar un flujo de exploración: catálogo, app embebida, detalle del patrón y diagrama.

