package co.airbnb.server.patterns.memento.model;

public class BookingMemento {

    private final String checkIn;
    private final String checkOut;

    public BookingMemento(String checkIn, String checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }
}