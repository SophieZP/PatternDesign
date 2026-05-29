package co.airbnb.server.patterns.composite.model;

import java.util.ArrayList;
import java.util.List;

public class PropertyGroup extends PropertyComponent {

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