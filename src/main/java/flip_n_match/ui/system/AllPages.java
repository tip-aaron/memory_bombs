package flip_n_match.ui.system;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

public class AllPages {
    private static AllPages instance;

    private final Map<Class<? extends Page>, Page> pagesMap;

    private static AllPages getInstance() {
        if (instance == null) {
            instance = new AllPages();
        }

        return instance;
    }

    public static void clear() {
        getInstance().pagesMap.forEach((key, val) -> {
            val.destroy();
        });

        getInstance().pagesMap.clear();
    }

    public static void removePage(final Class<? extends Page> cls) {
        getInstance().pagesMap.remove(cls);
    }

    public static void initPage(final Page page) {
        SwingUtilities.invokeLater(() -> {
            page.init();
        });
    }

    public static Page getPage(final Class<? extends Page> cls) {
        if (getInstance().pagesMap.containsKey(cls)) {
            return getInstance().pagesMap.get(cls);
        }

        try {
            final Page page = cls.getDeclaredConstructor().newInstance();

            getInstance().pagesMap.put(cls, page);

            initPage(page);

            return page;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AllPages() {
        pagesMap = new HashMap<>();
    }
}
