package co.airbnb.server.patterns.abstractfactory.model;

public abstract class UIComponent {

    private final String style;

    protected UIComponent(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public abstract String render();
}