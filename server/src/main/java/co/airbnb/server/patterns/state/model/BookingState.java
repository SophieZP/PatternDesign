package co.airbnb.server.patterns.state.model;

public abstract class BookingState {

    public abstract String pay();

    public abstract String cancel();

    public abstract String status();
}