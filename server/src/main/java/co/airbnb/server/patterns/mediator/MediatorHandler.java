package co.airbnb.server.patterns.mediator;

import java.util.ArrayList;
import java.util.List;

public class MediatorHandler {

    public String demo() {
        ChatRoomMediator mediator = new ChatRoomMediator();
        User host = new User("Host", mediator);
        User guest = new User("Guest", mediator);
        mediator.register(host);
        mediator.register(guest);
        host.send("Bienvenido");
        guest.send("Gracias");
        return mediator.history();
    }

    public static final class ChatRoomMediator {
        private final List<String> messages = new ArrayList<String>();
        private final List<User> users = new ArrayList<User>();

        public void register(User user) {
            users.add(user);
        }

        public void broadcast(String sender, String message) {
            messages.add(sender + ": " + message);
        }

        public String history() {
            return messages.toString();
        }
    }

    public static final class User {
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
}