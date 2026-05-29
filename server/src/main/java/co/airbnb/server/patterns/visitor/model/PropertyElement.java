package co.airbnb.server.patterns.visitor.model;

public abstract class PropertyElement {

    protected final String name;
    protected final double basePrice;

    protected PropertyElement(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public abstract double accept(PropertyVisitor visitor);
}