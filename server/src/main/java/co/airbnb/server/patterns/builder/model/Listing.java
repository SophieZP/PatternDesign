package co.airbnb.server.patterns.builder.model;

import java.util.ArrayList;
import java.util.List;

public class Listing {

    private final String title;
    private final String description;
    private final double pricePerNight;
    private final int capacity;
    private final List<String> amenities;
    private final List<String> houseRules;
    private final String cancellationPolicy;

    public Listing(String title, String description, double pricePerNight, int capacity, List<String> amenities, List<String> houseRules, String cancellationPolicy) {
        this.title = title;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.amenities = new ArrayList<String>(amenities);
        this.houseRules = new ArrayList<String>(houseRules);
        this.cancellationPolicy = cancellationPolicy;
    }

    public String summary() {
        return title + " | " + description + " | " + pricePerNight + " | " + capacity + " | " + amenities + " | " + houseRules + " | " + cancellationPolicy;
    }
}