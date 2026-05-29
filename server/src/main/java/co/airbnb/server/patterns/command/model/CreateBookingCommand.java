package co.airbnb.server.patterns.command.model;

public class CreateBookingCommand extends BookingCommand {

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