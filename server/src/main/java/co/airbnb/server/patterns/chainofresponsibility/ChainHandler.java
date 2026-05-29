package co.airbnb.server.patterns.chainofresponsibility;

public class ChainHandler {

    public String demo() {
        ReviewHandler chain = new SpamFilter(new ProfanityFilter(new LengthFilter(null)));
        return chain.handle("Excelente lugar para quedarse");
    }

    public abstract static class ReviewHandler {
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

    public static final class SpamFilter extends ReviewHandler {
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

    public static final class ProfanityFilter extends ReviewHandler {
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

    public static final class LengthFilter extends ReviewHandler {
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
}