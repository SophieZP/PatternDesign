package co.airbnb.server.patterns.composite.model;

public abstract class PropertyComponent {

    protected final String name;

    protected PropertyComponent(String name) {
        this.name = name;
    }

    public abstract String show();
}