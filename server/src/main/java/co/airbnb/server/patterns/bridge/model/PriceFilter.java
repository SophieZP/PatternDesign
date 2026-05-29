package co.airbnb.server.patterns.bridge.model;

public class PriceFilter extends SearchFilter {

    public PriceFilter(FilterImplementor implementor) {
        super(implementor);
    }

    @Override
    public String apply(String[] candidates, String criteria) {
        int count = 0;
        for (String candidate : candidates) {
            if (implementor.matches(candidate, criteria)) {
                count++;
            }
        }
        return "Precio: " + count + " coincidencias";
    }
}