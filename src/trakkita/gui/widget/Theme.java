package trakkita.gui.widget;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.UIManager;

/** Colors, fonts and drawing helpers shared by every screen (the "jeepney" theme). */
public final class Theme {
    public static final Color INK = new Color(0x1D1A2F);
    public static final Color CREAM = new Color(0xFFF3D6);
    public static final Color INDIGO = new Color(0x1B1A3A);
    public static final Color TEAL = new Color(0x0B7A75);
    public static final Color MARIGOLD = new Color(0xFFB627);
    public static final Color RED = new Color(0xE4372B);
    public static final Color DARK_RED = new Color(0xC62A1F);
    public static final Color LED_BG = new Color(0x0E0D1C);
    public static final Color MINT = new Color(0x5CFF9D);
    public static final Color CORAL = new Color(0xFF7A6E);
    public static final Color AQUA = new Color(0x9FE3DC);
    public static final Color MUTED = new Color(0x4A4660);
    public static final Color DOT = new Color(0xE9D9B0);
    public static final Color PAPER = new Color(0xFFFBF0);
    public static final Color PALE_RED = new Color(0xFDE3DF);
    public static final Color PALE_GREEN = new Color(0xDDF5E7);
    public static final Color ZEBRA = new Color(0xFFF8E6);
    public static final Color SIDE_TEXT = new Color(0xF3E9D2);
    public static final Color SIDE_MUTED = new Color(0xB9B4D6);

    private static Font display;
    private static Font label;
    private static Font body;
    private static Font monoBold;
    private static Font monoMedium;
    private static TexturePaint dots;

    private Theme() { }

    private static synchronized void load() {
        if (display != null) {
            return;
        }
        display = read("Bungee-Regular.ttf", new Font("SansSerif", Font.BOLD, 12));
        label = read("BarlowCondensed-Bold.ttf", new Font("SansSerif", Font.BOLD, 12));
        body = read("Barlow-SemiBold.ttf", new Font("SansSerif", Font.BOLD, 12));
        monoBold = read("IBMPlexMono-Bold.ttf", new Font("Monospaced", Font.BOLD, 12));
        monoMedium = read("IBMPlexMono-Medium.ttf", new Font("Monospaced", Font.PLAIN, 12));
    }

    /** Loads fonts/NAME from the working folder; falls back to a built-in font if missing. */
    private static Font read(String file, Font fallback) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("fonts", file));
        } catch (FontFormatException | IOException e) {
            System.err.println("Font fonts/" + file + " not available, using a fallback font.");
            return fallback;
        }
    }

    public static Font display(float size) { load(); return display.deriveFont(size); }
    public static Font label(float size) { load(); return label.deriveFont(size); }
    public static Font body(float size) { load(); return body.deriveFont(size); }
    public static Font mono(float size, boolean bold) { load(); return (bold ? monoBold : monoMedium).deriveFont(size); }

    /** A smooth (anti-aliased) copy of the Graphics to draw with. Caller disposes it. */
    public static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    public static double clamp(double v) { return Math.max(0, Math.min(1, v)); }

    public static double easeOut(double t) {
        double u = 1 - clamp(t);
        return 1 - u * u * u;
    }

    public static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }

    /** Rounded box with a hard offset shadow, the signature look of the app. */
    public static void hardBox(Graphics2D g, double x, double y, double w, double h, double arc,
                               double shadow, Color fill, Color line, float stroke) {
        if (shadow > 0) {
            g.setColor(INK);
            g.fill(new RoundRectangle2D.Double(x + shadow, y + shadow, w, h, arc, arc));
        }
        g.setColor(fill);
        g.fill(new RoundRectangle2D.Double(x, y, w, h, arc, arc));
        g.setColor(line);
        g.setStroke(new BasicStroke(stroke));
        g.draw(new RoundRectangle2D.Double(x + stroke / 2, y + stroke / 2, w - stroke, h - stroke, arc, arc));
    }

    /** Draws text with a soft glow (like a lit LED). glow is 0..1. */
    public static void glowText(Graphics2D g, String s, Font f, float x, float baseline, Color c, double glow) {
        if (s == null || s.isEmpty()) {
            return;
        }
        FontRenderContext frc = g.getFontRenderContext();
        Shape outline = new TextLayout(s, f, frc).getOutline(AffineTransform.getTranslateInstance(x, baseline));
        g.setStroke(new BasicStroke(1f));
        if (glow > 0) {
            float[] widths = {12f, 8f, 4f};
            int[] alphas = {14, 22, 36};
            for (int i = 0; i < widths.length; i++) {
                g.setStroke(new BasicStroke(widths[i], BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(alpha(c, (int) (alphas[i] * glow)));
                g.draw(outline);
            }
        }
        g.setColor(c);
        g.fill(outline);
    }

    /** Fills a shape with diagonal stripes of two colors (a non-color-only "loss" cue). */
    public static void hatch(Graphics2D g0, Shape shape, Color base, Color stripe, int width) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setClip(shape);
        Rectangle2D b = shape.getBounds2D();
        g.setColor(base);
        g.fill(b);
        g.setColor(stripe);
        double h = b.getHeight();
        for (double x = b.getX() - h; x < b.getMaxX() + h; x += width * 2) {
            Path2D p = new Path2D.Double();
            p.moveTo(x + h, b.getY());
            p.lineTo(x + h + width, b.getY());
            p.lineTo(x + width, b.getMaxY());
            p.lineTo(x, b.getMaxY());
            p.closePath();
            g.fill(p);
        }
        g.dispose();
    }

    /** Cream background with a faint dot grid, built once as a tiny tile. */
    public static synchronized Paint dotsPaint() {
        if (dots == null) {
            BufferedImage tile = new BufferedImage(18, 18, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = tile.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(CREAM);
            g.fillRect(0, 0, 18, 18);
            g.setColor(DOT);
            g.fillOval(8, 8, 3, 3);
            g.dispose();
            dots = new TexturePaint(tile, new Rectangle2D.Double(0, 0, 18, 18));
        }
        return dots;
    }

    public static void setAlpha(Graphics2D g, float a) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, a))));
    }

    /** Makes dialogs, combo boxes and tooltips match the theme. Call once at startup. */
    public static void install() {
        UIManager.put("OptionPane.background", CREAM);
        UIManager.put("Panel.background", CREAM);
        UIManager.put("OptionPane.messageFont", mono(13, false));
        UIManager.put("OptionPane.buttonFont", body(14));
        UIManager.put("OptionPane.messageForeground", INK);
        UIManager.put("Button.font", body(14));
        UIManager.put("ComboBox.font", mono(14, false));
        UIManager.put("ComboBox.background", PAPER);
        UIManager.put("ComboBox.foreground", INK);
        UIManager.put("ComboBox.selectionBackground", MARIGOLD);
        UIManager.put("ComboBox.selectionForeground", INK);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("swing.boldMetal", Boolean.FALSE);
    }
}
