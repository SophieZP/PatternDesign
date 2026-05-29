package co.airbnb.server.patterns.facade.model;

public class ReservationService {

    public String createReservation(String reservationId) {
        return "Reserva creada: " + reservationId;
    }
}