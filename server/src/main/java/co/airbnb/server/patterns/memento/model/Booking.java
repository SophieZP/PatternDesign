package co.airbnb.server.patterns.memento.model;

public class Booking {

    private String checkIn;
    private String checkOut;

    public Booking(String checkIn, String checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public BookingMemento save() {
        return new BookingMemento(checkIn, checkOut);
    }

    public void restore(BookingMemento memento) {
        this.checkIn = memento.getCheckIn();
        this.checkOut = memento.getCheckOut();
    }

    public String summary() {
        return checkIn + " -> " + checkOut;
    }
}