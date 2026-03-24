package flip_n_match.ui.pages.settings;

import flip_n_match.game.settings.UserSettings;

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
        return newMasterVolume != UserSettings.getInstance().getAudio().masterVolume().get();
    }

    @Override
    public void applyChanges() {
        if (isDirty()) {
            UserSettings.getInstance().getAudio().masterVolume().set(newMasterVolume);
        }
    }

    @Override
    public void revertChanges() {
        newMasterVolume = UserSettings.getInstance().getAudio().masterVolume().get();

        masterVolumeSlider.setValue(newMasterVolume);
    }

    @Override
    public void loadDefaults() {
        newMasterVolume = UserSettings.getInstance().getAudio().masterVolume().getDefaultValue();

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
