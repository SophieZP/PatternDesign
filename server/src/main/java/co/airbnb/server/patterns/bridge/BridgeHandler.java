package co.airbnb.server.patterns.bridge;

import java.util.ArrayList;
import java.util.List;

public class BridgeHandler {

    public String demo() {
        List<Listing> listings = new ArrayList<Listing>();
        listings.add(new Listing("Barcelona", 90));
        listings.add(new Listing("Madrid", 150));
        listings.add(new Listing("Barcelona", 130));

        SearchFilter priceFilter = new PriceFilter(new RangeMatchImplementor());
        SearchFilter locationFilter = new LocationFilter(new ExactMatchImplementor());

        return priceFilter.apply(listings, "80-120") + " || " + locationFilter.apply(listings, "Barcelona");
    }

    public static final class Listing {
        private final String city;
        private final double price;

        public Listing(String city, double price) {
            this.city = city;
            this.price = price;
        }
    }

    public abstract static class FilterImplementor {
        public abstract boolean matches(Listing listing, String criteria);
    }

    public static final class ExactMatchImplementor extends FilterImplementor {
        @Override
        public boolean matches(Listing listing, String criteria) {
            return listing.city.equalsIgnoreCase(criteria);
        }
    }

    public static final class RangeMatchImplementor extends FilterImplementor {
        @Override
        public boolean matches(Listing listing, String criteria) {
            String[] parts = criteria.split("-");
            double min = Double.parseDouble(parts[0]);
            double max = Double.parseDouble(parts[1]);
            return listing.price >= min && listing.price <= max;
        }
    }

    public abstract static class SearchFilter {
        protected final FilterImplementor implementor;

        protected SearchFilter(FilterImplementor implementor) {
            this.implementor = implementor;
        }

        public abstract String apply(List<Listing> listings, String criteria);
    }

    public static final class PriceFilter extends SearchFilter {
        public PriceFilter(FilterImplementor implementor) {
            super(implementor);
        }

        @Override
        public String apply(List<Listing> listings, String criteria) {
            int count = 0;
            for (Listing listing : listings) {
                if (implementor.matches(listing, criteria)) {
                    count++;
                }
            }
            return "Precio: " + count + " coincidencias";
        }
    }

    public static final class LocationFilter extends SearchFilter {
        public LocationFilter(FilterImplementor implementor) {
            super(implementor);
        }

        @Override
        public String apply(List<Listing> listings, String criteria) {
            int count = 0;
            for (Listing listing : listings) {
                if (implementor.matches(listing, criteria)) {
                    count++;
                }
            }
            return "Ubicación: " + count + " coincidencias";
        }
    }
}