package co.airbnb.server.patterns.mediator.model;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomMediator {

    private final List<String> messages = new ArrayList<String>();

    public void broadcast(String sender, String message) {
        messages.add(sender + ": " + message);
    }

    public String history() {
        return messages.toString();
    }
}