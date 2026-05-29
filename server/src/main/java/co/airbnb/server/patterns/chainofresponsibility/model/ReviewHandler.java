package co.airbnb.server.patterns.chainofresponsibility.model;

public abstract class ReviewHandler {

    protected final ReviewHandler next;

    protected ReviewHandler(ReviewHandler next) {
        this.next = next;
    }

    public String handle(String review) {
        if (!isAllowed(review)) {
            return getName() + ": rechazado";
        }
        if (next != null) {
            return getName() + ": aprobado -> " + next.handle(review);
        }
        return getName() + ": aprobado";
    }

    protected abstract boolean isAllowed(String review);

    protected abstract String getName();
}