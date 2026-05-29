package co.airbnb.server.patterns.command.model;

import java.util.ArrayList;
import java.util.List;

public class BookingInvoker {

    private final List<String> history = new ArrayList<String>();

    public void execute(BookingCommand command) {
        history.add(command.execute());
    }

    public String history() {
        return history.toString();
    }
}