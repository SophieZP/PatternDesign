package co.airbnb.server.patterns.memento;

import java.util.Stack;

public class MementoHandler {

    public String demo() {
        Booking booking = new Booking("2026-06-01", "2026-06-05");
        BookingHistory history = new BookingHistory();
        history.save(booking.save());
        booking.setCheckIn("2026-06-02");
        history.save(booking.save());
        booking.restore(history.undo());
        return booking.summary();
    }

    public static final class Booking {
        private String checkIn;
        private String checkOut;

        public Booking(String checkIn, String checkOut) {
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }

        public void setCheckIn(String checkIn) {
            this.checkIn = checkIn;
        }

        public BookingMemento save() {
            return new BookingMemento(checkIn, checkOut);
        }

        public void restore(BookingMemento memento) {
            this.checkIn = memento.checkIn;
            this.checkOut = memento.checkOut;
        }

        public String summary() {
            return checkIn + " -> " + checkOut;
        }
    }

    public static final class BookingMemento {
        private final String checkIn;
        private final String checkOut;

        private BookingMemento(String checkIn, String checkOut) {
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }
    }

    public static final class BookingHistory {
        private final Stack<BookingMemento> states = new Stack<BookingMemento>();

        public void save(BookingMemento memento) {
            states.push(memento);
        }

        public BookingMemento undo() {
            states.pop();
            return states.peek();
        }
    }
}