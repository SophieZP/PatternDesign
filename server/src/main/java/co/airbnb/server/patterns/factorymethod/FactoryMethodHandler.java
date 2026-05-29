package co.airbnb.server.patterns.factorymethod;

public class FactoryMethodHandler {

    public String demo() {
        NotificationFactory factory = new NotificationFactory();
        String email = factory.create("email", "Reserva confirmada").send();
        String push = factory.create("push", "Nuevo mensaje del anfitrión").send();
        String sms = factory.create("sms", "Pago recibido").send();
        return email + " | " + push + " | " + sms;
    }

    public abstract static class Notification {
        protected final String message;

        protected Notification(String message) {
            this.message = message;
        }

        public abstract String send();
    }

    public static final class EmailNotification extends Notification {
        public EmailNotification(String message) {
            super(message);
        }

        @Override
        public String send() {
            return "Email enviado: " + message;
        }
    }

    public static final class PushNotification extends Notification {
        public PushNotification(String message) {
            super(message);
        }

        @Override
        public String send() {
            return "Push enviado: " + message;
        }
    }

    public static final class SmsNotification extends Notification {
        public SmsNotification(String message) {
            super(message);
        }

        @Override
        public String send() {
            return "SMS enviado: " + message;
        }
    }

    public static final class NotificationFactory {
        public Notification create(String type, String message) {
            String normalized = type == null ? "" : type.trim().toLowerCase();
            if ("email".equals(normalized)) {
                return new EmailNotification(message);
            }
            if ("push".equals(normalized)) {
                return new PushNotification(message);
            }
            if ("sms".equals(normalized)) {
                return new SmsNotification(message);
            }
            throw new IllegalArgumentException("Tipo de notificación no soportado: " + type);
        }
    }
}