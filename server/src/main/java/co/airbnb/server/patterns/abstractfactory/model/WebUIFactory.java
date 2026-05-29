package co.airbnb.server.patterns.abstractfactory.model;

public class WebUIFactory extends UIFactory {

    @Override
    public Button createButton() {
        return new Button("web");
    }

    @Override
    public Card createCard() {
        return new Card("web");
    }
}