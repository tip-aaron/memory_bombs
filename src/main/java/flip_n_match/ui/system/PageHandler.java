package flip_n_match.ui.system;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import flip_n_match.ui.pages.PageStartMenu;

public class PageHandler {
    private static JFrame frame;
    private static RootPage rootPage;
    private static Page prevPage;

    private static void install() {
    }

    private static RootPage getRootPage() {
        if (rootPage == null) {
            rootPage = new RootPage();
        }

        return rootPage;
    }

    public static void install(final JFrame frame) {
        PageHandler.frame = frame;

        install();

        showDefaultPage();
        final Page page = AllPages.getPage(PageStartMenu.class);

        SwingUtilities.invokeLater(() -> showPage(page));
    }

    public static void showDefaultPage() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(getRootPage(), BorderLayout.CENTER);

        frame.repaint();
        frame.revalidate();
    }

    public static void showPage(final Page page) {
        if (prevPage != null) {
            prevPage.beforeClose();
        }

        page.themeCheck();
        page.open();
        rootPage.setPage(page);
        page.refresh();
        page.afterOpen();

        if (prevPage != null) {
            prevPage.close();
        }

        prevPage = page;
    }
}
