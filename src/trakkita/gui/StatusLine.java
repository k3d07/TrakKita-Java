package trakkita.gui;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.Timer;
import trakkita.gui.widget.Theme;

/** Brief inline success/error feedback for a screen. */
public final class StatusLine extends JLabel {
    private final Timer clearTimer;

    public StatusLine() {
        super(" ");
        setFont(Theme.mono(12, false));
        setForeground(Theme.MUTED);
        setVisible(false);
        clearTimer = new Timer(5000, e -> setVisible(false));
        clearTimer.setRepeats(false);
    }

    public void showMessage(String message) {
        showMessage(message, false);
    }

    public void showMessage(String message, boolean error) {
        setText(message == null || message.isBlank() ? " " : message);
        setForeground(error ? Theme.DARK_RED : Theme.TEAL);
        setVisible(true);
        clearTimer.restart();
    }
}
