package flip_n_match.ui.system;

import javax.swing.SwingUtilities;

public class Navigator {
    public static void navigate(final Class<? extends Page> page) {
        final Page pageInstance = AllPages.getPage(page);

        SwingUtilities.invokeLater(() -> PageHandler.showPage(pageInstance));
    }
}
