package co.airbnb.server.patterns.factorymethod.model;

public class NotificationFactory {

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