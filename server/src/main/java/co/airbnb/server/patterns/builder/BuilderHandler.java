package co.airbnb.server.patterns.builder;

import java.util.ArrayList;
import java.util.List;

public class BuilderHandler {

    public String demo() {
        Listing listing = new Listing.Builder()
                .title("Apartamento en Barcelona")
                .description("Luminoso y cerca del metro")
                .pricePerNight(120)
                .capacity(4)
                .addAmenity("Wi-Fi")
                .addAmenity("Cocina")
                .houseRule("No fumar")
                .cancellationPolicy("Flexible")
                .build();
        return listing.toSummary();
    }

    public static final class Listing {
        private final String title;
        private final String description;
        private final double pricePerNight;
        private final int capacity;
        private final List<String> amenities;
        private final List<String> houseRules;
        private final String cancellationPolicy;

        private Listing(Builder builder) {
            this.title = builder.title;
            this.description = builder.description;
            this.pricePerNight = builder.pricePerNight;
            this.capacity = builder.capacity;
            this.amenities = new ArrayList<String>(builder.amenities);
            this.houseRules = new ArrayList<String>(builder.houseRules);
            this.cancellationPolicy = builder.cancellationPolicy;
        }

        public String toSummary() {
            return title + " | " + description + " | " + pricePerNight + " | " + capacity + " | " + amenities + " | " + houseRules + " | " + cancellationPolicy;
        }

        public static final class Builder {
            private String title;
            private String description;
            private double pricePerNight;
            private int capacity;
            private final List<String> amenities = new ArrayList<String>();
            private final List<String> houseRules = new ArrayList<String>();
            private String cancellationPolicy;

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder pricePerNight(double pricePerNight) {
                this.pricePerNight = pricePerNight;
                return this;
            }

            public Builder capacity(int capacity) {
                this.capacity = capacity;
                return this;
            }

            public Builder addAmenity(String amenity) {
                this.amenities.add(amenity);
                return this;
            }

            public Builder houseRule(String rule) {
                this.houseRules.add(rule);
                return this;
            }

            public Builder cancellationPolicy(String cancellationPolicy) {
                this.cancellationPolicy = cancellationPolicy;
                return this;
            }

            public Listing build() {
                return new Listing(this);
            }
        }
    }
}