package flip_n_match.ui.pages.settings;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.audio.AudioManager;
import flip_n_match.game.settings.UserSettings;
import flip_n_match.ui.icons.SVGIconUIColor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

@TabInfo(name = "Audio")
public class AudioTab extends ASettingsTab {
    private final JSlider masterVolumeSlider;
    private final JSlider sfxVolumeSlider;
    private final JSlider musicVolumeSlider;
    private final JComboBox<AudioManager.Music> musicComboBox;
    private final JComboBox<AudioManager.Sfx> sfxComboBox;
    private final JCheckBox sfxEnabledCheckbox;
    private final JCheckBox musicEnabledCheckbox;

    private final JButton previewSfxButton;

    private final ChangeListener sliderChangeListener;
    private final ActionListener actionListener;
    private final ActionListener previewSfxActionListener;

    private int newMasterVolume;
    private int newSfxVolume;
    private int newMusicVolume;
    private boolean newSfxEnabled;
    private boolean newMusicEnabled;
    private AudioManager.Music newMusic;
    private AudioManager.Sfx newSfx;

    private boolean isUpdatingUI = false;
    private boolean isInitialized = false;

    public AudioTab() {
        super();

        addSectionHeader("Volume & Toggles", "Control your global audio levels.");

        masterVolumeSlider = new JSlider(0, 100);
        sfxVolumeSlider = new JSlider(0, 100);
        musicVolumeSlider = new JSlider(0, 100);

        sfxEnabledCheckbox = new JCheckBox("Enabled");
        musicEnabledCheckbox = new JCheckBox("Enabled");

        musicComboBox = new JComboBox<>(AudioManager.Music.values());
        sfxComboBox = new JComboBox<>(AudioManager.Sfx.values());

        previewSfxButton = new JButton("Preview SFX", new SVGIconUIColor("play.svg", 0.75f, "foreground.muted"));

        sliderChangeListener = new SliderChangeListener();
        actionListener = new ComponentActionListener();

        previewSfxActionListener = e -> {
            if (newSfxEnabled && newMasterVolume > 0 && newSfxVolume > 0) {
                AudioManager.getInstance().playSfx((AudioManager.Sfx) Objects.requireNonNull(sfxComboBox.getSelectedItem()));
            }
        };

        // --- Master Section ---
        add(new JLabel("Master Volume: "));
        add(masterVolumeSlider);

        // --- SFX Section ---
        add(createLabelWithDescription("Sfx Status: ", "Globally enable or disable all sound effects across the game."));
        add(sfxEnabledCheckbox);

        add(new JLabel("Sfx Volume: "));
        add(sfxVolumeSlider);

        // --- Music Section ---
        add(createLabelWithDescription("Music Status: ", "Globally enable or disable background music across the game."));
        add(musicEnabledCheckbox);

        add(new JLabel("Music Volume: "));
        add(musicVolumeSlider);

        // --- Tracks Section ---
        addSectionHeader("Tracks", "Select your preferred tracks. Previews respect your current UI volume/toggles.");

        add(createLabelWithDescription("Background Music: ", "Select the main background music to play during your sessions."));
        add(musicComboBox);

        add(createLabelWithDescription("Test/Default SFX: ", "Select a sound effect to test your current volume settings."));
        add(createInputWithPreviewButton(sfxComboBox, previewSfxButton));
    }

    private JPanel createLabelWithDescription(String title, String desc) {
        JPanel container = new JPanel(new MigLayout("insets 0, al left top"));
        JLabel descLabel = new JLabel("<html>" + desc + "</html>");

        descLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "mini muted");
        descLabel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

        container.add(new JLabel(title), "wrap, gapbottom 4px");
        container.add(descLabel);

