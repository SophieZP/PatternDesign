package co.airbnb.server.patterns.bridge.model;

public interface FilterImplementor {
    boolean matches(String value, String criteria);
}