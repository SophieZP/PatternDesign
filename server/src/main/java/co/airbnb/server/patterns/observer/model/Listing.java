package co.airbnb.server.patterns.observer.model;

import java.util.ArrayList;
import java.util.List;

public class Listing {

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
            notifications.add(observer.update(name, this.price));
        }
    }

    public String notifications() {
        return notifications.toString();
    }
}