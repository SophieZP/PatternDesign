package co.airbnb.server.patterns.adapter.model;

public class PayPalAPI {

    public String charge(double amount) {
        return "PayPal cargó " + amount;
    }
}