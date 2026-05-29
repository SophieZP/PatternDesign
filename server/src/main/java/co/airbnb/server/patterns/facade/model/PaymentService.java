package co.airbnb.server.patterns.facade.model;

public class PaymentService {

    public String pay(double amount) {
        return "Pago procesado: " + amount;
    }
}