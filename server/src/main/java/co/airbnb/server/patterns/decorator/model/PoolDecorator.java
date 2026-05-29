package co.airbnb.server.patterns.decorator.model;

public class PoolDecorator extends ListingDecorator {

    public PoolDecorator(Listing listing) {
        super(listing);
    }

    @Override
    public String description() {
        return listing.description() + " + Piscina";
    }
}