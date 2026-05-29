package co.airbnb.server.patterns.templatemethod.model;

public class LuxuryCheckInProcess extends CheckInProcess {

    @Override
    protected String verifyIdentity() {
        return "Verificación premium";
    }

    @Override
    protected String assignRoom() {
        return "Suite de lujo";
    }

    @Override
    protected String deliverKeys() {
        return "Recepción personal";
    }
}