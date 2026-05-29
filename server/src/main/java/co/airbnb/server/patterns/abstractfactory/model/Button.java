package co.airbnb.server.patterns.abstractfactory.model;

public class Button extends UIComponent {

    public Button(String style) {
        super(style);
    }

    @Override
    public String render() {
        return "Botón " + getStyle();
    }
}