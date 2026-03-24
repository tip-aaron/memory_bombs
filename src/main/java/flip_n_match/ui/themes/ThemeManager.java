package flip_n_match.ui.themes;

import java.awt.EventQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import flip_n_match.game.settings.GameTheme;
import flip_n_match.game.settings.UserSettings;
import flip_n_match.game.settings.events.SettingsChangeListener;

public class ThemeManager {
    public static void manage() {
        if (UserSettings.getInstance().getAppearance().theme().get() == GameTheme.DARK) {
            FlipMatchDarkTheme.setup();
        } else if (UserSettings.getInstance().getAppearance().theme().get() == GameTheme.LIGHT) {
            FlipMatchLightTheme.setup();
        }

        UserSettings.getInstance().getAppearance().theme().addListener(new SettingsChangeListener<>() {
            private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            private ScheduledFuture<?> scheduledFuture;

            @Override
            public void onValueChanged(GameTheme oldValue, GameTheme newValue) {
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }

                scheduledFuture = scheduler.schedule(() -> {
                    switch (newValue) {
                        case GameTheme.DARK -> EventQueue.invokeLater(() -> {
                            FlatAnimatedLafChange.showSnapshot();
                            FlipMatchDarkTheme.setup();
                            FlatLaf.updateUI();
                            FlatAnimatedLafChange.hideSnapshotWithAnimation();
                        });
                        case GameTheme.LIGHT -> EventQueue.invokeLater(() -> {
                            FlatAnimatedLafChange.showSnapshot();
                            FlipMatchLightTheme.setup();
                            FlatLaf.updateUI();
                            FlatAnimatedLafChange.hideSnapshotWithAnimation();
                        });
                    }
                }, 250, TimeUnit.MILLISECONDS);
            }
        });
    }
}
