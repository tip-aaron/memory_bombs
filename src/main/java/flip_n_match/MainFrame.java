package flip_n_match;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import flip_n_match.constants.Metadata;
import flip_n_match.ui.system.PageHandler;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle(String.format("%s v%s", Metadata.APP_TITLE, Metadata.VERSION));

        //getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);

        PageHandler.install(this);

        setPreferredSize(new Dimension(1280, 720));
        setSize(getPreferredSize());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                final int res = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the game?");

                if (res == 0) {
                    App.close();
                } else {
                    super.windowClosing(e);
                }
            }
        });
    }

}
