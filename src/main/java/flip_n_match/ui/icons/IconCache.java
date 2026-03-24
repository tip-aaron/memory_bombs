package flip_n_match.ui.icons;

import java.util.HashMap;
import java.util.Map;

public class IconCache {
    private static final Map<String, SVGIconUIColor> cache = new HashMap<>();

    public static SVGIconUIColor get(String key, String colorKey) {
        String cacheKey = key + "_" + colorKey;

        return cache.computeIfAbsent(cacheKey, k ->
                new SVGIconUIColor(key, 1f, colorKey)
        );
    }

    public static void clear() {
        cache.clear();
    }
}
