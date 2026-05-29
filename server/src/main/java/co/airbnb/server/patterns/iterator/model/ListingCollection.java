package co.airbnb.server.patterns.iterator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListingCollection implements Iterable<Listing> {

    private final List<Listing> listings = new ArrayList<Listing>();

    public void add(Listing listing) {
        listings.add(listing);
    }

    @Override
    public Iterator<Listing> iterator() {
        return new ListingIterator(listings);
    }
}