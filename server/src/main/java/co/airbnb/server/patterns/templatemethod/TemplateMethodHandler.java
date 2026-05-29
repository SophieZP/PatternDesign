package co.airbnb.server.patterns.templatemethod;

public class TemplateMethodHandler {

    public String demo() {
        CheckInProcess standard = new StandardCheckInProcess();
        CheckInProcess luxury = new LuxuryCheckInProcess();
        return standard.run() + " | " + luxury.run();
    }

    public abstract static class CheckInProcess {
        public final String run() {
            return verifyIdentity() + " -> " + assignRoom() + " -> " + deliverKeys();
        }

        protected abstract String verifyIdentity();

        protected abstract String assignRoom();

        protected abstract String deliverKeys();
    }

    public static final class StandardCheckInProcess extends CheckInProcess {
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

    public static final class LuxuryCheckInProcess extends CheckInProcess {
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
}