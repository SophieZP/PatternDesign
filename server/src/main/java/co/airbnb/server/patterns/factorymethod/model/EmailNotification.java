package co.airbnb.server.patterns.factorymethod.model;

public class EmailNotification extends Notification {

    public EmailNotification(String message) {
        super(message);
    }

    @Override
    public String send() {
        return "Email enviado: " + message;
    }
}