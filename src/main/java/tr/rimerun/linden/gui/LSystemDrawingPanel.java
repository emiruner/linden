package tr.rimerun.linden.gui;

import tr.rimerun.linden.engine.LSystem;
import tr.rimerun.linden.engine.LSystemExecutor;
import tr.rimerun.linden.engine.LSystemTracer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LSystemDrawingPanel extends JPanel {
    private LSystem sys;
    private double stepSize;
    private int depth;
    private int offsetx;
    private int offsety;

    private MouseEvent pressed;

    public LSystemDrawingPanel() {
        this.sys = null;

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                offsetx += e.getX() - pressed.getX();
                offsety += e.getY() - pressed.getY();

                LSystemDrawingPanel.this.repaint();

                pressed = e;
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                pressed = e;
            }

            public void mouseReleased(MouseEvent e) {
                pressed = null;
            }
        });
    }

    public void setParams(LSystem sys, double stepSize, int depth) {
        this.sys = sys;
        this.stepSize = stepSize;
        this.depth = depth;
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);

        if (sys == null) {
            return;
        }

        final double halfWidth = getWidth() / 2.0;
        final double halfHeight = getHeight() / 2.0;

        new LSystemExecutor(sys, depth, stepSize, new LSystemTracer() {
            public void draw(double x0, double y0, double x1, double y1) {
                y0 *= -1;
                y1 *= -1;

                x0 += halfWidth + offsetx;
                y0 += halfHeight + offsety;
                x1 += halfWidth + offsetx;
                y1 += halfHeight + offsety;

                g.drawLine((int) Math.round(x0), (int) Math.round(y0), (int) Math.round(x1), (int) Math.round(y1));
            }
        }).execute();
    }
}
