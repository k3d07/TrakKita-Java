package trakkita.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import trakkita.service.AppContext;

/** The window. It only hosts the AppShell and saves the data when closed. */
public class MainFrame extends JFrame {
    private final AppContext ctx;

    public MainFrame(AppContext ctx) {
        super("TrakKita - PUV Operations & Earnings Tracker");
        this.ctx = ctx;
        AppShell shell = new AppShell(ctx);
        setContentPane(shell);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });
        setSize(1180, 760);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null);
        shell.show("Dashboard");
    }

    private void exitApp() {
        try {
            ctx.save();
        } catch (IOException ex) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Your data could not be saved: " + ex.getMessage() + "\nExit anyway?",
                    "Save failed", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        dispose();
        System.exit(0);
    }
}
