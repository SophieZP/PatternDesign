package co.airbnb.server.patterns.prototype.model;

public class ListingPrototype implements Cloneable {

    private String title;
    private String room;
    private double price;

    public ListingPrototype(String title, String room, double price) {
        this.title = title;
        this.room = room;
        this.price = price;
    }

    public ListingPrototype copy() {
        try {
            return (ListingPrototype) clone();
        } catch (CloneNotSupportedException exception) {
            return new ListingPrototype(title, room, price);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String summary() {
        return title + " | " + room + " | " + price;
    }
}