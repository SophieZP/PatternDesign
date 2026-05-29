package co.airbnb.server.patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class ObserverHandler {

    public String demo() {
        Listing listing = new Listing("Apartamento Vista Mar", 200);
        listing.addObserver(new GuestObserver("Ana"));
        listing.addObserver(new GuestObserver("Luis"));
        listing.setPrice(180);
        return listing.notifications();
    }

    public interface ListingObserver {
        String update(String listingName, double newPrice);
    }

    public static final class GuestObserver implements ListingObserver {
        private final String name;

        public GuestObserver(String name) {
            this.name = name;
        }

        @Override
        public String update(String listingName, double newPrice) {
            return name + " notificado: " + listingName + " -> " + newPrice;
        }
    }

    public static final class Listing {
        private final String name;
        private double price;
        private final List<ListingObserver> observers = new ArrayList<ListingObserver>();
        private final List<String> notifications = new ArrayList<String>();

        public Listing(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public void addObserver(ListingObserver observer) {
            observers.add(observer);
        }

        public void setPrice(double price) {
            this.price = price;
            for (ListingObserver observer : observers) {
                notifications.add(observer.update(name, price));
            }
        }

        public String notifications() {
            return notifications.toString();
        }
    }
}