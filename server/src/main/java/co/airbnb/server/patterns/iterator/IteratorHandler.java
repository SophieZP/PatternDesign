package co.airbnb.server.patterns.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorHandler {

    public String demo() {
        ListingCollection collection = new ListingCollection();
        collection.add(new Listing("L1"));
        collection.add(new Listing("L2"));
        collection.add(new Listing("L3"));
        StringBuilder builder = new StringBuilder();
        for (Iterator<Listing> iterator = collection.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next().name).append(" ");
        }
        return builder.toString().trim();
    }

    public static final class Listing {
        private final String name;

        public Listing(String name) {
            this.name = name;
        }
    }

    public static final class ListingCollection implements Iterable<Listing> {
        private final List<Listing> listings = new ArrayList<Listing>();

        public void add(Listing listing) {
            listings.add(listing);
        }

        @Override
        public Iterator<Listing> iterator() {
            return new ListingIterator(listings);
        }
    }

    public static final class ListingIterator implements Iterator<Listing> {
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
}