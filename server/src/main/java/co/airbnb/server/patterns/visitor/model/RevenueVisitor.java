package co.airbnb.server.patterns.visitor.model;

public class RevenueVisitor implements PropertyVisitor {

    @Override
    public double visitApartment(Apartment apartment) {
        return apartment.basePrice * 0.9;
    }

    @Override
    public double visitVilla(Villa villa) {
        return villa.basePrice * 0.8;
    }
}