package co.airbnb.server.patterns.facade.model;

public class NotificationService {

    public String notifyUser(String status) {
        return "Notificación enviada: " + status;
    }
}