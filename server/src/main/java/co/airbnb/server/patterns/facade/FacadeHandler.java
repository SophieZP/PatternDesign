package co.airbnb.server.patterns.facade;

public class FacadeHandler {

    public String demo() {
        BookingFacade facade = new BookingFacade();
        return facade.book("Reserva 101", 240.0, "confirmada");
    }

    public static final class BookingFacade {
        private final PaymentService paymentService = new PaymentService();
        private final ReservationService reservationService = new ReservationService();
        private final NotificationService notificationService = new NotificationService();

        public String book(String reservationId, double amount, String status) {
            String reservation = reservationService.createReservation(reservationId);
            String payment = paymentService.pay(amount);
            String notification = notificationService.notifyUser(status);
            return reservation + " | " + payment + " | " + notification;
        }
    }

    public static final class PaymentService {
        public String pay(double amount) {
            return "Pago procesado: " + amount;
        }
    }

    public static final class ReservationService {
        public String createReservation(String reservationId) {
            return "Reserva creada: " + reservationId;
        }
    }

    public static final class NotificationService {
        public String notifyUser(String status) {
            return "Notificación enviada: " + status;
        }
    }
}