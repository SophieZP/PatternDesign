package co.airbnb.server.patterns.mediator.model;

public class User {

    private final String name;
    private final ChatRoomMediator mediator;

    public User(String name, ChatRoomMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public void send(String message) {
        mediator.broadcast(name, message);
    }
}