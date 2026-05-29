package co.airbnb.server.patterns.adapter.model;

public class PaymentAdapter implements PaymentGateway {

    private final Object api;

    public PaymentAdapter(Object api) {
        this.api = api;
    }

    @Override
    public String pay(double amount) {
        if (api instanceof StripeAPI) {
            return ((StripeAPI) api).charge(amount);
        }
        if (api instanceof PayPalAPI) {
            return ((PayPalAPI) api).charge(amount);
        }
        return "Proveedor no soportado";
    }
}