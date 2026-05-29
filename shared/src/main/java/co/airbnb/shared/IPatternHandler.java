package co.airbnb.shared;

@FunctionalInterface
public interface IPatternHandler {

	String handle(JsonMessage request);
}
