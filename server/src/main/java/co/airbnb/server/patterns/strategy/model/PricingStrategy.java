package co.airbnb.server.patterns.strategy.model;

public interface PricingStrategy {
    double calculate(double basePrice, int nights);
}