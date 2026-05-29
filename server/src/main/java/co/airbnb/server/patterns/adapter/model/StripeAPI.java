package co.airbnb.server.patterns.adapter.model;

public class StripeAPI {

    public String charge(double amount) {
        return "Stripe cargó " + amount;
    }
}