        return container;
    }

    private JPanel createInputWithPreviewButton(JComboBox<?> comboBox, JButton previewButton) {
        JPanel container = new JPanel(new MigLayout("insets 0, al right top"));
        container.add(comboBox, "wrap, growx, gapbottom 4px");
        container.add(previewButton, "al right");

        return container;
    }

    @Override
    public void open() {
        // to turn the logic if restarting, in
        // revertChanges() to allow us to not play
        // on first load.
        revertCurrentState();

        masterVolumeSlider.addChangeListener(sliderChangeListener);
        sfxVolumeSlider.addChangeListener(sliderChangeListener);
        musicVolumeSlider.addChangeListener(sliderChangeListener);

        musicComboBox.addActionListener(actionListener);
        sfxComboBox.addActionListener(actionListener);
        sfxEnabledCheckbox.addActionListener(actionListener);
        musicEnabledCheckbox.addActionListener(actionListener);

        previewSfxButton.addActionListener(previewSfxActionListener);

        isInitialized = true;
    }

    @Override
    public void close() {
        masterVolumeSlider.removeChangeListener(sliderChangeListener);
        sfxVolumeSlider.removeChangeListener(sliderChangeListener);
        musicVolumeSlider.removeChangeListener(sliderChangeListener);

        musicComboBox.removeActionListener(actionListener);
        sfxComboBox.removeActionListener(actionListener);
        sfxEnabledCheckbox.removeActionListener(actionListener);
        musicEnabledCheckbox.removeActionListener(actionListener);

        previewSfxButton.removeActionListener(previewSfxActionListener);

        isInitialized = false;
    }

    @Override
    public boolean isDirty() {
        var audio = UserSettings.getInstance().getAudio();

        return newMasterVolume != audio.masterVolume().get() ||
                newSfxVolume != audio.sfxVolume().get() ||
                newMusicVolume != audio.musicVolume().get() ||
                newSfxEnabled != audio.sfxEnabled().get() ||
                newMusicEnabled != audio.musicEnabled().get() ||
                newMusic != audio.selectedMusic().get() ||
                newSfx != audio.selectedSfx().get();
    }

    @Override
    public void applyChanges() {
        if (isDirty()) {
            var audio = UserSettings.getInstance().getAudio();

            audio.masterVolume().set(newMasterVolume);
            audio.sfxVolume().set(newSfxVolume);
            audio.musicVolume().set(newMusicVolume);
            audio.sfxEnabled().set(newSfxEnabled);
            audio.musicEnabled().set(newMusicEnabled);
            audio.selectedMusic().set(newMusic);
            audio.selectedSfx().set(newSfx);
        }
    }

    private void revertCurrentState() {
        var audio = UserSettings.getInstance().getAudio();

        // Just update variables and UI, do NOT touch AudioManager playback
        newMasterVolume = audio.masterVolume().get();
        newSfxVolume = audio.sfxVolume().get();
        newMusicVolume = audio.musicVolume().get();
        newSfxEnabled = audio.sfxEnabled().get();
        newMusicEnabled = audio.musicEnabled().get();
        newMusic = audio.selectedMusic().get();
        newSfx = audio.selectedSfx().get();

        updateUIFromState();
        applyPreviewVolumes();
    }

    @Override
    public void revertChanges() {
        var audio = UserSettings.getInstance().getAudio();
        AudioManager.Music savedMusic = audio.selectedMusic().get();

        // ONLY restore audio if the tab is already active (meaning a user "Cancel" action)
        // and the track currently in the 'newMusic' preview differs from 'saved'
        if (isInitialized && newMusic != savedMusic) {
            if (audio.musicEnabled().get()) {
                AudioManager.getInstance().playMusic(savedMusic);
            } else {
                AudioManager.getInstance().stopMusic();
            }
        }

        revertCurrentState();
    }

    @Override
    public void loadDefaults() {
        var audio = UserSettings.getInstance().getAudio();

        newMasterVolume = audio.masterVolume().getDefaultValue();
        newSfxVolume = audio.sfxVolume().getDefaultValue();
        newMusicVolume = audio.musicVolume().getDefaultValue();
        newSfxEnabled = audio.sfxEnabled().getDefaultValue();
        newMusicEnabled = audio.musicEnabled().getDefaultValue();
        newMusic = audio.selectedMusic().getDefaultValue();
        newSfx = audio.selectedSfx().getDefaultValue();

        updateUIFromState();
        applyPreviewVolumes();
        notifyDirty();
    }

    private void updateUIFromState() {
        isUpdatingUI = true; // Block action listeners

        masterVolumeSlider.setValue(newMasterVolume);
        sfxVolumeSlider.setValue(newSfxVolume);
        musicVolumeSlider.setValue(newMusicVolume);
        sfxEnabledCheckbox.setSelected(newSfxEnabled);
        musicEnabledCheckbox.setSelected(newMusicEnabled);
        musicComboBox.setSelectedItem(newMusic);
        sfxComboBox.setSelectedItem(newSfx);

        sfxComboBox.setEnabled(newSfxEnabled);
        sfxVolumeSlider.setEnabled(newSfxEnabled);
        musicVolumeSlider.setEnabled(newMusicEnabled);
        previewSfxButton.setEnabled(newSfxEnabled);

        isUpdatingUI = false;
    }

    /**
     * Temporarily overrides the audio output while in the menu so previews
     * reflect the active UI elements without saving to the UserSettings.
     */
    private void applyPreviewVolumes() {
        float masterBase = newMasterVolume / 100.0f;
        float sfxBase = newSfxVolume / 100.0f;
        float musicBase = newMusicVolume / 100.0f;

        float effectiveSfxVolume = newSfxEnabled ? (masterBase * sfxBase) : 0.0f;
        float effectiveMusicVolume = newMusicEnabled ? (masterBase * musicBase) : 0.0f;

        AudioManager.getInstance().setSfxVolume(effectiveSfxVolume);
        AudioManager.getInstance().setMusicVolume(effectiveMusicVolume);
    }

    class SliderChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            newMasterVolume = masterVolumeSlider.getValue();
            newSfxVolume = sfxVolumeSlider.getValue();
            newMusicVolume = musicVolumeSlider.getValue();

            applyPreviewVolumes();
            notifyDirty();
        }
    }

    class ComponentActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isUpdatingUI) return;

            AudioManager.Music selectedMusic = (AudioManager.Music) musicComboBox.getSelectedItem();

            // ONLY play music if the selection actually changed and music is enabled
            if (selectedMusic != newMusic && musicEnabledCheckbox.isSelected()) {
                assert selectedMusic != null;
                AudioManager.getInstance().playMusic(selectedMusic);
            }
            // If user unchecks "Music Enabled", stop it immediately
            else if (e.getSource() == musicEnabledCheckbox && !musicEnabledCheckbox.isSelected()) {
                AudioManager.getInstance().stopMusic();
            }
            // If user checks "Music Enabled" and nothing is playing, start it
            else if (e.getSource() == musicEnabledCheckbox && musicEnabledCheckbox.isSelected()) {
                if (!AudioManager.getInstance().getIsMusicPlaying()) {
                    assert selectedMusic != null;
                    AudioManager.getInstance().playMusic(selectedMusic);
                }
            }

            newMusic = selectedMusic;
            newSfx = (AudioManager.Sfx) sfxComboBox.getSelectedItem();
            newSfxEnabled = sfxEnabledCheckbox.isSelected();
            newMusicEnabled = musicEnabledCheckbox.isSelected();

            sfxComboBox.setEnabled(newSfxEnabled);
            sfxVolumeSlider.setEnabled(newSfxEnabled);
            musicVolumeSlider.setEnabled(newMusicEnabled);
            previewSfxButton.setEnabled(newSfxEnabled);

            applyPreviewVolumes();
            notifyDirty();
        }
    }
}