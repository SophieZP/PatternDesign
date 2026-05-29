package co.airbnb.server.patterns.abstractfactory;

public class AbstractFactoryHandler {

    public String demo() {
        UIFactory webFactory = new WebUIFactory();
        UIFactory mobileFactory = new MobileUIFactory();
        return webFactory.createButton().render() + " | "
                + webFactory.createCard().render() + " | "
                + mobileFactory.createButton().render() + " | "
                + mobileFactory.createCard().render();
    }

    public abstract static class UIComponent {
        public abstract String render();
    }

    public static final class Button extends UIComponent {
        private final String style;

        public Button(String style) {
            this.style = style;
        }

        @Override
        public String render() {
            return "Botón " + style;
        }
    }

    public static final class Card extends UIComponent {
        private final String style;

        public Card(String style) {
            this.style = style;
        }

        @Override
        public String render() {
            return "Tarjeta " + style;
        }
    }

    public abstract static class UIFactory {
        public abstract Button createButton();

        public abstract Card createCard();
    }

    public static final class WebUIFactory extends UIFactory {
        @Override
        public Button createButton() {
            return new Button("web");
        }

        @Override
        public Card createCard() {
            return new Card("web");
        }
    }

    public static final class MobileUIFactory extends UIFactory {
        @Override
        public Button createButton() {
            return new Button("móvil");
        }

        @Override
        public Card createCard() {
            return new Card("móvil");
        }
    }
}