package co.airbnb.server.patterns.proxy.model;

public class UserProfileProxy extends UserProfile {

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