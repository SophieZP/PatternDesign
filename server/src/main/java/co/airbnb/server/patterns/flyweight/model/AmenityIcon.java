package co.airbnb.server.patterns.flyweight.model;

public class AmenityIcon {

    private final String name;

    public AmenityIcon(String name) {
        this.name = name;
    }

    public String render(int listingId) {
        return name + "@" + listingId;
    }
}