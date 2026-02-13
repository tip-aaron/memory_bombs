package flip_n_match.ui.themes;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import flip_n_match.config.GameTheme;
import flip_n_match.config.UserSettings;

public class ThemeManager {

    public static void manage() {
        if (UserSettings.getInstance().getCurrentTheme() == GameTheme.DARK) {
            FlipMatchDarkTheme.setup();
        } else if (UserSettings.getInstance().getCurrentTheme() == GameTheme.LIGHT) {

        }

        UserSettings.getInstance().addPropertyChangeListener(new PropertyChangeListener() {
            private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            private ScheduledFuture<?> scheduledFuture;

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (!"theme".equals(evt.getPropertyName())) {
                    return;
                }

                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }

                scheduledFuture = scheduler.schedule(() -> {
                    if (evt.getNewValue() == GameTheme.DARK) {
                        EventQueue.invokeLater(() -> {
                            FlatAnimatedLafChange.showSnapshot();
                            FlipMatchDarkTheme.setup();
                            FlatLaf.updateUI();
                            FlatAnimatedLafChange.hideSnapshotWithAnimation();
                        });
                    } else if (evt.getNewValue() == GameTheme.LIGHT) {
                        EventQueue.invokeLater(() -> {
                            FlatAnimatedLafChange.showSnapshot();
                            FlipMatchLightTheme.setup();
                            FlatLaf.updateUI();
                            FlatAnimatedLafChange.hideSnapshotWithAnimation();
                        });
                    }
                }, 500, TimeUnit.MILLISECONDS);
            }
        });
    }
}
