package rme.linden.gui;

import rme.linden.engine.LSystem;
import rme.linden.engine.LSystemExecutor;
import rme.linden.engine.LSystemTracer;

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
        this.setDoubleBuffered(true);
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

    private class DrawingTracer implements LSystemTracer {
        private final Graphics g;
        private final double halfWidth;
        private final double halfHeight;

        public DrawingTracer(Graphics g, int width, int height) {
            this.g = g;
            this.halfWidth = width / 2.0;
            this.halfHeight = height / 2.0;
        }

        public void draw(double x0, double y0, double x1, double y1) {
            y0 *= -1;
            y1 *= -1;

            x0 += halfWidth + offsetx;
            y0 += halfHeight + offsety;
            x1 += halfWidth + offsetx;
            y1 += halfHeight + offsety;

            g.drawLine((int) Math.round(x0), (int) Math.round(y0), (int) Math.round(x1), (int) Math.round(y1));
        }
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);

        g.drawString("x: " + offsetx, 4, 12);
        g.drawString("y: " + offsety, 4, 24);

        if (sys != null) {
            new LSystemExecutor(sys, depth, stepSize, new DrawingTracer(g, getWidth(), getHeight())).execute();
        }
    }
}
