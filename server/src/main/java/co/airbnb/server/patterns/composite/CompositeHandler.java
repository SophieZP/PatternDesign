package co.airbnb.server.patterns.composite;

import java.util.ArrayList;
import java.util.List;

public class CompositeHandler {

    public String demo() {
        PropertyGroup apartment = new PropertyGroup("Apartamento Centro");
        apartment.add(new Room("Habitación 1"));
        apartment.add(new Room("Habitación 2"));

        PropertyGroup building = new PropertyGroup("Edificio Principal");
        building.add(apartment);
        building.add(new Room("Habitación Penthouse"));

        return building.show();
    }

    public abstract static class PropertyComponent {
        protected final String name;

        protected PropertyComponent(String name) {
            this.name = name;
        }

        public abstract String show();
    }

    public static final class Room extends PropertyComponent {
        public Room(String name) {
            super(name);
        }

        @Override
        public String show() {
            return name;
        }
    }

    public static final class PropertyGroup extends PropertyComponent {
        private final List<PropertyComponent> children = new ArrayList<PropertyComponent>();

        public PropertyGroup(String name) {
            super(name);
        }

        public void add(PropertyComponent component) {
            children.add(component);
        }

        @Override
        public String show() {
            StringBuilder builder = new StringBuilder(name);
            for (PropertyComponent child : children) {
                builder.append(" -> ").append(child.show());
            }
            return builder.toString();
        }
    }
}