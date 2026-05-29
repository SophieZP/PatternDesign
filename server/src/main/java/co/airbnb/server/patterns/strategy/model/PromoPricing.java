package co.airbnb.server.patterns.strategy.model;

public class PromoPricing implements PricingStrategy {

    @Override
    public double calculate(double basePrice, int nights) {
        return basePrice * nights - 30;
    }
}