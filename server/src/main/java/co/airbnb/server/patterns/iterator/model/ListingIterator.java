package co.airbnb.server.patterns.iterator.model;

import java.util.Iterator;
import java.util.List;

public class ListingIterator implements Iterator<Listing> {

    private final List<Listing> listings;
    private int index;

    public ListingIterator(List<Listing> listings) {
        this.listings = listings;
    }

    @Override
    public boolean hasNext() {
        return index < listings.size();
    }

    @Override
    public Listing next() {
        return listings.get(index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}