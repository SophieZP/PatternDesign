package co.airbnb.server.patterns.observer.model;

public interface ListingObserver {
    String update(String listingName, double newPrice);
}