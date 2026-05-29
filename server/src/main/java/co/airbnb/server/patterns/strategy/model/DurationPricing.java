package co.airbnb.server.patterns.strategy.model;

public class DurationPricing implements PricingStrategy {

    @Override
    public double calculate(double basePrice, int nights) {
        return nights >= 7 ? basePrice * nights * 0.85 : basePrice * nights;
    }
}