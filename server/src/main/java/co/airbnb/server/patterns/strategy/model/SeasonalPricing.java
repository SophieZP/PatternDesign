package co.airbnb.server.patterns.strategy.model;

public class SeasonalPricing implements PricingStrategy {

    @Override
    public double calculate(double basePrice, int nights) {
        return basePrice * nights * 1.2;
    }
}