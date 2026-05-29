package co.airbnb.server.patterns.chainofresponsibility.model;

public class SpamFilter extends ReviewHandler {

    public SpamFilter(ReviewHandler next) {
        super(next);
    }

    @Override
    protected boolean isAllowed(String review) {
        return review != null && review.toLowerCase().indexOf("spam") < 0;
    }

    @Override
    protected String getName() {
        return "SpamFilter";
    }
}