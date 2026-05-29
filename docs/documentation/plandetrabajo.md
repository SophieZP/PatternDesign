Plan trabajo glud patterns · MD
# Plan de trabajo — glud-patterns
## Proyecto final · Modelos de Programación · Universidad Distrital
### Aplicación de los 23 patrones de diseño GoF en contexto Airbnb
 
---
 
## Tabla de contenido
 
1. [Descripción general del proyecto](#1-descripción-general-del-proyecto)
2. [Tecnologías y herramientas](#2-tecnologías-y-herramientas)
3. [Arquitectura de la aplicación](#3-arquitectura-de-la-aplicación)
4. [Estructura del repositorio](#4-estructura-del-repositorio)
5. [Módulo shared — protocolo JSON](#5-módulo-shared--protocolo-json)
6. [Módulo server — backend](#6-módulo-server--backend)
7. [Módulo client — frontend](#7-módulo-client--frontend)
8. [Los 23 patrones — situación, clases y responsabilidades](#8-los-23-patrones--situación-clases-y-responsabilidades)
9. [Convenciones de código](#9-convenciones-de-código)
10. [Plan de implementación por fases](#10-plan-de-implementación-por-fases)
11. [Estructura del informe (rúbrica)](#11-estructura-del-informe-rúbrica)
12. [Checklist de entrega](#12-checklist-de-entrega)
---
 
## 1. Descripción general del proyecto
 
### Objetivo
 
Desarrollar una aplicación Java de escritorio que demuestre la aplicación práctica de los 23 patrones de diseño del catálogo GoF (Gang of Four), usando como contexto narrativo la plataforma Airbnb. La aplicación sigue un modelo cliente-servidor con protocolo JSON sobre sockets TCP, organizada como un monorepo Maven multi-módulo.
 
### Características clave
 
- Una sola aplicación ejecutable con un servidor central y un cliente con interfaz de consola.
- El cliente presenta una barra lateral con los 23 patrones organizados por categoría. Cada patrón tiene su propia vista con dos zonas: zona interactiva (demuestra el patrón en ejecución) y zona de diagrama (muestra el diagrama de clases UML en ASCII art del patrón).
- El servidor expone un único punto de entrada que recibe mensajes JSON, identifica el patrón solicitado mediante un router y delega al handler correspondiente.
- Cada patrón vive en su propio paquete dentro del servidor, con sus clases de modelo aisladas. El cliente tiene una vista dedicada por patrón.
- No se usan frameworks externos. Todo es Java SE puro. La única dependencia de build es Maven.
- El protocolo JSON se implementa manualmente con operaciones de cadenas, sin Gson ni Jackson.
### Contexto narrativo — Airbnb
 
Airbnb es una plataforma de alquiler de alojamientos que conecta anfitriones (hosts) con huéspedes (guests). Tiene listados de propiedades, sistema de reservas, pagos, reseñas, notificaciones, búsqueda con filtros, chat entre usuarios, y un panel de administración. Este contexto es rico en situaciones que justifican naturalmente cada uno de los 23 patrones.
 
---
 
## 2. Tecnologías y herramientas
 
| Elemento | Detalle |
|---|---|
| Lenguaje | Java SE 17 (o 11 como mínimo) |
| Build tool | Apache Maven 3.8+, configuración multi-módulo |
| IDE recomendado | IntelliJ IDEA o VS Code con Extension Pack for Java |
| Control de versiones | Git + GitHub (repositorio monolítico / monorepo) |
| Comunicación | `java.net.ServerSocket` / `java.net.Socket` sobre TCP |
| Serialización | JSON manual con `String`, `StringBuilder` y parsing propio |
| Diagramas UML | ASCII art en consola + imagen exportada para el informe |
| Informe | LaTeX (se genera desde `docs/`) |
| Java SE APIs usadas | `java.net`, `java.io`, `java.util`, `java.util.concurrent` |
 
### Dependencias Maven
 
El proyecto no tiene dependencias externas de runtime. El `pom.xml` padre solo declara:
 
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```
 
Los tres módulos son `shared`, `server` y `client`. `server` y `client` declaran dependencia sobre `shared`.
 
---
 
## 3. Arquitectura de la aplicación
 
### Visión general
 
```
┌─────────────────────────────┐         TCP :8080          ┌─────────────────────────────────────┐
│         CLIENT              │ ─── JsonMessage (request) ─►│             SERVER                  │
│                             │ ◄── JsonMessage (response)─ │                                     │
│  ConsoleUI                  │                             │  PatternServer                      │
│    └── barra lateral        │                             │    └── PatternRouter                │
│         23 patrones         │                             │         └── IPatternHandler         │
│    └── PatternView (activa) │                             │              ├── SingletonHandler   │
│         ├── zona interactiva│                             │              ├── FactoryHandler      │
│         └── zona diagrama   │                             │              └── · · · 23 handlers  │
└─────────────────────────────┘                             └─────────────────────────────────────┘
              │                                                             │
              └──────────────────────┬──────────────────────────────────────┘
                                     │
                              ┌──────▼──────┐
                              │   SHARED    │
                              │ JsonMessage │
                              │ JsonParser  │
                              │ PatternType │
                              │IPatternHndlr│
                              └─────────────┘
```
 
### Flujo de una petición completa
 
1. El usuario está en la vista de un patrón y selecciona una acción del menú.
2. `PatternView` construye un `JsonMessage` con los campos `pattern`, `action` y `payload`.
3. `PatternClient` serializa el mensaje a String JSON y lo envía por el socket TCP al puerto 8080.
4. `PatternServer` recibe la conexión, lee el String del `BufferedReader`.
5. `PatternRouter` deserializa el JSON, lee el campo `pattern` y busca el handler registrado.
6. El `XxxHandler` correspondiente ejecuta la lógica de negocio usando sus clases `model/`.
7. El handler construye un `JsonMessage` de respuesta con `status` y `payload`.
8. `PatternServer` serializa la respuesta y la escribe al socket.
9. `PatternClient` recibe el String, lo deserializa a `JsonMessage`.
10. `ConsoleUI` / `PatternView` imprime la respuesta formateada en la consola.
### Protocolo de mensajes JSON
 
Cada mensaje, tanto de request como de response, sigue esta estructura:
 
**Request:**
```json
{
  "pattern": "SINGLETON",
  "action": "GET_INSTANCE",
  "payload": ""
}
```
 
**Response exitosa:**
```json
{
  "status": "OK",
  "pattern": "SINGLETON",
  "action": "GET_INSTANCE",
  "payload": "{ \"instance_id\": \"cfg@1a2b3c\", \"db_host\": \"airbnb-db-prod\" }"
}
```
 
**Response de error:**
```json
{
  "status": "ERROR",
  "pattern": "SINGLETON",
  "action": "GET_INSTANCE",
  "payload": "Handler no encontrado para el patrón: SINGLETON"
}
```
 
---
 
## 4. Estructura del repositorio
 
```
glud-patterns/
│
├── pom.xml                                        ← parent POM, módulos: shared, server, client
├── README.md
│
├── docs/
│   ├── report.tex                                 ← informe LaTeX principal
│   ├── diagrams/                                  ← imágenes UML exportadas (PNG/PDF)
│   └── bibliography.bib
│
├── shared/
│   ├── pom.xml
│   └── src/main/java/co/airbnb/shared/
│       ├── JsonMessage.java
│       ├── JsonParser.java
│       ├── PatternType.java
│       └── IPatternHandler.java
│
├── server/
│   ├── pom.xml
│   └── src/main/java/co/airbnb/server/
│       ├── PatternServer.java
│       ├── PatternRouter.java
│       └── patterns/
│           ├── singleton/
│           │   ├── SingletonHandler.java
│           │   └── model/
│           │       └── ConfigManager.java
│           ├── factorymethod/
│           │   ├── FactoryMethodHandler.java
│           │   └── model/
│           │       ├── Notification.java
│           │       ├── EmailNotification.java
│           │       ├── PushNotification.java
│           │       ├── SmsNotification.java
│           │       └── NotificationFactory.java
│           ├── abstractfactory/
│           │   ├── AbstractFactoryHandler.java
│           │   └── model/
│           │       ├── UIComponent.java
│           │       ├── Button.java
│           │       ├── Card.java
│           │       ├── WebUIFactory.java
│           │       ├── MobileUIFactory.java
│           │       └── UIFactory.java
│           ├── builder/
│           │   ├── BuilderHandler.java
│           │   └── model/
│           │       ├── Listing.java
│           │       └── ListingBuilder.java
│           ├── prototype/
│           │   ├── PrototypeHandler.java
│           │   └── model/
│           │       ├── ListingTemplate.java
│           │       └── Cloneable.java (interfaz propia)
│           ├── adapter/
│           │   ├── AdapterHandler.java
│           │   └── model/
│           │       ├── PaymentGateway.java
│           │       ├── StripeAPI.java
│           │       ├── PayPalAPI.java
│           │       └── PaymentAdapter.java
│           ├── bridge/
│           │   ├── BridgeHandler.java
│           │   └── model/
│           │       ├── SearchFilter.java
│           │       ├── FilterImplementor.java
│           │       ├── PriceFilter.java
│           │       └── LocationFilter.java
│           ├── composite/
│           │   ├── CompositeHandler.java
│           │   └── model/
│           │       ├── PropertyComponent.java
│           │       ├── Room.java
│           │       └── PropertyGroup.java
│           ├── decorator/
│           │   ├── DecoratorHandler.java
│           │   └── model/
│           │       ├── Listing.java
│           │       ├── ListingDecorator.java
│           │       ├── WifiDecorator.java
│           │       └── PoolDecorator.java
│           ├── facade/
│           │   ├── FacadeHandler.java
│           │   └── model/
│           │       ├── BookingFacade.java
│           │       ├── PaymentService.java
│           │       ├── ReservationService.java
│           │       └── NotificationService.java
│           ├── flyweight/
│           │   ├── FlyweightHandler.java
│           │   └── model/
│           │       ├── AmenityIcon.java
│           │       └── AmenityIconFactory.java
│           ├── proxy/
│           │   ├── ProxyHandler.java
│           │   └── model/
│           │       ├── UserProfile.java
│           │       ├── RealUserProfile.java
│           │       └── UserProfileProxy.java
│           ├── chainofresponsibility/
│           │   ├── ChainHandler.java
│           │   └── model/
│           │       ├── ReviewHandler.java
│           │       ├── SpamFilter.java
│           │       ├── ProfanityFilter.java
│           │       └── LengthFilter.java
│           ├── command/
│           │   ├── CommandHandler.java
│           │   └── model/
│           │       ├── BookingCommand.java
│           │       ├── CreateBookingCommand.java
│           │       ├── CancelBookingCommand.java
│           │       └── BookingInvoker.java
│           ├── interpreter/
│           │   ├── InterpreterHandler.java
│           │   └── model/
│           │       ├── SearchExpression.java
│           │       ├── CityExpression.java
│           │       ├── PriceExpression.java
│           │       └── AndExpression.java
│           ├── iterator/
│           │   ├── IteratorHandler.java
│           │   └── model/
│           │       ├── ListingCollection.java
│           │       ├── ListingIterator.java
│           │       └── Listing.java
│           ├── mediator/
│           │   ├── MediatorHandler.java
│           │   └── model/
│           │       ├── ChatMediator.java
│           │       ├── AirbnbChatMediator.java
│           │       ├── User.java
│           │       ├── Host.java
│           │       └── Guest.java
│           ├── memento/
│           │   ├── MementoHandler.java
│           │   └── model/
│           │       ├── Booking.java
│           │       ├── BookingMemento.java
│           │       └── BookingCaretaker.java
│           ├── observer/
│           │   ├── ObserverHandler.java
│           │   └── model/
│           │       ├── PriceObservable.java
│           │       ├── ListingPrice.java
│           │       ├── PriceObserver.java
│           │       └── GuestAlert.java
│           ├── state/
│           │   ├── StateHandler.java
│           │   └── model/
│           │       ├── BookingState.java
│           │       ├── PendingState.java
│           │       ├── ConfirmedState.java
│           │       ├── CancelledState.java
│           │       └── BookingContext.java
│           ├── strategy/
│           │   ├── StrategyHandler.java
│           │   └── model/
│           │       ├── PricingStrategy.java
│           │       ├── NightlyPricing.java
│           │       ├── WeeklyPricing.java
│           │       ├── SeasonalPricing.java
│           │       └── PricingContext.java
│           ├── templatemethod/
│           │   ├── TemplateMethodHandler.java
│           │   └── model/
│           │       ├── CheckInProcess.java
│           │       ├── StandardCheckIn.java
│           │       └── LuxuryCheckIn.java
│           └── visitor/
│               ├── VisitorHandler.java
│               └── model/
│                   ├── PropertyElement.java
│                   ├── Apartment.java
│                   ├── Villa.java
│                   ├── PropertyVisitor.java
│                   └── RevenueCalculator.java
│
└── client/
    ├── pom.xml
    └── src/main/java/co/airbnb/client/
        ├── Main.java
        ├── PatternClient.java
        ├── ConsoleUI.java
        └── views/
            ├── PatternView.java                   ← interfaz base de todas las vistas
            ├── singleton/SingletonView.java
            ├── factorymethod/FactoryMethodView.java
            ├── abstractfactory/AbstractFactoryView.java
            ├── builder/BuilderView.java
            ├── prototype/PrototypeView.java
            ├── adapter/AdapterView.java
            ├── bridge/BridgeView.java
            ├── composite/CompositeView.java
            ├── decorator/DecoratorView.java
            ├── facade/FacadeView.java
            ├── flyweight/FlyweightView.java
            ├── proxy/ProxyView.java
            ├── chainofresponsibility/ChainView.java
            ├── command/CommandView.java
            ├── interpreter/InterpreterView.java
            ├── iterator/IteratorView.java
            ├── mediator/MediatorView.java
            ├── memento/MementoView.java
            ├── observer/ObserverView.java
            ├── state/StateView.java
            ├── strategy/StrategyView.java
            ├── templatemethod/TemplateMethodView.java
            └── visitor/VisitorView.java
```
 
---
 
## 5. Módulo shared — protocolo JSON
 
### 5.1 `PatternType.java`
 
Enum con los 23 patrones. Se usa tanto en el router del servidor como en el cliente para identificar a qué handler dirigir cada mensaje.
 
```java
package co.airbnb.shared;
 
public enum PatternType {
    SINGLETON,
    FACTORY_METHOD,
    ABSTRACT_FACTORY,
    BUILDER,
    PROTOTYPE,
    ADAPTER,
    BRIDGE,
    COMPOSITE,
    DECORATOR,
    FACADE,
    FLYWEIGHT,
    PROXY,
    CHAIN_OF_RESPONSIBILITY,
    COMMAND,
    INTERPRETER,
    ITERATOR,
    MEDIATOR,
    MEMENTO,
    OBSERVER,
    STATE,
    STRATEGY,
    TEMPLATE_METHOD,
    VISITOR
}
```
 
### 5.2 `JsonMessage.java`
 
Representa el sobre que viaja por el socket en ambas direcciones. Se serializa y deserializa manualmente.
 
```java
package co.airbnb.shared;
 
public class JsonMessage {
 
    private String pattern;   // valor del enum PatternType como String
    private String action;    // acción específica del patrón, ej: "GET_INSTANCE"
    private String payload;   // datos adicionales serializados como String JSON
    private String status;    // "OK" o "ERROR" (solo en responses)
 
    public JsonMessage() {}
 
    public JsonMessage(String pattern, String action, String payload) {
        this.pattern = pattern;
        this.action  = action;
        this.payload = payload;
    }
 
    // Serializa el objeto a String JSON
    public String toJson() {
        return "{"
            + "\"pattern\":\"" + escape(pattern) + "\","
            + "\"action\":\""  + escape(action)  + "\","
            + "\"status\":\""  + escape(status)  + "\","
            + "\"payload\":"   + (payload != null ? payload : "\"\"")
            + "}";
    }
 
    // Escapa comillas dobles dentro de valores de cadena
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
 
    // Getters y setters
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public String getAction()  { return action; }
    public void setAction(String action) { this.action = action; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getStatus()  { return status; }
    public void setStatus(String status) { this.status = status; }
 
    @Override
    public String toString() { return toJson(); }
}
```
 
### 5.3 `JsonParser.java`
 
Parser JSON manual. Extrae valores de un JSON plano (un nivel de profundidad). Para payloads anidados, el valor del campo `payload` ya viene como String y se procesa por separado si hace falta.
 
```java
package co.airbnb.shared;
 
public class JsonParser {
 
    // Extrae el valor de un campo String del JSON: "campo":"valor"
    public static String getString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return "";
        return json.substring(start, end).replace("\\\"", "\"").replace("\\\\", "\\");
    }
 
    // Extrae el valor de un campo que puede ser objeto o array: "campo":{...} o "campo":[...]
    public static String getObject(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "{}";
        start += search.length();
        char open  = json.charAt(start);
        char close = open == '{' ? '}' : ']';
        int depth = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == open)  depth++;
            if (c == close) depth--;
            sb.append(c);
            if (depth == 0) break;
        }
        return sb.toString();
    }
 
    // Construye el JsonMessage desde un String JSON recibido por el socket
    public static JsonMessage parse(String json) {
        JsonMessage msg = new JsonMessage();
        msg.setPattern(getString(json, "pattern"));
        msg.setAction(getString(json, "action"));
        msg.setStatus(getString(json, "status"));
        // payload puede ser objeto JSON o string
        String payloadStr = getString(json, "payload");
        if (payloadStr.isEmpty()) {
            String payloadObj = getObject(json, "payload");
            msg.setPayload(payloadObj.equals("{}") ? "\"\"" : payloadObj);
        } else {
            msg.setPayload("\"" + payloadStr.replace("\"", "\\\"") + "\"");
        }
        return msg;
    }
}
```
 
### 5.4 `IPatternHandler.java`
 
Interfaz que deben implementar todos los handlers del servidor. Garantiza que el router pueda tratarlos de forma polimórfica.
 
```java
package co.airbnb.shared;
 
public interface IPatternHandler {
    /**
     * Recibe un JsonMessage con la acción solicitada y retorna
     * un JsonMessage con el resultado de ejecutar el patrón.
     */
    JsonMessage handle(JsonMessage request);
}
```
 
---
 
## 6. Módulo server — backend
 
### 6.1 `PatternServer.java`
 
Abre el `ServerSocket` en el puerto 8080 y atiende conexiones en un loop. Cada conexión se maneja en el mismo hilo (versión simple; se puede pasar a `ExecutorService` si se desea concurrencia).
 
```java
package co.airbnb.server;
 
import co.airbnb.shared.IPatternHandler;
import co.airbnb.shared.JsonMessage;
import co.airbnb.shared.JsonParser;
 
import java.io.*;
import java.net.*;
 
public class PatternServer {
 
    private final int port;
    private final PatternRouter router;
 
    public PatternServer(int port) {
        this.port   = port;
        this.router = new PatternRouter();
    }
 
    public void start() {
        System.out.println("[SERVER] Escuchando en el puerto " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Error: " + e.getMessage());
        }
    }
 
    private void handleConnection(Socket socket) {
        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String raw = in.readLine();
            if (raw == null || raw.isBlank()) return;
 
            JsonMessage request  = JsonParser.parse(raw);
            JsonMessage response = router.route(request);
            out.println(response.toJson());
 
        } catch (IOException e) {
            System.err.println("[SERVER] Error al manejar conexión: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
```
 
### 6.2 `PatternRouter.java`
 
Registra todos los handlers y delega según el campo `pattern` del mensaje.
 
```java
package co.airbnb.server;
 
import co.airbnb.shared.*;
import co.airbnb.server.patterns.singleton.SingletonHandler;
import co.airbnb.server.patterns.factorymethod.FactoryMethodHandler;
// ... importar todos los handlers
 
import java.util.HashMap;
import java.util.Map;
 
public class PatternRouter {
 
    private final Map<String, IPatternHandler> handlers = new HashMap<>();
 
    public PatternRouter() {
        handlers.put(PatternType.SINGLETON.name(),               new SingletonHandler());
        handlers.put(PatternType.FACTORY_METHOD.name(),          new FactoryMethodHandler());
        handlers.put(PatternType.ABSTRACT_FACTORY.name(),        new AbstractFactoryHandler());
        handlers.put(PatternType.BUILDER.name(),                 new BuilderHandler());
        handlers.put(PatternType.PROTOTYPE.name(),               new PrototypeHandler());
        handlers.put(PatternType.ADAPTER.name(),                 new AdapterHandler());
        handlers.put(PatternType.BRIDGE.name(),                  new BridgeHandler());
        handlers.put(PatternType.COMPOSITE.name(),               new CompositeHandler());
        handlers.put(PatternType.DECORATOR.name(),               new DecoratorHandler());
        handlers.put(PatternType.FACADE.name(),                  new FacadeHandler());
        handlers.put(PatternType.FLYWEIGHT.name(),               new FlyweightHandler());
        handlers.put(PatternType.PROXY.name(),                   new ProxyHandler());
        handlers.put(PatternType.CHAIN_OF_RESPONSIBILITY.name(), new ChainHandler());
        handlers.put(PatternType.COMMAND.name(),                 new CommandHandler());
        handlers.put(PatternType.INTERPRETER.name(),             new InterpreterHandler());
        handlers.put(PatternType.ITERATOR.name(),                new IteratorHandler());
        handlers.put(PatternType.MEDIATOR.name(),                new MediatorHandler());
        handlers.put(PatternType.MEMENTO.name(),                 new MementoHandler());
        handlers.put(PatternType.OBSERVER.name(),                new ObserverHandler());
        handlers.put(PatternType.STATE.name(),                   new StateHandler());
        handlers.put(PatternType.STRATEGY.name(),                new StrategyHandler());
        handlers.put(PatternType.TEMPLATE_METHOD.name(),         new TemplateMethodHandler());
        handlers.put(PatternType.VISITOR.name(),                 new VisitorHandler());
    }
 
    public JsonMessage route(JsonMessage request) {
        String key     = request.getPattern();
        IPatternHandler handler = handlers.get(key);
 
        if (handler == null) {
            JsonMessage error = new JsonMessage();
            error.setPattern(key);
            error.setAction(request.getAction());
            error.setStatus("ERROR");
            error.setPayload("\"Handler no registrado para el patrón: " + key + "\"");
            return error;
        }
 
        return handler.handle(request);
    }
}
```
 
### 6.3 Estructura de cada handler
 
Todos los handlers siguen el mismo esquema. El switch sobre `action` determina qué operación ejecutar. Cada acción construye un payload JSON de respuesta manualmente.
 
```java
package co.airbnb.server.patterns.singleton;
 
import co.airbnb.shared.IPatternHandler;
import co.airbnb.shared.JsonMessage;
 
public class SingletonHandler implements IPatternHandler {
 
    @Override
    public JsonMessage handle(JsonMessage request) {
        JsonMessage response = new JsonMessage();
        response.setPattern(request.getPattern());
        response.setAction(request.getAction());
 
        switch (request.getAction()) {
            case "GET_INSTANCE" -> {
                ConfigManager cfg = ConfigManager.getInstance();
                response.setStatus("OK");
                response.setPayload(
                    "{\"instance_id\":\"" + Integer.toHexString(cfg.hashCode()) + "\","
                  + "\"db_host\":\""       + cfg.getDbHost()  + "\","
                  + "\"api_key\":\""       + cfg.getApiKey()  + "\"}"
                );
            }
            case "SET_DB_HOST" -> {
                String newHost = request.getPayload().replace("\"", "").trim();
                ConfigManager.getInstance().setDbHost(newHost);
                response.setStatus("OK");
                response.setPayload("\"DB host actualizado a: " + newHost + "\"");
            }
            case "VERIFY_UNIQUE" -> {
                ConfigManager a = ConfigManager.getInstance();
                ConfigManager b = ConfigManager.getInstance();
                boolean same = (a == b);
                response.setStatus("OK");
                response.setPayload(
                    "{\"same_instance\":" + same + ","
                  + "\"hash_a\":\""       + Integer.toHexString(a.hashCode()) + "\","
                  + "\"hash_b\":\""       + Integer.toHexString(b.hashCode()) + "\"}"
                );
            }
            default -> {
                response.setStatus("ERROR");
                response.setPayload("\"Acción desconocida: " + request.getAction() + "\"");
            }
        }
 
        return response;
    }
}
```
 
---
 
## 7. Módulo client — frontend
 
### 7.1 `Main.java`
 
Punto de entrada. Lanza el servidor en un hilo separado, espera un instante para que arranque, luego lanza el cliente en el hilo principal.
 
```java
package co.airbnb.client;
 
public class Main {
 
    public static void main(String[] args) throws InterruptedException {
        // Lanzar servidor en hilo daemon para que muera al cerrar el cliente
        Thread serverThread = new Thread(() -> {
            // PatternServer se importa del módulo server
            new co.airbnb.server.PatternServer(8080).start();
        });
        serverThread.setDaemon(true);
        serverThread.start();
 
        // Esperar a que el servidor esté listo
        Thread.sleep(500);
 
        // Lanzar la interfaz de consola
        PatternClient client = new PatternClient("localhost", 8080);
        new ConsoleUI(client).start();
    }
}
```
 
> Nota: para que `Main.java` en el módulo `client` pueda instanciar `PatternServer` del módulo `server`, el `pom.xml` del módulo `client` debe declarar dependencia sobre `server` además de `shared`. Si se prefiere mantener estricta separación, `PatternServer` se puede mover a `shared` o ejecutar en un proceso separado.
 
### 7.2 `PatternClient.java`
 
Encapsula la comunicación TCP. Abre un socket nuevo por cada petición (modelo request-response sin estado persistente).
 
```java
package co.airbnb.client;
 
import co.airbnb.shared.JsonMessage;
import co.airbnb.shared.JsonParser;
 
import java.io.*;
import java.net.*;
 
public class PatternClient {
 
    private final String host;
    private final int    port;
 
    public PatternClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
 
    public JsonMessage send(JsonMessage request) {
        try (
            Socket       socket = new Socket(host, port);
            PrintWriter  out    = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in   = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(request.toJson());
            String raw = in.readLine();
            if (raw == null) {
                return errorResponse(request, "El servidor no devolvió respuesta.");
            }
            return JsonParser.parse(raw);
        } catch (IOException e) {
            return errorResponse(request, e.getMessage());
        }
    }
 
    private JsonMessage errorResponse(JsonMessage req, String msg) {
        JsonMessage err = new JsonMessage();
        err.setPattern(req.getPattern());
        err.setAction(req.getAction());
        err.setStatus("ERROR");
        err.setPayload("\"" + msg + "\"");
        return err;
    }
}
```
 
### 7.3 `ConsoleUI.java`
 
Shell principal. Dibuja la barra lateral con los 23 patrones agrupados por categoría, gestiona la navegación y delega a la vista activa.
 
```java
package co.airbnb.client;
 
import co.airbnb.client.views.*;
import co.airbnb.shared.PatternType;
import java.util.*;
 
public class ConsoleUI {
 
    private final PatternClient client;
    private final Map<PatternType, PatternView> views = new LinkedHashMap<>();
    private final Scanner scanner = new Scanner(System.in);
 
    public ConsoleUI(PatternClient client) {
        this.client = client;
        registerViews();
    }
 
    private void registerViews() {
        views.put(PatternType.SINGLETON,               new co.airbnb.client.views.singleton.SingletonView(client));
        views.put(PatternType.FACTORY_METHOD,          new co.airbnb.client.views.factorymethod.FactoryMethodView(client));
        // ... registrar las 23 vistas
    }
 
    public void start() {
        while (true) {
            printSidebar();
            System.out.print("\nSelecciona un patrón (número) o 0 para salir: ");
            int choice = readInt();
            if (choice == 0) break;
 
            PatternType selected = getByIndex(choice);
            if (selected == null) {
                System.out.println("Opción inválida.");
                continue;
            }
 
            PatternView view = views.get(selected);
            view.showInteractive(scanner);
            System.out.println("\n--- Diagrama de clases ---");
            view.showDiagram();
            System.out.println("\nPresiona Enter para volver al menú...");
            scanner.nextLine();
        }
        System.out.println("Cerrando glud-patterns. ¡Hasta luego!");
    }
 
    private void printSidebar() {
        clearScreen();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         glud-patterns · Airbnb       ║");
        System.out.println("║       23 Patrones de Diseño GoF      ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║ CREACIONALES                         ║");
        System.out.println("║  [1]  Singleton                      ║");
        System.out.println("║  [2]  Factory Method                 ║");
        System.out.println("║  [3]  Abstract Factory               ║");
        System.out.println("║  [4]  Builder                        ║");
        System.out.println("║  [5]  Prototype                      ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║ ESTRUCTURALES                        ║");
        System.out.println("║  [6]  Adapter                        ║");
        System.out.println("║  [7]  Bridge                         ║");
        System.out.println("║  [8]  Composite                      ║");
        System.out.println("║  [9]  Decorator                      ║");
        System.out.println("║  [10] Facade                         ║");
        System.out.println("║  [11] Flyweight                      ║");
        System.out.println("║  [12] Proxy                          ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║ DE COMPORTAMIENTO                    ║");
        System.out.println("║  [13] Chain of Responsibility        ║");
        System.out.println("║  [14] Command                        ║");
        System.out.println("║  [15] Interpreter                    ║");
        System.out.println("║  [16] Iterator                       ║");
        System.out.println("║  [17] Mediator                       ║");
        System.out.println("║  [18] Memento                        ║");
        System.out.println("║  [19] Observer                       ║");
        System.out.println("║  [20] State                          ║");
        System.out.println("║  [21] Strategy                       ║");
        System.out.println("║  [22] Template Method                ║");
        System.out.println("║  [23] Visitor                        ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
 
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
 
    private int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
 
    private PatternType getByIndex(int i) {
        List<PatternType> list = new ArrayList<>(views.keySet());
        if (i < 1 || i > list.size()) return null;
        return list.get(i - 1);
    }
}
```
 
### 7.4 `PatternView.java` — interfaz base
 
Todas las vistas implementan esta interfaz. Garantiza que `ConsoleUI` pueda tratar cualquier vista de forma uniforme.
 
```java
package co.airbnb.client.views;
 
import java.util.Scanner;
 
public interface PatternView {
    /**
     * Muestra el menú interactivo del patrón, envía peticiones
     * al servidor y muestra las respuestas.
     */
    void showInteractive(Scanner scanner);
 
    /**
     * Imprime en consola el diagrama de clases en ASCII art
     * exclusivamente de las clases que participan en este patrón.
     */
    void showDiagram();
}
```
 
### 7.5 Estructura de cada vista
 
Todas las vistas siguen el mismo esquema:
 
```java
package co.airbnb.client.views.singleton;
 
import co.airbnb.client.PatternClient;
import co.airbnb.client.views.PatternView;
import co.airbnb.shared.JsonMessage;
import co.airbnb.shared.PatternType;
import java.util.Scanner;
 
public class SingletonView implements PatternView {
 
    private final PatternClient client;
 
    public SingletonView(PatternClient client) {
        this.client = client;
    }
 
    @Override
    public void showInteractive(Scanner scanner) {
        System.out.println("\n══════════════ SINGLETON — ConfigManager ══════════════");
        System.out.println("Situación: Airbnb necesita que toda la aplicación comparta");
        System.out.println("una única instancia de configuración (BD, API keys, etc.)");
        System.out.println();
        System.out.println("[1] Obtener instancia");
        System.out.println("[2] Leer configuración actual");
        System.out.println("[3] Cambiar DB host");
        System.out.println("[4] Verificar unicidad de la instancia");
        System.out.println("[0] Volver");
 
        while (true) {
            System.out.print("\nAcción: ");
            int opt = readInt(scanner);
            if (opt == 0) break;
 
            JsonMessage req = new JsonMessage();
            req.setPattern(PatternType.SINGLETON.name());
 
            switch (opt) {
                case 1 -> req.setAction("GET_INSTANCE");
                case 2 -> req.setAction("GET_CONFIG");
                case 3 -> {
                    System.out.print("Nuevo DB host: ");
                    String host = scanner.nextLine().trim();
                    req.setAction("SET_DB_HOST");
                    req.setPayload("\"" + host + "\"");
                }
                case 4 -> req.setAction("VERIFY_UNIQUE");
                default -> { System.out.println("Opción inválida."); continue; }
            }
 
            JsonMessage res = client.send(req);
            printResponse(res);
        }
    }
 
    @Override
    public void showDiagram() {
        System.out.println("""
            ┌─────────────────────────────────────────────────────────────┐
            │                      SINGLETON                              │
            ├─────────────────────────────────────────────────────────────┤
            │                                                             │
            │   ┌───────────────────────────────────────────────────┐    │
            │   │               <<class>> ConfigManager             │    │
            │   ├───────────────────────────────────────────────────┤    │
            │   │ - instance : ConfigManager  {static}              │    │
            │   │ - dbHost   : String                               │    │
            │   │ - apiKey   : String                               │    │
            │   │ - maxGuests: int                                  │    │
            │   ├───────────────────────────────────────────────────┤    │
            │   │ - ConfigManager()  {constructor privado}          │    │
            │   │ + getInstance() : ConfigManager  {static}        │    │
            │   │ + getDbHost()   : String                         │    │
            │   │ + setDbHost(s: String) : void                    │    │
            │   │ + getApiKey()   : String                         │    │
            │   └───────────────────────────────────────────────────┘    │
            │                          △                                  │
            │                          │ usa                              │
            │   ┌───────────────────────────────────────────────────┐    │
            │   │            SingletonHandler                        │    │
            │   ├───────────────────────────────────────────────────┤    │
            │   │ + handle(req: JsonMessage) : JsonMessage           │    │
            │   └───────────────────────────────────────────────────┘    │
            │                                                             │
            │  Rol ConfigManager : Garantiza una sola instancia global.  │
            │  Rol SingletonHandler: Recibe las peticiones JSON del       │
            │    cliente y delega en ConfigManager.getInstance().         │
            └─────────────────────────────────────────────────────────────┘
        """);
    }
 
    private void printResponse(JsonMessage res) {
        String symbol = "OK".equals(res.getStatus()) ? "✓" : "✗";
        System.out.println("\n" + symbol + " [" + res.getStatus() + "] " + res.getAction());
        System.out.println("  " + res.getPayload().replace(",", ",\n  "));
    }
 
    private int readInt(Scanner sc) {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}
```
 
---
 
## 8. Los 23 patrones — situación, clases y responsabilidades
 
### Patrones creacionales
 
---
 
#### Patrón 01 — Singleton
 
**Situación Airbnb:** La aplicación necesita una única instancia de `ConfigManager` que mantenga la configuración global: host de base de datos, API key de pagos, número máximo de huéspedes por reserva. Si se crearan múltiples instancias, diferentes partes del sistema podrían trabajar con configuraciones inconsistentes.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `ConfigManager` | La clase singleton. Tiene constructor privado, atributo estático `instance` y método estático `getInstance()` que crea la instancia en el primer llamado y la retorna en todos los siguientes. |
| `SingletonHandler` | Handler del servidor. Recibe el `JsonMessage`, extrae la acción y delega en `ConfigManager.getInstance()`. |
 
**Acciones disponibles:** `GET_INSTANCE`, `GET_CONFIG`, `SET_DB_HOST`, `VERIFY_UNIQUE`.
 
---
 
#### Patrón 02 — Factory Method
 
**Situación Airbnb:** Airbnb envía notificaciones a sus usuarios por distintos canales: email, push notification y SMS. La lógica de creación de cada tipo de notificación debe estar encapsulada. Agregar un nuevo canal (por ejemplo, WhatsApp) no debe modificar el código existente.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `Notification` | Clase abstracta base. Declara el método abstracto `send(String message)`. |
| `EmailNotification` | Concreta. Implementa `send()` simulando envío por email. Tiene atributo `recipientEmail`. |
| `PushNotification` | Concreta. Implementa `send()` simulando push. Tiene atributo `deviceToken`. |
| `SmsNotification` | Concreta. Implementa `send()` simulando SMS. Tiene atributo `phoneNumber`. |
| `NotificationFactory` | Clase abstracta que declara `createNotification()` como factory method. |
| `EmailFactory`, `PushFactory`, `SmsFactory` | Fábricas concretas que implementan `createNotification()`. |
| `FactoryMethodHandler` | Handler del servidor. |
 
**Acciones disponibles:** `CREATE_EMAIL`, `CREATE_PUSH`, `CREATE_SMS`, `SEND`.
 
---
 
#### Patrón 03 — Abstract Factory
 
**Situación Airbnb:** La aplicación web y la aplicación móvil de Airbnb necesitan componentes de UI diferentes: los botones y tarjetas tienen distinto estilo en web versus móvil, pero la lógica de construcción debe ser la misma. La Abstract Factory permite cambiar toda la familia de componentes con un solo cambio.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `UIFactory` | Interfaz abstracta. Declara `createButton()` y `createCard()`. |
| `WebUIFactory` | Fábrica concreta para web. Crea `WebButton` y `WebCard`. |
| `MobileUIFactory` | Fábrica concreta para móvil. Crea `MobileButton` y `MobileCard`. |
| `Button` | Interfaz de producto abstracto. Declara `render()`. |
| `Card` | Interfaz de producto abstracto. Declara `display()`. |
| `WebButton`, `MobileButton` | Productos concretos de tipo Button. |
| `WebCard`, `MobileCard` | Productos concretos de tipo Card. |
| `AbstractFactoryHandler` | Handler del servidor. |
 
**Acciones disponibles:** `CREATE_WEB_UI`, `CREATE_MOBILE_UI`, `RENDER_BUTTON`, `DISPLAY_CARD`.
 
---
 
#### Patrón 04 — Builder
 
**Situación Airbnb:** Crear un `Listing` (anuncio de propiedad) implica muchos atributos opcionales: título, descripción, precio por noche, capacidad, lista de amenities, fotos, reglas de la casa, política de cancelación. El Builder permite construir el objeto paso a paso sin un constructor con 15 parámetros.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `Listing` | Producto final. Clase inmutable con todos los atributos de un anuncio. |
| `ListingBuilder` | Builder. Tiene métodos encadenables `withTitle()`, `withPrice()`, `withAmenities()`, etc. El método `build()` retorna el `Listing`. |
| `BuilderHandler` | Handler del servidor. |
 
**Acciones disponibles:** `BUILD_BASIC`, `BUILD_FULL`, `BUILD_CUSTOM`, `GET_LISTING`.
 
---
 
#### Patrón 05 — Prototype
 
**Situación Airbnb:** Un host tiene varias propiedades similares (por ejemplo, varios apartamentos en el mismo edificio). En lugar de configurar cada `Listing` desde cero, puede clonar una plantilla existente y solo modificar los detalles que cambian (número de habitación, piso, precio).
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `ListingTemplate` | Prototipo concreto. Implementa `clone()` (ya sea la interfaz `Cloneable` de Java o una propia). Tiene todos los atributos de un listing y un método `deepCopy()` que retorna una copia independiente. |
| `PrototypeHandler` | Handler del servidor. Mantiene un registro de prototipos disponibles en un `HashMap`. |
 
**Acciones disponibles:** `REGISTER_TEMPLATE`, `CLONE_TEMPLATE`, `LIST_TEMPLATES`, `MODIFY_CLONE`.
 
---
 
### Patrones estructurales
 
---
 
#### Patrón 06 — Adapter
 
**Situación Airbnb:** Airbnb integra múltiples pasarelas de pago. Stripe y PayPal tienen APIs completamente diferentes. El Adapter traduce la interfaz común `PaymentGateway` que usa Airbnb a la API específica de cada proveedor, sin que el resto del sistema sepa con cuál está hablando.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `PaymentGateway` | Interfaz objetivo. Declara `processPayment(double amount, String currency)`. |
| `StripeAPI` | Clase externa (incompatible). Tiene `chargeCard(int cents, String curr)`. |
| `PayPalAPI` | Clase externa (incompatible). Tiene `sendPayment(double usd)`. |
| `StripeAdapter` | Adapter. Implementa `PaymentGateway`, adapta llamadas a `StripeAPI`. |
| `PayPalAdapter` | Adapter. Implementa `PaymentGateway`, adapta llamadas a `PayPalAPI`. |
| `AdapterHandler` | Handler del servidor. |
 
**Acciones disponibles:** `PAY_STRIPE`, `PAY_PAYPAL`, `COMPARE_GATEWAYS`.
 
---
 
#### Patrón 07 — Bridge
 
**Situación Airbnb:** El motor de búsqueda de Airbnb tiene filtros (precio, ubicación, tipo de propiedad) y distintas implementaciones de cómo aplicarlos (búsqueda exacta, búsqueda aproximada, búsqueda por rango). El Bridge separa la abstracción del filtro de su implementación concreta.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `SearchFilter` | Abstracción. Tiene referencia a `FilterImplementor`. Declara `apply(String query)`. |
| `PriceFilter` | Abstracción refinada. Extiende `SearchFilter` con lógica específica de precio. |
| `LocationFilter` | Abstracción refinada. Extiende `SearchFilter` con lógica de ubicación. |
| `FilterImplementor` | Interfaz de implementación. Declara `execute(String params)`. |
| `ExactSearch` | Implementación concreta. Búsqueda exacta. |
| `FuzzySearch` | Implementación concreta. Búsqueda aproximada. |
| `BridgeHandler` | Handler del servidor. |
 
**Acciones disponibles:** `SEARCH_PRICE_EXACT`, `SEARCH_LOCATION_FUZZY`, `SWITCH_IMPLEMENTATION`.
 
---
 
#### Patrón 08 — Composite
 
**Situación Airbnb:** Una propiedad en Airbnb puede ser una habitación individual, un apartamento completo o un conjunto de villas en una misma finca. El Composite trata de forma uniforme tanto las hojas (habitaciones individuales) como los compuestos (propiedades con múltiples unidades).
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `PropertyComponent` | Componente abstracto. Declara `getPrice()`, `getDescription()`, `add()`, `remove()`. |
| `Room` | Hoja. Tiene precio y descripción propios. No puede tener hijos. |
| `PropertyGroup` | Composite. Contiene una lista de `PropertyComponent`. Su precio es la suma de los hijos. |
| `CompositeHandler` | Handler del servidor. |
 
**Acciones disponibles:** `ADD_ROOM`, `ADD_GROUP`, `GET_TOTAL_PRICE`, `LIST_STRUCTURE`.
 
---
 
#### Patrón 09 — Decorator
 
**Situación Airbnb:** Un listing base puede enriquecerse con amenities: wifi, piscina, estacionamiento, desayuno incluido. Cada amenity agrega descripción y costo adicional. El Decorator permite componer amenities dinámicamente sin crear una subclase por cada combinación posible.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `Listing` | Interfaz componente. Declara `getDescription()` y `getPrice()`. |
| `BaseListing` | Componente concreto. Implementación base con descripción y precio mínimos. |
| `ListingDecorator` | Decorator abstracto. Implementa `Listing`, tiene referencia a otro `Listing`. |
| `WifiDecorator` | Decorator concreto. Añade "+Wifi" a la descripción y suma $5/noche. |
| `PoolDecorator` | Decorator concreto. Añade "+Piscina" y suma $20/noche. |
| `ParkingDecorator` | Decorator concreto. Añade "+Estacionamiento" y suma $10/noche. |
| `DecoratorHandler` | Handler del servidor. |
 
**Acciones disponibles:** `CREATE_BASE`, `ADD_WIFI`, `ADD_POOL`, `ADD_PARKING`, `GET_TOTAL`.
 
---
 
#### Patrón 10 — Facade
 
**Situación Airbnb:** Realizar una reserva en Airbnb involucra varios subsistemas: verificar disponibilidad, procesar el pago, crear la reserva en la base de datos y enviar notificaciones al host y al huésped. La Facade simplifica todo esto en una sola llamada `book()`.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `AvailabilityService` | Subsistema. Verifica si el listing está disponible en las fechas solicitadas. |
| `PaymentService` | Subsistema. Procesa el cargo al huésped. |
| `ReservationService` | Subsistema. Crea y persiste la reserva. |
| `NotificationService` | Subsistema. Envía emails/push al host y al huésped. |
| `BookingFacade` | Facade. Orquesta los cuatro subsistemas con un método `book(guestId, listingId, dates)`. |
| `FacadeHandler` | Handler del servidor. |
 
**Acciones disponibles:** `BOOK`, `CANCEL_BOOKING`, `GET_BOOKING_STATUS`.
 
---
 
#### Patrón 11 — Flyweight
 
**Situación Airbnb:** La lista de resultados de búsqueda puede mostrar miles de listings, y cada uno muestra íconos de amenities (wifi, piscina, cocina, etc.). Si se crea un objeto ícono por cada aparición, la memoria se agota. El Flyweight comparte las instancias de íconos comunes entre todos los listings.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `AmenityIcon` | Flyweight concreto. Almacena el estado intrínseco: nombre y símbolo ASCII del amenity. |
| `AmenityIconFactory` | Fábrica de Flyweights. Mantiene un `HashMap<String, AmenityIcon>`. Si el ícono ya existe lo retorna; si no, lo crea y lo almacena. |
| `FlyweightHandler` | Handler del servidor. Demuestra cuántas instancias se crean con y sin Flyweight. |
 
**Acciones disponibles:** `GET_ICON`, `LIST_SHARED_ICONS`, `COMPARE_MEMORY`, `SHOW_CACHE`.
 
---
 
#### Patrón 12 — Proxy
 
**Situación Airbnb:** Acceder al perfil completo de un usuario (fotos, historial, documentos de verificación) es costoso. El Proxy controla ese acceso: implementa caché para no consultar el backend cada vez, y verifica que el solicitante tenga permisos antes de devolver información sensible.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `UserProfile` | Interfaz. Declara `getProfile(String userId)`. |
| `RealUserProfile` | Sujeto real. Implementa `getProfile()` simulando acceso costoso a BD. |
| `UserProfileProxy` | Proxy. Implementa `UserProfile`. Mantiene caché en `HashMap`. Verifica permisos antes de delegar a `RealUserProfile`. |
| `ProxyHandler` | Handler del servidor. |
 
**Acciones disponibles:** `GET_PROFILE`, `GET_PROFILE_CACHED`, `CLEAR_CACHE`, `VERIFY_ACCESS`.
 
---
 
### Patrones de comportamiento
 
---
 
#### Patrón 13 — Chain of Responsibility
 
**Situación Airbnb:** Cuando un huésped escribe una reseña, pasa por una cadena de filtros antes de publicarse: filtro de spam, filtro de palabras inapropiadas, filtro de longitud mínima y filtro de idioma. Cada filtro decide si la reseña pasa al siguiente o es rechazada.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `ReviewHandler` | Handler abstracto de la cadena. Tiene referencia al siguiente handler. Declara `handleReview(String review)`. |
| `SpamFilter` | Eslabón concreto. Rechaza reseñas con URLs o patrones de spam. |
| `ProfanityFilter` | Eslabón concreto. Rechaza reseñas con palabras prohibidas. |
| `LengthFilter` | Eslabón concreto. Rechaza reseñas menores a 10 caracteres. |
| `PublishHandler` | Eslabón final. Si la reseña llega hasta aquí, se publica. |
| `ChainHandler` | Handler del servidor. Construye y ejecuta la cadena. |
 
**Acciones disponibles:** `MODERATE_REVIEW`, `SHOW_CHAIN`, `TEST_SPAM`, `TEST_PROFANITY`.
 
---
 
#### Patrón 14 — Command
 
**Situación Airbnb:** Las acciones sobre una reserva (crear, cancelar, modificar fechas, aplicar descuento) deben poder deshacerse y rehacerse. El Command encapsula cada acción como un objeto con métodos `execute()` y `undo()`.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `BookingCommand` | Interfaz. Declara `execute()` y `undo()`. |
| `CreateBookingCommand` | Comando concreto. Crea una reserva. `undo()` la cancela. |
| `CancelBookingCommand` | Comando concreto. Cancela. `undo()` la restaura. |
| `ModifyDatesCommand` | Comando concreto. Cambia fechas. `undo()` restaura las anteriores. |
| `BookingInvoker` | Invocador. Mantiene una pila de comandos ejecutados para soportar undo. |
| `Booking` | Receptor. Objeto de reserva que los comandos manipulan. |
| `CommandHandler` | Handler del servidor. |
 
**Acciones disponibles:** `CREATE`, `CANCEL`, `MODIFY_DATES`, `UNDO`, `SHOW_HISTORY`.
 
---
 
#### Patrón 15 — Interpreter
 
**Situación Airbnb:** El buscador de Airbnb acepta expresiones de filtro como "ciudad=Bogotá AND precio<100 AND tipo=apartamento". El Interpreter define una gramática para estas expresiones y las evalúa sobre la lista de listings.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `SearchExpression` | Interfaz abstracta. Declara `interpret(List<Listing> listings)`. |
| `CityExpression` | Terminal. Filtra por ciudad. |
| `PriceExpression` | Terminal. Filtra por precio máximo. |
| `TypeExpression` | Terminal. Filtra por tipo de propiedad. |
| `AndExpression` | No terminal. Combina dos expresiones con AND (intersección). |
| `OrExpression` | No terminal. Combina dos expresiones con OR (unión). |
| `InterpreterHandler` | Handler del servidor. Parsea la query y construye el árbol de expresiones. |
 
**Acciones disponibles:** `SEARCH`, `SHOW_GRAMMAR`, `PARSE_QUERY`.
 
---
 
#### Patrón 16 — Iterator
 
**Situación Airbnb:** El catálogo de listings se puede recorrer de distintas maneras: por orden de precio, por distancia, por valoración. El Iterator proporciona una forma uniforme de recorrer la colección sin exponer su estructura interna.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `ListingIterator` | Interfaz Iterator. Declara `hasNext()` y `next()`. |
| `PriceIterator` | Iterador concreto. Recorre la colección ordenada por precio ascendente. |
| `RatingIterator` | Iterador concreto. Recorre por valoración descendente. |
| `ListingCollection` | Colección. Tiene `createPriceIterator()` y `createRatingIterator()`. |
| `Listing` | Elemento de la colección. Tiene precio, valoración y nombre. |
| `IteratorHandler` | Handler del servidor. |
 
**Acciones disponibles:** `ITERATE_BY_PRICE`, `ITERATE_BY_RATING`, `LIST_ALL`, `NEXT`, `HAS_NEXT`.
 
---
 
#### Patrón 17 — Mediator
 
**Situación Airbnb:** El sistema de mensajería entre hosts y huéspedes pasa por un mediador central que registra los mensajes, los enruta al destinatario correcto y puede aplicar filtros o traducciones. Ningún usuario se comunica directamente con otro.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `ChatMediator` | Interfaz mediador. Declara `sendMessage(String msg, User sender)` y `addUser(User user)`. |
| `AirbnbChatMediator` | Mediador concreto. Mantiene lista de usuarios conectados y enruta mensajes. |
| `User` | Clase abstracta colega. Tiene referencia al mediador. Declara `send()` y `receive()`. |
| `Host` | Colega concreto. |
| `Guest` | Colega concreto. |
| `MediatorHandler` | Handler del servidor. Mantiene el mediador activo en sesión. |
 
**Acciones disponibles:** `ADD_HOST`, `ADD_GUEST`, `SEND_MESSAGE`, `SHOW_CHAT`.
 
---
 
#### Patrón 18 — Memento
 
**Situación Airbnb:** Un host está editando los precios y disponibilidad de su listing. Si comete un error, quiere deshacer los cambios. El Memento guarda snapshots del estado del listing para poder restaurar versiones anteriores.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `Booking` | Originador. Su estado (fechas, precio, estado) puede guardarse y restaurarse. |
| `BookingMemento` | Memento. Almacena una copia inmutable del estado del `Booking`. Constructor package-private. |
| `BookingCaretaker` | Cuidador. Mantiene una pila de `BookingMemento`. Provee `save()` y `restore()`. |
| `MementoHandler` | Handler del servidor. |
 
**Acciones disponibles:** `CREATE_BOOKING`, `MODIFY_PRICE`, `MODIFY_DATES`, `SAVE_STATE`, `UNDO`, `SHOW_HISTORY`.
 
---
 
#### Patrón 19 — Observer
 
**Situación Airbnb:** Los huéspedes pueden suscribirse a alertas de precio de un listing. Cuando el host cambia el precio, todos los suscriptores reciben una notificación automáticamente.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `PriceObservable` | Interfaz sujeto. Declara `subscribe()`, `unsubscribe()` y `notifyObservers()`. |
| `ListingPrice` | Sujeto concreto. Mantiene la lista de observadores y notifica cuando cambia el precio. |
| `PriceObserver` | Interfaz observador. Declara `update(double newPrice)`. |
| `GuestAlert` | Observador concreto. Recibe la notificación y simula mostrar la alerta al huésped. |
| `ObserverHandler` | Handler del servidor. Mantiene el sujeto activo en sesión. |
 
**Acciones disponibles:** `SUBSCRIBE`, `UNSUBSCRIBE`, `CHANGE_PRICE`, `LIST_SUBSCRIBERS`, `SHOW_ALERTS`.
 
---
 
#### Patrón 20 — State
 
**Situación Airbnb:** Una reserva en Airbnb pasa por estados bien definidos: pendiente (esperando confirmación del host), confirmada, activa (huésped en la propiedad), completada y cancelada. Cada estado permite o prohíbe ciertas transiciones y acciones.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `BookingState` | Interfaz estado. Declara `confirm()`, `cancel()`, `checkIn()`, `checkOut()`. |
| `PendingState` | Estado concreto. Solo permite `confirm()` y `cancel()`. |
| `ConfirmedState` | Estado concreto. Permite `cancel()` y `checkIn()`. |
| `ActiveState` | Estado concreto. Solo permite `checkOut()`. |
| `CompletedState` | Estado final. Ninguna transición posible. |
| `CancelledState` | Estado final. Ninguna transición posible. |
| `BookingContext` | Contexto. Mantiene el estado actual y delega las acciones en él. |
| `StateHandler` | Handler del servidor. |
 
**Acciones disponibles:** `CREATE_BOOKING`, `CONFIRM`, `CANCEL`, `CHECK_IN`, `CHECK_OUT`, `SHOW_STATE`.
 
---
 
#### Patrón 21 — Strategy
 
**Situación Airbnb:** Airbnb ofrece distintas estrategias de precio para un listing: precio por noche, descuento semanal, precio de temporada alta. El host puede cambiar la estrategia activa sin modificar la clase principal del listing.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `PricingStrategy` | Interfaz estrategia. Declara `calculatePrice(int nights, double basePrice)`. |
| `NightlyPricing` | Estrategia concreta. Precio = basePrice × nights. |
| `WeeklyPricing` | Estrategia concreta. Aplica 10% de descuento por semana completa. |
| `SeasonalPricing` | Estrategia concreta. Aplica recargo del 30% en temporada alta. |
| `PricingContext` | Contexto. Tiene una referencia a `PricingStrategy` que puede cambiarse en runtime. |
| `StrategyHandler` | Handler del servidor. |
 
**Acciones disponibles:** `SET_NIGHTLY`, `SET_WEEKLY`, `SET_SEASONAL`, `CALCULATE`, `COMPARE_ALL`.
 
---
 
#### Patrón 22 — Template Method
 
**Situación Airbnb:** El proceso de check-in siempre sigue los mismos pasos en orden: verificar reserva, validar identidad, entregar llaves/código, dar bienvenida. Pero el detalle de cada paso varía: un check-in estándar es diferente a uno de villa de lujo. El Template Method define el esqueleto fijo y deja que las subclases personalicen los pasos variables.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `CheckInProcess` | Clase abstracta. Define el template method `doCheckIn()` que llama en orden a: `verifyReservation()`, `validateIdentity()`, `deliverAccess()`, `giveWelcome()`. Los últimos dos son abstractos. |
| `StandardCheckIn` | Subclase concreta. Implementa `deliverAccess()` entregando código de caja fuerte y `giveWelcome()` con mensaje estándar. |
| `LuxuryCheckIn` | Subclase concreta. Implementa `deliverAccess()` con concierge presencial y `giveWelcome()` con champagne. |
| `TemplateMethodHandler` | Handler del servidor. |
 
**Acciones disponibles:** `STANDARD_CHECKIN`, `LUXURY_CHECKIN`, `SHOW_STEPS`.
 
---
 
#### Patrón 23 — Visitor
 
**Situación Airbnb:** Airbnb necesita calcular estadísticas diferentes sobre sus propiedades: ingresos esperados, impuestos, puntaje de calidad. Cada cálculo es diferente según el tipo de propiedad (apartamento, villa, habitación compartida). El Visitor separa el algoritmo del objeto sobre el que opera.
 
**Clases del patrón:**
 
| Clase | Rol |
|---|---|
| `PropertyElement` | Interfaz elemento. Declara `accept(PropertyVisitor visitor)`. |
| `Apartment` | Elemento concreto. Implementa `accept()` llamando `visitor.visitApartment(this)`. |
| `Villa` | Elemento concreto. Implementa `accept()` llamando `visitor.visitVilla(this)`. |
| `SharedRoom` | Elemento concreto. Implementa `accept()` llamando `visitor.visitSharedRoom(this)`. |
| `PropertyVisitor` | Interfaz visitante. Declara `visitApartment()`, `visitVilla()`, `visitSharedRoom()`. |
| `RevenueCalculator` | Visitante concreto. Calcula ingresos proyectados según tipo. |
| `TaxCalculator` | Visitante concreto. Calcula impuestos según tipo y precio. |
| `VisitorHandler` | Handler del servidor. |
 
**Acciones disponibles:** `CALCULATE_REVENUE`, `CALCULATE_TAX`, `LIST_PROPERTIES`, `ADD_PROPERTY`.
 
---
 
## 9. Convenciones de código
 
### Nomenclatura
 
| Elemento | Convención | Ejemplo |
|---|---|---|
| Paquetes | lowercase, separados por punto | `co.airbnb.server.patterns.singleton` |
| Clases | PascalCase | `ConfigManager`, `SingletonHandler` |
| Interfaces | PascalCase, prefijo `I` solo en shared | `IPatternHandler`, `PatternView` |
| Métodos | camelCase | `getInstance()`, `handleReview()` |
| Constantes | SCREAMING_SNAKE_CASE | `MAX_RETRIES`, `DEFAULT_PORT` |
| Variables locales | camelCase | `newPrice`, `listingId` |
| Atributos de clase | camelCase | `dbHost`, `apiKey` |
 
### Reglas generales
 
- Cada clase en su propio archivo `.java`.
- Máximo 80 caracteres por línea.
- Comentarios Javadoc en todas las clases y métodos públicos.
- Cada handler tiene exactamente un `switch` sobre `request.getAction()`.
- Los `model/` del servidor no importan nada de `client/` ni de otros patrones.
- Las vistas del cliente no contienen lógica de negocio; solo construyen `JsonMessage` y muestran respuestas.
- Ninguna clase de `server/` imprime con `System.out` durante operación normal; los logs van a `System.err`.
- El payload de las respuestas siempre es un JSON válido (objeto `{}` o string `"..."`), nunca un valor primitivo suelto.
### Estructura de un método `handle()`
 
```
1. Crear objeto response con pattern y action del request
2. Switch sobre action
3. Cada case: ejecutar lógica, construir payload, setear status "OK"
4. Default: setear status "ERROR" con mensaje descriptivo
5. Retornar response
```
 
### Estructura de un método `showDiagram()`
 
```
1. Imprimir borde superior
2. Imprimir título del patrón
3. Imprimir cada clase con sus atributos y métodos en notación UML simplificada
4. Imprimir las relaciones entre clases con flechas ASCII
5. Imprimir descripción del rol de cada clase
6. Imprimir borde inferior
```
 
---
 
## 10. Plan de implementación por fases
 
### Fase 1 — Esqueleto base (realizar primero, todo el equipo)
 
**Objetivo:** Tener la app corriendo de punta a punta con un solo patrón antes de escalar a los 23.
 
| Tarea | Descripción |
|---|---|
| 1.1 | Crear el repositorio en GitHub con la estructura de carpetas completa |
| 1.2 | Crear el `pom.xml` padre con los tres módulos declarados |
| 1.3 | Crear los `pom.xml` de `shared`, `server` y `client` con las dependencias correctas |
| 1.4 | Implementar `PatternType.java`, `JsonMessage.java`, `JsonParser.java`, `IPatternHandler.java` en `shared` |
| 1.5 | Implementar `PatternServer.java` y `PatternRouter.java` en `server` |
| 1.6 | Implementar `PatternClient.java` y `ConsoleUI.java` (menú con 23 entradas, todas vacías) en `client` |
| 1.7 | Implementar `PatternView.java` (interfaz) en `client/views` |
| 1.8 | Implementar `Main.java` que lanza servidor y cliente |
| 1.9 | Implementar el patrón Singleton completo (handler + model + view) como prueba de concepto |
| 1.10 | Verificar que el flujo completo funciona: cliente envía JSON → servidor responde → cliente muestra |
 
**Criterio de éxito de la fase 1:** `mvn package` compila sin errores y el Singleton responde correctamente desde el cliente.
 
---
 
### Fase 2 — Patrones creacionales (patrones 02 al 05)
 
| Tarea | Descripción |
|---|---|
| 2.1 | Implementar Factory Method: modelo + handler + view |
| 2.2 | Implementar Abstract Factory: modelo + handler + view |
| 2.3 | Implementar Builder: modelo + handler + view |
| 2.4 | Implementar Prototype: modelo + handler + view |
| 2.5 | Registrar los 4 handlers nuevos en `PatternRouter` |
| 2.6 | Registrar las 4 vistas nuevas en `ConsoleUI` |
| 2.7 | Prueba de integración: navegar a cada patrón y ejecutar todas sus acciones |
 
---
 
### Fase 3 — Patrones estructurales (patrones 06 al 12)
 
| Tarea | Descripción |
|---|---|
| 3.1 | Implementar Adapter: modelo + handler + view |
| 3.2 | Implementar Bridge: modelo + handler + view |
| 3.3 | Implementar Composite: modelo + handler + view |
| 3.4 | Implementar Decorator: modelo + handler + view |
| 3.5 | Implementar Facade: modelo + handler + view |
| 3.6 | Implementar Flyweight: modelo + handler + view |
| 3.7 | Implementar Proxy: modelo + handler + view |
| 3.8 | Registrar los 7 handlers y vistas nuevas |
| 3.9 | Prueba de integración completa de estructurales |
 
---
 
### Fase 4 — Patrones de comportamiento (patrones 13 al 23)
 
| Tarea | Descripción |
|---|---|
| 4.1 | Implementar Chain of Responsibility |
| 4.2 | Implementar Command |
| 4.3 | Implementar Interpreter |
| 4.4 | Implementar Iterator |
| 4.5 | Implementar Mediator |
| 4.6 | Implementar Memento |
| 4.7 | Implementar Observer |
| 4.8 | Implementar State |
| 4.9 | Implementar Strategy |
| 4.10 | Implementar Template Method |
| 4.11 | Implementar Visitor |
| 4.12 | Registrar los 11 handlers y vistas nuevas |
| 4.13 | Prueba de integración completa de comportamentales |
 
---
 
### Fase 5 — Pulido y documentación
 
| Tarea | Descripción |
|---|---|
| 5.1 | Revisar que todos los diagramas ASCII en `showDiagram()` sean correctos y completos |
| 5.2 | Agregar Javadoc a todas las clases y métodos públicos |
| 5.3 | Revisar que ninguna clase de `model/` tenga dependencias cruzadas entre patrones |
| 5.4 | Refinar los mensajes de output en consola para que sean claros y bien formateados |
| 5.5 | Exportar capturas de pantalla de cada patrón en ejecución para el informe |
| 5.6 | Compilar el JAR ejecutable con `mvn package` y verificar que corre con `java -jar` |
| 5.7 | Redactar el `README.md` con instrucciones de compilación y ejecución |
 
---
 
### Fase 6 — Informe LaTeX
 
| Tarea | Descripción |
|---|---|
| 6.1 | Redactar portada con datos del equipo y la materia |
| 6.2 | Redactar tabla de contenido |
| 6.3 | Redactar introducción al proyecto |
| 6.4 | Redactar objetivos general y específicos |
| 6.5 | Por cada uno de los 23 patrones: redactar situación, incluir diagrama, describir roles, incluir código, incluir captura de prueba |
| 6.6 | Redactar conclusiones individuales por patrón |
| 6.7 | Compilar el PDF y verificar que no hay errores de LaTeX |
 
---
 
## 11. Estructura del informe (rúbrica)
 
El informe sigue estrictamente la rúbrica del profesor. A continuación se detalla qué debe contener cada sección.
 
### Portada
 
- Nombre de la universidad
- Facultad y programa
- Nombre de la materia: Modelos de Programación
- Nombre del proyecto: Aplicación de los 23 Patrones de Diseño GoF — Contexto Airbnb
- Nombres completos de los integrantes del equipo
- Nombre del profesor
- Ciudad y fecha
### Tabla de contenido (agenda)
 
Generada automáticamente en LaTeX con `\tableofcontents`. Debe reflejar exactamente la estructura del documento.
 
### Introducción
 
Debe cubrir:
- Qué son los patrones de diseño y por qué son importantes
- El catálogo GoF y sus tres categorías
- Por qué se eligió Airbnb como contexto narrativo
- Descripción breve de la arquitectura cliente-servidor con JSON
- Descripción breve de la programación modular aplicada
### Objetivos
 
**Objetivo general:**
Implementar los 23 patrones de diseño del catálogo GoF aplicados al contexto de la plataforma Airbnb, mediante una aplicación Java cliente-servidor con protocolo JSON, demostrando la aplicación de programación modular.
 
**Objetivos específicos:** (uno por categoría de patrón, más uno de arquitectura)
1. Implementar los 5 patrones creacionales mostrando cómo resuelven problemas de creación de objetos en el contexto de Airbnb.
2. Implementar los 7 patrones estructurales mostrando cómo organizan las relaciones entre clases en Airbnb.
3. Implementar los 11 patrones de comportamiento mostrando cómo gestionan la comunicación entre objetos en Airbnb.
4. Diseñar una arquitectura cliente-servidor modular con protocolo JSON que permita demostrar cada patrón de forma aislada e interactiva.
### Por cada patrón (repetir 23 veces)
 
Cada sección de patrón debe contener, en este orden:
 
#### a) Situación
Párrafo de 3 a 5 oraciones que describa el problema concreto de Airbnb que resuelve el patrón. Debe responder: ¿qué problema existe? ¿por qué el patrón es la solución adecuada?
 
#### b) Diagrama UML
Diagrama de clases con solo las clases que participan en el patrón. No incluir `XxxHandler` ni las clases de infraestructura. Mostrar: nombre de la clase, atributos con tipos, métodos con parámetros y tipos de retorno, relaciones (herencia, implementación, composición, uso). El diagrama puede ser generado con PlantUML, draw.io o a mano en LaTeX con `tikz`.
 
#### c) Descripción — rol de cada clase
Tabla con dos columnas: Clase y Rol. Describir en 1 a 2 oraciones qué responsabilidad tiene cada clase dentro del patrón.
 
#### d) Código
Incluir el código fuente de todas las clases del patrón (las de `model/` y el handler). Usar el entorno `lstlisting` de LaTeX con resaltado de sintaxis Java. No incluir el código de `shared/` ni del cliente en esta sección.
 
#### e) Prueba
Captura de pantalla o transcripción del output de la consola mostrando el patrón en ejecución. Debe mostrar al menos dos acciones diferentes. Incluir el JSON de request y response para que sea evidente el uso del protocolo.
 
### Conclusiones
 
Una conclusión por patrón (23 en total). Cada conclusión debe:
- Resumir en qué situación real de Airbnb se aplicó el patrón
- Explicar qué problema resuelve y qué ventaja aporta
- Mencionar una situación del mundo real donde el patrón podría aplicarse igualmente
---
 
## 12. Checklist de entrega
 
### Código
 
- [ ] El proyecto compila con `mvn clean package` sin errores ni warnings
- [ ] La aplicación arranca con `java -jar client/target/client-1.0.jar`
- [ ] Los 23 patrones aparecen en el menú lateral
- [ ] Cada patrón tiene al menos 3 acciones funcionales
- [ ] Cada patrón muestra su diagrama de clases en ASCII al seleccionarlo
- [ ] El protocolo JSON funciona correctamente (request y response bien formados)
- [ ] No hay dependencias entre paquetes de diferentes patrones
- [ ] No hay lógica de negocio en las vistas del cliente
- [ ] No hay `System.out` en las clases de modelo del servidor
- [ ] Todos los métodos públicos tienen Javadoc
### Repositorio
 
- [ ] El repositorio está en GitHub con commits atómicos y mensajes descriptivos
- [ ] El `README.md` explica cómo compilar y ejecutar el proyecto
- [ ] La carpeta `docs/` contiene el informe PDF compilado
- [ ] La carpeta `docs/diagrams/` contiene los diagramas UML de los 23 patrones
### Informe
 
- [ ] Portada completa con todos los datos
- [ ] Tabla de contenido generada automáticamente
- [ ] Introducción de al menos una página
- [ ] Objetivos general y específicos bien redactados
- [ ] Los 23 patrones con situación, diagrama, roles, código y prueba
- [ ] Conclusiones para los 23 patrones
- [ ] El PDF compila sin errores de LaTeX
- [ ] Las imágenes de los diagramas están en alta resolución
---
 
*Documento generado para el equipo de desarrollo de glud-patterns · Universidad Distrital · Modelos de Programación*