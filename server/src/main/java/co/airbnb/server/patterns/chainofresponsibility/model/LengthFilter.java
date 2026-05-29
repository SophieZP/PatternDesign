package co.airbnb.server.patterns.chainofresponsibility.model;

public class LengthFilter extends ReviewHandler {

    public LengthFilter(ReviewHandler next) {
        super(next);
    }

    @Override
    protected boolean isAllowed(String review) {
        return review != null && review.length() >= 10;
    }

    @Override
    protected String getName() {
        return "LengthFilter";
    }
}