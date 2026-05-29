package co.airbnb.server.patterns.composite.model;

public class Room extends PropertyComponent {

    public Room(String name) {
        super(name);
    }

    @Override
    public String show() {
        return name;
    }
}