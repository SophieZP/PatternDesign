package co.airbnb.server.patterns.visitor.model;

public interface PropertyVisitor {

    double visitApartment(Apartment apartment);

    double visitVilla(Villa villa);
}