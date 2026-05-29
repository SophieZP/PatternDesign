package co.airbnb.server.patterns.abstractfactory.model;

public class Card extends UIComponent {

    public Card(String style) {
        super(style);
    }

    @Override
    public String render() {
        return "Tarjeta " + getStyle();
    }
}