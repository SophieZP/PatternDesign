package co.airbnb.server.patterns.bridge.model;

public class LocationFilter extends SearchFilter {

    public LocationFilter(FilterImplementor implementor) {
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
        return "Ubicación: " + count + " coincidencias";
    }
}