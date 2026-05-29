package co.airbnb.server.patterns.decorator.model;

public abstract class ListingDecorator extends Listing {

    protected final Listing listing;

    protected ListingDecorator(Listing listing) {
        super(listing.name, listing.price);
        this.listing = listing;
    }
}