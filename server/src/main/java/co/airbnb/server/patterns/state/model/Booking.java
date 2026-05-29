package co.airbnb.server.patterns.state.model;

public class Booking {

    private BookingState state = new PendingState();

    public String pay() {
        return state.pay();
    }

    public String confirm() {
        state = new ConfirmedState();
        return state.status();
    }

    public String cancel() {
        return state.cancel();
    }
}