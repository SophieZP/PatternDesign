package co.airbnb.server.patterns.proxy;

public class ProxyHandler {

    public String demo() {
        UserProfile guestView = new UserProfileProxy(new RealUserProfile("Ana", "ana@mail.com", "555-123"), "guest");
        UserProfile adminView = new UserProfileProxy(new RealUserProfile("Ana", "ana@mail.com", "555-123"), "admin");
        return guestView.display() + " | " + adminView.display();
    }

    public abstract static class UserProfile {
        public abstract String display();
    }

    public static final class RealUserProfile extends UserProfile {
        private final String name;
        private final String email;
        private final String phone;

        public RealUserProfile(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        @Override
        public String display() {
            return name + " | " + email + " | " + phone;
        }
    }

    public static final class UserProfileProxy extends UserProfile {
        private final UserProfile realProfile;
        private final String role;

        public UserProfileProxy(UserProfile realProfile, String role) {
            this.realProfile = realProfile;
            this.role = role;
        }

        @Override
        public String display() {
            if (!"admin".equalsIgnoreCase(role)) {
                return "Acceso restringido: " + role;
            }
            return realProfile.display();
        }
    }
}