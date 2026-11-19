package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import trakkita.gui.widget.BackdropPanel;
import trakkita.gui.widget.NavButton;
import trakkita.gui.widget.SignBoard;
import trakkita.gui.widget.StripeBand;
import trakkita.gui.widget.Theme;
import trakkita.service.AppContext;

/** The whole window content: sidebar on the left, the active screen on the right. */
public class AppShell extends JPanel {
    private final CardLayout cards = new CardLayout();
    private final BackdropPanel content = new BackdropPanel();
    private final Map<String, ScreenPanel> screens = new LinkedHashMap<>();
    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();

    public AppShell(AppContext ctx) {
        setLayout(new BorderLayout());
        content.setLayout(cards);
        screens.put("Dashboard", new DashboardPanel(ctx));
        screens.put("Profit / Loss", new ProfitPanel(ctx));
        screens.put("Fuel Log", new FuelPanel(ctx));
        screens.put("Routes", new RoutePanel(ctx));
        screens.put("Manage Routes", new RouteManagerPanel(ctx));
        screens.put("Break-Even", new BreakEvenPanel(ctx));
        screens.put("Quick-Tap", new QuickTapPanel(ctx));
        for (Map.Entry<String, ScreenPanel> e : screens.entrySet()) {
            e.getValue().setNavigator(this::show);
            content.add(e.getValue(), e.getKey());
        }
        add(buildSidebar(), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(Theme.INDIGO);
        side.setPreferredSize(new Dimension(226, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(20, 16, 18, 16));

        SignBoard sign = new SignBoard();
        sign.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(sign);
        side.add(Box.createVerticalStrut(18));
        for (String name : screens.keySet()) {
            NavButton b = new NavButton(name);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.addActionListener(e -> show(name));
            navButtons.put(name, b);
            side.add(b);
            side.add(Box.createVerticalStrut(2));
        }
        side.add(Box.createVerticalGlue());
        StripeBand band = new StripeBand();
        band.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(band);
        side.add(Box.createVerticalStrut(8));
        JLabel foot = new JLabel("saved: data/puv_records.csv");
        foot.setFont(Theme.mono(11, false));
        foot.setForeground(Theme.SIDE_MUTED);
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(foot);
        return side;
    }

    /** Switches screens: refresh the data, flip the card, then let the screen animate in. */
    public void show(String name) {
        ScreenPanel s = screens.get(name);
        s.refresh();
        cards.show(content, name);
        s.onShown();
        for (Map.Entry<String, NavButton> e : navButtons.entrySet()) {
            e.getValue().setActive(e.getKey().equals(name));
        }
    }

    public ScreenPanel getScreen(String name) {
        return screens.get(name);
    }

    public java.util.Set<String> screenNames() {
        return screens.keySet();
    }
}
