package co.airbnb.server.patterns.memento.model;

import java.util.Stack;

public class BookingHistory {

    private final Stack<BookingMemento> states = new Stack<BookingMemento>();

    public void save(BookingMemento memento) {
        states.push(memento);
    }

    public BookingMemento undo() {
        states.pop();
        return states.peek();
    }
}