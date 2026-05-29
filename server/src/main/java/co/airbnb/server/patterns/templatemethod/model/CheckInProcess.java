package co.airbnb.server.patterns.templatemethod.model;

public abstract class CheckInProcess {

    public final String run() {
        return verifyIdentity() + " -> " + assignRoom() + " -> " + deliverKeys();
    }

    protected abstract String verifyIdentity();

    protected abstract String assignRoom();

    protected abstract String deliverKeys();
}