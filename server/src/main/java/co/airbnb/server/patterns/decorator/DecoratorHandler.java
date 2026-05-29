package co.airbnb.server.patterns.decorator;

public class DecoratorHandler {

    public String demo() {
        Listing listing = new BaseListing("Apartamento base", 100);
        Listing wifi = new WifiDecorator(listing);
        Listing pool = new PoolDecorator(wifi);
        return pool.description();
    }

    public abstract static class Listing {
        protected final String name;
        protected final double price;

        protected Listing(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public abstract String description();
    }

    public static final class BaseListing extends Listing {
        public BaseListing(String name, double price) {
            super(name, price);
        }

        @Override
        public String description() {
            return name + " | " + price;
        }
    }

    public abstract static class ListingDecorator extends Listing {
        protected final Listing listing;

        protected ListingDecorator(Listing listing) {
            super(listing.name, listing.price);
            this.listing = listing;
        }
    }

    public static final class WifiDecorator extends ListingDecorator {
        public WifiDecorator(Listing listing) {
            super(listing);
        }

        @Override
        public String description() {
            return listing.description() + " + Wi-Fi";
        }
    }

    public static final class PoolDecorator extends ListingDecorator {
        public PoolDecorator(Listing listing) {
            super(listing);
        }

        @Override
        public String description() {
            return listing.description() + " + Piscina";
        }
    }
}