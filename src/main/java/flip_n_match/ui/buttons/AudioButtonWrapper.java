package flip_n_match.ui.buttons;

import flip_n_match.audio.AudioManager;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * plays the sfx for hover and click
 */
@Getter
public class AudioButtonWrapper {
    private final JButton button;
    private final ActionListener clickListener;
    private final MouseAdapter hoverListener;

    public AudioButtonWrapper(String text, Icon icon, Runnable onClickAction) {
        this.button = new JButton(text, icon);

        this.clickListener = e -> {
            AudioManager.getInstance().playSfx(AudioManager.Sfx.BUTTON_CLICK);

            if (onClickAction != null) {
                onClickAction.run();
            }
        };

        this.hoverListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    AudioManager.getInstance().playSfx(AudioManager.Sfx.BUTTON_HOVER);
                }
            }
        };
    }

    /**
     * Attaches the listeners to the button. Call this in your Page's open() method.
     */
    public void bind() {
        button.addActionListener(clickListener);
        button.addMouseListener(hoverListener);
    }

    /**
     * Detaches the listeners from the button. Call this in your Page's close() method.
     */
    public void unbind() {
        button.removeActionListener(clickListener);
        button.removeMouseListener(hoverListener);
    }
}
