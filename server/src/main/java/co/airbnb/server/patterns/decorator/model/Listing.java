package co.airbnb.server.patterns.decorator.model;

public abstract class Listing {

    protected final String name;
    protected final double price;

    protected Listing(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public abstract String description();
}