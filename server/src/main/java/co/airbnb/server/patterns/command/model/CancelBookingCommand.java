package co.airbnb.server.patterns.command.model;

public class CancelBookingCommand extends BookingCommand {

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