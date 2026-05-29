package co.airbnb.server.patterns.visitor.model;

public class Villa extends PropertyElement {

    public Villa(String name, double basePrice) {
        super(name, basePrice);
    }

    @Override
    public double accept(PropertyVisitor visitor) {
        return visitor.visitVilla(this);
    }
}