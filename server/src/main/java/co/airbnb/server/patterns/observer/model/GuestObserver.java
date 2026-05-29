package co.airbnb.server.patterns.observer.model;

public class GuestObserver implements ListingObserver {

    private final String name;

    public GuestObserver(String name) {
        this.name = name;
    }

    @Override
    public String update(String listingName, double newPrice) {
        return name + " notificado: " + listingName + " -> " + newPrice;
    }
}