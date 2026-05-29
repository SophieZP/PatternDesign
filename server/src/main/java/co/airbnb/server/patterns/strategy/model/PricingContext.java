package co.airbnb.server.patterns.strategy.model;

public class PricingContext {

    private PricingStrategy strategy;

    public PricingContext(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculate(double basePrice, int nights) {
        return strategy.calculate(basePrice, nights);
    }
}