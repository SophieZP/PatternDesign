package co.airbnb.server.patterns.adapter.model;

public interface PaymentGateway {
    String pay(double amount);
}