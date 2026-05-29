package co.airbnb.server.patterns.proxy.model;

public class RealUserProfile extends UserProfile {

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