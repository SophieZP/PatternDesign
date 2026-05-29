package co.airbnb.server.patterns.factorymethod.model;

public class SmsNotification extends Notification {

    public SmsNotification(String message) {
        super(message);
    }

    @Override
    public String send() {
        return "SMS enviado: " + message;
    }
}