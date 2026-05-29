package co.airbnb.server.patterns.factorymethod.model;

public class PushNotification extends Notification {

    public PushNotification(String message) {
        super(message);
    }

    @Override
    public String send() {
        return "Push enviado: " + message;
    }
}