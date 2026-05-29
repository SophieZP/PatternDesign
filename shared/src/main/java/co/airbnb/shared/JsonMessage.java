package co.airbnb.shared;

public class JsonMessage {

	private String pattern;
	private String input;
	private String result;
	private boolean success;
	private String error;

	public JsonMessage() {
	}

	public JsonMessage(String pattern, String input, String result, boolean success, String error) {
		this.pattern = pattern;
		this.input = input;
		this.result = result;
		this.success = success;
		this.error = error;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public static JsonMessage success(String pattern, String input, String result) {
		return new JsonMessage(pattern, input, result, true, null);
	}

	public static JsonMessage failure(String pattern, String input, String error) {
		return new JsonMessage(pattern, input, null, false, error);
	}
}
