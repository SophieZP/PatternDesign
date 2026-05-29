package co.airbnb.server.patterns.iterator.model;

public class Listing {

    private final String name;

    public Listing(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}