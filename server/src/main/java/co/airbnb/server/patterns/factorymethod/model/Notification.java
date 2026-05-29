package co.airbnb.server.patterns.factorymethod.model;

public abstract class Notification {

    protected final String message;

    protected Notification(String message) {
        this.message = message;
    }

    public abstract String send();
}