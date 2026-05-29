package co.airbnb.server.patterns.builder.model;

import java.util.ArrayList;
import java.util.List;

public class ListingBuilder {

    private String title;
    private String description;
    private double pricePerNight;
    private int capacity;
    private final List<String> amenities = new ArrayList<String>();
    private final List<String> houseRules = new ArrayList<String>();
    private String cancellationPolicy;

    public ListingBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ListingBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ListingBuilder pricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
        return this;
    }

    public ListingBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public ListingBuilder addAmenity(String amenity) {
        amenities.add(amenity);
        return this;
    }

    public ListingBuilder addHouseRule(String rule) {
        houseRules.add(rule);
        return this;
    }

    public ListingBuilder cancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
        return this;
    }

    public Listing build() {
        return new Listing(title, description, pricePerNight, capacity, amenities, houseRules, cancellationPolicy);
    }
}