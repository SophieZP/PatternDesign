package co.airbnb.server.patterns.visitor.model;

public class Apartment extends PropertyElement {

    public Apartment(String name, double basePrice) {
        super(name, basePrice);
    }

    @Override
    public double accept(PropertyVisitor visitor) {
        return visitor.visitApartment(this);
    }
}