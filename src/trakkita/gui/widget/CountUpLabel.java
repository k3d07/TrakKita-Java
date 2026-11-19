package trakkita.gui.widget;

import javax.swing.JLabel;
import trakkita.util.Money;

/** A label showing pesos that counts up to its value. */
public class CountUpLabel extends JLabel {
    private final CountUp counter = new CountUp();

    public CountUpLabel(float size) {
        super("-");
        setFont(Theme.mono(size, true));
        setForeground(Theme.INK);
        Anim.bind(this, () -> {
            if (counter.step()) {
                setText(Money.peso(counter.get()));
            }
            return false;
        });
    }

    public void setAmount(double value) {
        counter.set(value, true);
        if (Anim.isInstant() || !isDisplayable()) {
            counter.set(value, false);
        }
        setText(Money.peso(counter.get()));
    }

    public void setDash() {
        counter.set(0, false);
        setText("-");
    }
}
