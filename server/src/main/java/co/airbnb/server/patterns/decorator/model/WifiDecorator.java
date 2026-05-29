package co.airbnb.server.patterns.decorator.model;

public class WifiDecorator extends ListingDecorator {

    public WifiDecorator(Listing listing) {
        super(listing);
    }

    @Override
    public String description() {
        return listing.description() + " + Wi-Fi";
    }
}