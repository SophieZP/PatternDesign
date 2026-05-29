package co.airbnb.server.patterns.flyweight;

import java.util.HashMap;
import java.util.Map;

public class FlyweightHandler {

    public String demo() {
        AmenityIconFactory factory = new AmenityIconFactory();
        AmenityIcon wifi1 = factory.getIcon("Wi-Fi");
        AmenityIcon wifi2 = factory.getIcon("Wi-Fi");
        AmenityIcon pool = factory.getIcon("Piscina");
        return wifi1.render(1) + " | " + wifi2.render(2) + " | " + pool.render(3) + " | cache=" + factory.size();
    }

    public static final class AmenityIcon {
        private final String name;

        public AmenityIcon(String name) {
            this.name = name;
        }

        public String render(int listingId) {
            return name + "@" + listingId;
        }
    }

    public static final class AmenityIconFactory {
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
}