package co.airbnb.server.patterns.command;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    public String demo() {
        BookingInvoker invoker = new BookingInvoker();
        invoker.execute(new CreateBookingCommand("R-1"));
        invoker.execute(new CancelBookingCommand("R-1"));
        return invoker.history();
    }

    public abstract static class BookingCommand {
        protected final String reservationId;

        protected BookingCommand(String reservationId) {
            this.reservationId = reservationId;
        }

        public abstract String execute();

        public abstract String undo();
    }

    public static final class CreateBookingCommand extends BookingCommand {
        public CreateBookingCommand(String reservationId) {
            super(reservationId);
        }

        @Override
        public String execute() {
            return "Reserva creada: " + reservationId;
        }

        @Override
        public String undo() {
            return "Crear reserva deshecho: " + reservationId;
        }
    }

    public static final class CancelBookingCommand extends BookingCommand {
        public CancelBookingCommand(String reservationId) {
            super(reservationId);
        }

        @Override
        public String execute() {
            return "Reserva cancelada: " + reservationId;
        }

        @Override
        public String undo() {
            return "Cancelar reserva deshecho: " + reservationId;
        }
    }

    public static final class BookingInvoker {
        private final List<String> history = new ArrayList<String>();

        public void execute(BookingCommand command) {
            history.add(command.execute());
        }

        public String history() {
            return history.toString();
        }
    }
}