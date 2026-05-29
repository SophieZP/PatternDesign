package co.airbnb.server.patterns.abstractfactory.model;

public class MobileUIFactory extends UIFactory {

    @Override
    public Button createButton() {
        return new Button("móvil");
    }

    @Override
    public Card createCard() {
        return new Card("móvil");
    }
}