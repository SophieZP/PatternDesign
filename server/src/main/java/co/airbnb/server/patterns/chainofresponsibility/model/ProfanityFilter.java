package co.airbnb.server.patterns.chainofresponsibility.model;

public class ProfanityFilter extends ReviewHandler {

    public ProfanityFilter(ReviewHandler next) {
        super(next);
    }

    @Override
    protected boolean isAllowed(String review) {
        return review != null && review.toLowerCase().indexOf("groseria") < 0;
    }

    @Override
    protected String getName() {
        return "ProfanityFilter";
    }
}