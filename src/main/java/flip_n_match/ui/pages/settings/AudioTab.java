package flip_n_match.ui.pages.settings;

import flip_n_match.config.UserSettings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@TabInfo(name = "Audio")
public class AudioTab extends ASettingsTab {
    private final JSlider masterVolumeSlider;
    private final ChangeListener masterVolumeChangeListener;
    private int newMasterVolume;

    public AudioTab() {
        super();

        addSectionHeader("Volume", "");

        masterVolumeSlider = new JSlider(0, 100);
        masterVolumeChangeListener = new MasterVolumeChangeListener();

        add(new JLabel("Master Volume: "));
        add(masterVolumeSlider);

        revertChanges();
    }

    @Override
    public void open() {
        masterVolumeSlider.addChangeListener(masterVolumeChangeListener);
    }

    @Override
    public void close() {
        masterVolumeSlider.removeChangeListener(masterVolumeChangeListener);
    }

    @Override
    public boolean isDirty() {
        return newMasterVolume != UserSettings.getInstance().getVolume();
    }

    @Override
    public void applyChanges() {
        if (isDirty()) {
            UserSettings.getInstance().setVolume(newMasterVolume);
        }
    }

    @Override
    public void revertChanges() {
        newMasterVolume = UserSettings.getInstance().getVolume();

        masterVolumeSlider.setValue(newMasterVolume);
    }

    @Override
    public void loadDefaults() {
        newMasterVolume = UserSettings.getInstance().getDEFAULT_VOLUME();

        masterVolumeSlider.setValue(newMasterVolume);
    }

    class MasterVolumeChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (!masterVolumeSlider.getValueIsAdjusting()) {
                newMasterVolume = masterVolumeSlider.getValue();

                notifyDirty();
            }
        }
    }
}
