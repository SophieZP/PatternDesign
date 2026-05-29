package co.airbnb.server.patterns.interpreter.model;

import java.util.ArrayList;
import java.util.List;

public class PriceExpression extends SearchExpression {

    private final String operator;
    private final double price;

    public PriceExpression(String operator, double price) {
        this.operator = operator;
        this.price = price;
    }

    @Override
    public List<String> interpret(List<String> listings) {
        List<String> result = new ArrayList<String>();
        for (String listing : listings) {
            boolean match = "<".equals(operator) ? listing.length() < price : listing.length() > price;
            if (match) {
                result.add(listing);
            }
        }
        return result;
    }
}