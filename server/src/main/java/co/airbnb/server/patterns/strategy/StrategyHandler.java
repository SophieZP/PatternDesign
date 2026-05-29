package co.airbnb.server.patterns.strategy;

public class StrategyHandler {

    public String demo() {
        PricingContext context = new PricingContext(new SeasonalPricing());
        double seasonal = context.calculate(100, 3);
        context.setStrategy(new DurationPricing());
        double duration = context.calculate(100, 7);
        context.setStrategy(new PromoPricing());
        double promo = context.calculate(100, 2);
        return seasonal + " | " + duration + " | " + promo;
    }

    public interface PricingStrategy {
        double calculate(double basePrice, int nights);
    }

    public static final class SeasonalPricing implements PricingStrategy {
        @Override
        public double calculate(double basePrice, int nights) {
            return basePrice * nights * 1.2;
        }
    }

    public static final class DurationPricing implements PricingStrategy {
        @Override
        public double calculate(double basePrice, int nights) {
            return nights >= 7 ? basePrice * nights * 0.85 : basePrice * nights;
        }
    }

    public static final class PromoPricing implements PricingStrategy {
        @Override
        public double calculate(double basePrice, int nights) {
            return basePrice * nights - 30;
        }
    }

    public static final class PricingContext {
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
}