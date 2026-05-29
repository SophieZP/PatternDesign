package co.airbnb.server.patterns.abstractfactory.model;

public abstract class UIFactory {

    public abstract Button createButton();

    public abstract Card createCard();
}