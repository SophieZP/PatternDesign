package co.airbnb.server.patterns.state;

public class StateHandler {

    public String demo() {
        Booking booking = new Booking();
        String pending = booking.pay();
        booking.confirm();
        String confirmed = booking.cancel();
        return pending + " | " + confirmed;
    }

    public static final class Booking {
        private BookingState state = new PendingState(this);

        public String pay() {
            return state.pay();
        }

        public String confirm() {
            state = new ConfirmedState(this);
            return state.status();
        }

        public String cancel() {
            return state.cancel();
        }
    }

    public abstract static class BookingState {
        protected final Booking booking;

        protected BookingState(Booking booking) {
            this.booking = booking;
        }

        public abstract String pay();

        public abstract String cancel();

        public abstract String status();
    }

    public static final class PendingState extends BookingState {
        public PendingState(Booking booking) {
            super(booking);
        }

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

    public static final class ConfirmedState extends BookingState {
        public ConfirmedState(Booking booking) {
            super(booking);
        }

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
}