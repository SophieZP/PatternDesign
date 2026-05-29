package co.airbnb.server.patterns.command.model;

public abstract class BookingCommand {

    protected final String reservationId;

    protected BookingCommand(String reservationId) {
        this.reservationId = reservationId;
    }

    public abstract String execute();

    public abstract String undo();
}