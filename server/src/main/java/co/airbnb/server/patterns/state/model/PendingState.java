package co.airbnb.server.patterns.state.model;

public class PendingState extends BookingState {

    @Override
    public String pay() {
        return "Pago permitido en estado pendiente";
    }

    @Override
    public String cancel() {
        return "Reserva cancelada";
    }

    @Override
    public String status() {
        return "Pendiente";
    }
}