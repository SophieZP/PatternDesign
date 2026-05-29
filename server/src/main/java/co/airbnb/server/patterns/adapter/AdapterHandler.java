package co.airbnb.server.patterns.adapter;

public class AdapterHandler {

    public String demo() {
        PaymentGateway stripe = new PaymentAdapter(new StripeAPI());
        PaymentGateway paypal = new PaymentAdapter(new PayPalAPI());
        return stripe.pay(150.0) + " | " + paypal.pay(80.0);
    }

    public abstract static class PaymentGateway {
        public abstract String pay(double amount);
    }

    public interface LegacyPaymentAPI {
        String charge(double amount);
    }

    public static final class StripeAPI implements LegacyPaymentAPI {
        @Override
        public String charge(double amount) {
            return "Stripe cargó " + amount;
        }
    }

    public static final class PayPalAPI implements LegacyPaymentAPI {
        @Override
        public String charge(double amount) {
            return "PayPal cargó " + amount;
        }
    }

    public static final class PaymentAdapter extends PaymentGateway {
        private final LegacyPaymentAPI api;

        public PaymentAdapter(LegacyPaymentAPI api) {
            this.api = api;
        }

        @Override
        public String pay(double amount) {
            return api.charge(amount);
        }
    }
}