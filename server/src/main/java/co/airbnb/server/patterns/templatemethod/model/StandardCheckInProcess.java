package co.airbnb.server.patterns.templatemethod.model;

public class StandardCheckInProcess extends CheckInProcess {

    @Override
    protected String verifyIdentity() {
        return "Verificación estándar";
    }

    @Override
    protected String assignRoom() {
        return "Habitación estándar";
    }

    @Override
    protected String deliverKeys() {
        return "Llaves digitales";
    }
}