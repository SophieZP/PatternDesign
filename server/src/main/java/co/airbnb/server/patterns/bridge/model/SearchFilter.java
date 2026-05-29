package co.airbnb.server.patterns.bridge.model;

public abstract class SearchFilter {

    protected final FilterImplementor implementor;

    protected SearchFilter(FilterImplementor implementor) {
        this.implementor = implementor;
    }

    public abstract String apply(String[] candidates, String criteria);
}