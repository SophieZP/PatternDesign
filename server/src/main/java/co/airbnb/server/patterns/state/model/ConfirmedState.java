package co.airbnb.server.patterns.state.model;

public class ConfirmedState extends BookingState {

    @Override
    public String pay() {
        return "Pago ya realizado";
    }

    @Override
    public String cancel() {
        return "Cancelación permitida en confirmada";
    }

    @Override
    public String status() {
        return "Confirmada";
    }
}