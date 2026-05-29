package co.airbnb.server.patterns.visitor;

public class VisitorHandler {

    public String demo() {
        RevenueVisitor visitor = new RevenueVisitor();
        PropertyElement apartment = new Apartment("Apartamento", 120);
        PropertyElement villa = new Villa("Villa", 300);
        return apartment.accept(visitor) + " | " + villa.accept(visitor);
    }

    public interface PropertyVisitor {
        double visitApartment(Apartment apartment);

        double visitVilla(Villa villa);
    }

    public abstract static class PropertyElement {
        protected final String name;
        protected final double basePrice;

        protected PropertyElement(String name, double basePrice) {
            this.name = name;
            this.basePrice = basePrice;
        }

        public abstract double accept(PropertyVisitor visitor);
    }

    public static final class Apartment extends PropertyElement {
        public Apartment(String name, double basePrice) {
            super(name, basePrice);
        }

        @Override
        public double accept(PropertyVisitor visitor) {
            return visitor.visitApartment(this);
        }
    }

    public static final class Villa extends PropertyElement {
        public Villa(String name, double basePrice) {
            super(name, basePrice);
        }

        @Override
        public double accept(PropertyVisitor visitor) {
            return visitor.visitVilla(this);
        }
    }

    public static final class RevenueVisitor implements PropertyVisitor {
        @Override
        public double visitApartment(Apartment apartment) {
            return apartment.basePrice * 0.9;
        }

        @Override
        public double visitVilla(Villa villa) {
            return villa.basePrice * 0.8;
        }
    }
}