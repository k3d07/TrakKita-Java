package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import trakkita.gui.widget.CardPanel;
import trakkita.gui.widget.StyledTable;
import trakkita.gui.widget.Theme;
import trakkita.gui.widget.ThemedButton;
import trakkita.gui.widget.ThemedField;
import trakkita.gui.widget.TitleLabel;

/** Small factory methods so every screen is built from the same themed parts. */
public final class UI {
    private UI() { }

    /** Primary = dark button; otherwise the marigold accent button. */
    public static ThemedButton button(String text, boolean primary) {
        return primary ? new ThemedButton(text, Theme.INK, Color.WHITE)
                       : new ThemedButton(text, Theme.MARIGOLD, Theme.INK);
    }

    public static ThemedButton button(String text, Color fill, Color fg) {
        return new ThemedButton(text, fill, fg);
    }

    public static CardPanel card() {
        return new CardPanel();
    }

    /** A card with a colored caption strip on top, like a ticket. */
    public static CardPanel ticket(String caption, Color bg, Color fg) {
        return new CardPanel(caption, bg, fg);
    }

    public static JComponent title(String text) {
        return new TitleLabel(text);
    }

    /** Body text under a title. */
    public static JLabel sub(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.body(15));
        l.setForeground(Theme.MUTED);
        return l;
    }

    /** Small caps caption (also used for field captions). */
    public static JLabel muted(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(Theme.label(14));
        l.setForeground(Theme.MUTED);
        return l;
    }

    public static JLabel value(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.mono(24, true));
        l.setForeground(Theme.INK);
        return l;
    }

    public static ThemedField field(int columns) {
        return new ThemedField(columns);
    }

    /** A caption above a component. */
    public static JPanel labeled(String caption, JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = muted(caption);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(3));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        p.add(c);
        return p;
    }

    /** A left-aligned row of components. */
    public static JPanel row(JComponent... items) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (JComponent c : items) {
            p.add(c);
        }
        return p;
    }

    /** Rows stacked vertically with a gap between them. */
    public static void stack(JPanel host, int gap, JComponent... rows) {
        host.setLayout(new BoxLayout(host, BoxLayout.Y_AXIS));
        for (int i = 0; i < rows.length; i++) {
            rows[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            host.add(rows[i]);
            if (i < rows.length - 1) {
                host.add(Box.createVerticalStrut(gap));
            }
        }
    }

    public static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    public static JTable table(DefaultTableModel m) {
        return new StyledTable(m);
    }

    /** Puts a table in a scroll pane inside a card. */
    public static CardPanel tableCard(JTable t) {
        CardPanel card = new CardPanel();
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12 + CardPanel.SHADOW, 12 + CardPanel.SHADOW));
        card.setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(Theme.INK, 2));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    public static void fullWidth(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public static DocumentListener onChange(Runnable action) {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { action.run(); }
            @Override public void removeUpdate(DocumentEvent e) { action.run(); }
            @Override public void changedUpdate(DocumentEvent e) { action.run(); }
        };
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Please check your input", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "TrakKita", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    /** Used by screens that need a JTextField type for a filter box. */
    public static JTextField searchField(int cols) {
        return new ThemedField(cols);
    }

    /** Makes Enter in any form field submit the form. */
    public static void bindEnter(Runnable submit, JTextField... fields) {
        for (JTextField field : fields) field.addActionListener(e -> submit.run());
    }

    /** Makes Escape clear a form while focus is anywhere inside its component tree. */
    public static void bindEscape(JComponent root, Runnable clear) {
        String action = "clear-form";
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("ESCAPE"), action);
        root.getActionMap().put(action, new javax.swing.AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { clear.run(); }
        });
    }
}
