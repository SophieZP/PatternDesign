package co.airbnb.server.patterns.interpreter.model;

import java.util.ArrayList;
import java.util.List;

public class CityExpression extends SearchExpression {

    private final String city;

    public CityExpression(String city) {
        this.city = city;
    }

    @Override
    public List<String> interpret(List<String> listings) {
        List<String> result = new ArrayList<String>();
        for (String listing : listings) {
            if (listing.toLowerCase().contains(city.toLowerCase())) {
                result.add(listing);
            }
        }
        return result;
    }
}