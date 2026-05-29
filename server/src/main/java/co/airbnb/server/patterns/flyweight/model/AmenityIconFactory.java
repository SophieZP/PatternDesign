package co.airbnb.server.patterns.flyweight.model;

import java.util.HashMap;
import java.util.Map;

public class AmenityIconFactory {

    private final Map<String, AmenityIcon> cache = new HashMap<String, AmenityIcon>();

    public AmenityIcon getIcon(String name) {
        AmenityIcon icon = cache.get(name);
        if (icon == null) {
            icon = new AmenityIcon(name);
            cache.put(name, icon);
        }
        return icon;
    }

    public int size() {
        return cache.size();
    }
}