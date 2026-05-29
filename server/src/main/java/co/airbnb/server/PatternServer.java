package co.airbnb.server;

import co.airbnb.shared.JsonMessage;
import co.airbnb.shared.JsonParser;
import co.airbnb.shared.PatternType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class PatternServer {

	private static final JsonParser JSON_PARSER = new JsonParser();
	private static final PatternRouter ROUTER = new PatternRouter();

	public static void main(String[] args) throws Exception {
		if (args != null && args.length > 0) {
			System.out.println(ROUTER.run(args[0]));
			return;
		}

		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		server.createContext("/api/patterns/run", PatternServer::handleRunPattern);
		server.createContext("/api/patterns", PatternServer::handleCatalog);
		server.createContext("/", PatternServer::handleStaticFiles);
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println("Servidor activo en http://localhost:8080");
	}

	private static void handleCatalog(HttpExchange exchange) throws IOException {
		addCorsHeaders(exchange);
		if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(204, -1);
			return;
		}
		if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			sendText(exchange, 405, "Method Not Allowed");
			return;
		}
		String body = JSON_PARSER.patternTypesToJson(Arrays.asList(PatternType.values()));
		sendJson(exchange, 200, body);
	}

	private static void handleRunPattern(HttpExchange exchange) throws IOException {
		addCorsHeaders(exchange);
		if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(204, -1);
			return;
		}
		if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			sendText(exchange, 405, "Method Not Allowed");
			return;
		}
		String requestBody = readBody(exchange.getRequestBody());
		JsonMessage requestMessage = JSON_PARSER.fromJson(requestBody);
		PatternType patternType = PatternType.fromValue(requestMessage.getPattern());
		if (patternType == null) {
			sendJson(exchange, 400, JSON_PARSER.toJson(JsonMessage.failure(requestMessage.getPattern(), requestMessage.getInput(), "Patrón no válido")));
			return;
		}
		String result = ROUTER.run(patternType.getCode());
		JsonMessage response = JsonMessage.success(patternType.getCode(), requestMessage.getInput(), result);
		sendJson(exchange, 200, JSON_PARSER.toJson(response));
	}

	private static void handleStaticFiles(HttpExchange exchange) throws IOException {
		addCorsHeaders(exchange);
		if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(204, -1);
			return;
		}
		if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			sendText(exchange, 405, "Method Not Allowed");
			return;
		}
		String requestPath = exchange.getRequestURI().getPath();
		String relativePath = "/".equals(requestPath) ? "public/index.html" : mapPath(requestPath);
		Path resourcePath = resolveResource(relativePath);
		if (resourcePath == null || !Files.exists(resourcePath)) {
			sendText(exchange, 404, "Not Found");
			return;
		}
		byte[] content = Files.readAllBytes(resourcePath);
		exchange.getResponseHeaders().set("Content-Type", contentType(relativePath));
		exchange.sendResponseHeaders(200, content.length);
		try (OutputStream outputStream = exchange.getResponseBody()) {
			outputStream.write(content);
		}
	}

	private static String mapPath(String requestPath) {
		if (requestPath.startsWith("/diagrams/")) {
			return "docs" + requestPath;
		}
		if (requestPath.startsWith("/data/")) {
			return requestPath.substring(1);
		}
		return "public/" + requestPath.substring(1);
	}

	private static Path resolveResource(String relativePath) {
		List<Path> candidates = Arrays.asList(
				Paths.get(relativePath),
				Paths.get("client", "src", "main", "resources", relativePath),
				Paths.get("docs", relativePath),
				Paths.get("glud-patterns", "client", "src", "main", "resources", relativePath),
				Paths.get("glud-patterns", relativePath),
				Paths.get("..", "client", "src", "main", "resources", relativePath),
				Paths.get("..", "docs", relativePath),
				Paths.get("..", "glud-patterns", "client", "src", "main", "resources", relativePath),
				Paths.get("..", "glud-patterns", relativePath),
				Paths.get("..", "..", "glud-patterns", "client", "src", "main", "resources", relativePath));
		for (Path candidate : candidates) {
			if (Files.exists(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	private static String contentType(String relativePath) {
		if (relativePath.endsWith(".html")) {
			return "text/html; charset=UTF-8";
		}
		if (relativePath.endsWith(".css")) {
			return "text/css; charset=UTF-8";
		}
		if (relativePath.endsWith(".js")) {
			return "application/javascript; charset=UTF-8";
		}
		if (relativePath.endsWith(".json")) {
			return "application/json; charset=UTF-8";
		}
		if (relativePath.endsWith(".puml")) {
			return "text/plain; charset=UTF-8";
		}
		return "application/octet-stream";
	}

	private static String readBody(InputStream inputStream) throws IOException {
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	private static void sendJson(HttpExchange exchange, int statusCode, String body) throws IOException {
		sendTextWithType(exchange, statusCode, body, "application/json; charset=UTF-8");
	}

	private static void sendText(HttpExchange exchange, int statusCode, String body) throws IOException {
		sendTextWithType(exchange, statusCode, body, "text/plain; charset=UTF-8");
	}

	private static void sendTextWithType(HttpExchange exchange, int statusCode, String body, String contentType) throws IOException {
		byte[] content = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", contentType);
		exchange.sendResponseHeaders(statusCode, content.length);
		try (OutputStream outputStream = exchange.getResponseBody()) {
			outputStream.write(content);
		}
	}

	private static void addCorsHeaders(HttpExchange exchange) {
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
			exchange.getResponseHeaders().set("Content-Length", "0");
		}
	}
}
