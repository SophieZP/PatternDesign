package co.airbnb.shared;

import java.util.List;

public class JsonParser {

	public String toJson(JsonMessage message) {
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		builder.append("\"pattern\":").append(stringValue(message.getPattern())).append(',');
		builder.append("\"input\":").append(stringValue(message.getInput())).append(',');
		builder.append("\"result\":").append(stringValue(message.getResult())).append(',');
		builder.append("\"success\":").append(message.isSuccess());
		if (message.getError() != null) {
			builder.append(',').append("\"error\":").append(stringValue(message.getError()));
		}
		builder.append('}');
		return builder.toString();
	}

	public JsonMessage fromJson(String json) {
		JsonMessage message = new JsonMessage();
		message.setPattern(extractString(json, "pattern"));
		message.setInput(extractString(json, "input"));
		message.setResult(extractString(json, "result"));
		String successValue = extractLiteral(json, "success");
		if (successValue != null) {
			message.setSuccess(Boolean.parseBoolean(successValue));
		}
		message.setError(extractString(json, "error"));
		return message;
	}

	public String patternTypesToJson(List<PatternType> patternTypes) {
		StringBuilder builder = new StringBuilder();
		builder.append('{').append("\"patterns\":").append('[');
		for (int index = 0; index < patternTypes.size(); index++) {
			PatternType patternType = patternTypes.get(index);
			builder.append('{');
			builder.append("\"code\":").append(stringValue(patternType.getCode())).append(',');
			builder.append("\"name\":").append(stringValue(patternType.getDisplayName())).append(',');
			builder.append("\"description\":").append(stringValue(patternType.getDescription()));
			builder.append('}');
			if (index < patternTypes.size() - 1) {
				builder.append(',');
			}
		}
		builder.append(']').append('}');
		return builder.toString();
	}

	private String extractString(String json, String fieldName) {
		String value = extractLiteral(json, fieldName);
		return value == null ? null : unescape(value);
	}

	private String extractLiteral(String json, String fieldName) {
		if (json == null) {
			return null;
		}
		String search = "\"" + fieldName + "\"";
		int fieldIndex = json.indexOf(search);
		if (fieldIndex < 0) {
			return null;
		}
		int colonIndex = json.indexOf(':', fieldIndex + search.length());
		if (colonIndex < 0) {
			return null;
		}
		int startIndex = colonIndex + 1;
		while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
			startIndex++;
		}
		if (startIndex >= json.length()) {
			return null;
		}
		if (json.charAt(startIndex) == '"') {
			int endIndex = startIndex + 1;
			boolean escaped = false;
			while (endIndex < json.length()) {
				char current = json.charAt(endIndex);
				if (current == '"' && !escaped) {
					break;
				}
				escaped = current == '\\' && !escaped;
				if (current != '\\') {
					escaped = false;
				}
				endIndex++;
			}
			return json.substring(startIndex + 1, endIndex);
		}
		int endIndex = startIndex;
		while (endIndex < json.length()) {
			char current = json.charAt(endIndex);
			if (current == ',' || current == '}' || Character.isWhitespace(current)) {
				break;
			}
			endIndex++;
		}
		return json.substring(startIndex, endIndex);
	}

	private String stringValue(String value) {
		if (value == null) {
			return "null";
		}
		return '"' + escape(value) + '"';
	}

	private String escape(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private String unescape(String value) {
		return value.replace("\\\"", "\"").replace("\\\\", "\\");
	}
}
