package co.airbnb.server.patterns.facade.model;

public class BookingFacade {

    private final PaymentService paymentService = new PaymentService();
    private final ReservationService reservationService = new ReservationService();
    private final NotificationService notificationService = new NotificationService();

    public String book(String reservationId, double amount, String status) {
        return reservationService.createReservation(reservationId) + " | "
                + paymentService.pay(amount) + " | "
                + notificationService.notifyUser(status);
    }
}