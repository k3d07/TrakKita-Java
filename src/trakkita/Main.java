package trakkita;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import trakkita.gui.MainFrame;
import trakkita.gui.widget.Theme;
import trakkita.service.AppContext;
import trakkita.storage.CsvDataStore;

/** Entry point. It only launches the application. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Using the default look and feel: " + ex.getMessage());
            }
            Theme.install();
            AppContext ctx = new AppContext(new CsvDataStore("data/puv_records.csv"));
            ctx.load();
            MainFrame frame = new MainFrame(ctx);
            frame.setVisible(true);
            if (!ctx.getStartupWarnings().isEmpty()) {
                JOptionPane.showMessageDialog(frame, String.join("\n", ctx.getStartupWarnings()),
                        "Some saved data was skipped", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
