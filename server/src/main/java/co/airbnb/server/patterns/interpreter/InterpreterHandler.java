package co.airbnb.server.patterns.interpreter;

import java.util.ArrayList;
import java.util.List;

public class InterpreterHandler {

    public String demo() {
        List<Listing> listings = new ArrayList<Listing>();
        listings.add(new Listing("Barcelona", 90));
        listings.add(new Listing("Barcelona", 140));
        listings.add(new Listing("Madrid", 85));

        SearchExpression expression = parse("ciudad:Barcelona AND precio:<100");
        return expression.interpret(listings).toString();
    }

    private SearchExpression parse(String query) {
        String[] parts = query.split(" AND ");
        SearchExpression left = parsePart(parts[0]);
        SearchExpression right = parsePart(parts[1]);
        return new AndExpression(left, right);
    }

    private SearchExpression parsePart(String part) {
        if (part.startsWith("ciudad:")) {
            return new CityExpression(part.substring("ciudad:".length()));
        }
        if (part.startsWith("precio:<")) {
            return new PriceExpression("<", Double.parseDouble(part.substring("precio:<".length())));
        }
        if (part.startsWith("precio:>")) {
            return new PriceExpression(">", Double.parseDouble(part.substring("precio:>".length())));
        }
        throw new IllegalArgumentException("Expresión no soportada: " + part);
    }

    public static final class Listing {
        private final String city;
        private final double price;

        public Listing(String city, double price) {
            this.city = city;
            this.price = price;
        }
    }

    public abstract static class SearchExpression {
        public abstract List<Listing> interpret(List<Listing> listings);
    }

    public static final class CityExpression extends SearchExpression {
        private final String city;

        public CityExpression(String city) {
            this.city = city;
        }

        @Override
        public List<Listing> interpret(List<Listing> listings) {
            List<Listing> result = new ArrayList<Listing>();
            for (Listing listing : listings) {
                if (listing.city.equalsIgnoreCase(city)) {
                    result.add(listing);
                }
            }
            return result;
        }
    }

    public static final class PriceExpression extends SearchExpression {
        private final String operator;
        private final double price;

        public PriceExpression(String operator, double price) {
            this.operator = operator;
            this.price = price;
        }

        @Override
        public List<Listing> interpret(List<Listing> listings) {
            List<Listing> result = new ArrayList<Listing>();
            for (Listing listing : listings) {
                boolean match = "<".equals(operator) ? listing.price < price : listing.price > price;
                if (match) {
                    result.add(listing);
                }
            }
            return result;
        }
    }

    public static final class AndExpression extends SearchExpression {
        private final SearchExpression left;
        private final SearchExpression right;

        public AndExpression(SearchExpression left, SearchExpression right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public List<Listing> interpret(List<Listing> listings) {
            List<Listing> leftResult = left.interpret(listings);
            return right.interpret(leftResult);
        }
    }
}