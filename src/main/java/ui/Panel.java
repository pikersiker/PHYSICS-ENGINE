package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import object.Entity;
import org.joml.Vector2f;

public class Panel extends JPanel {
    private float uniformScale;
    private int offsetX;
    private int offsetY;
    private final Engine engine;
    private int fps;

    public void setFps(int fps) {
        this.fps = fps;
    }

    public Panel(Engine engine) {
        this.engine = engine;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        updateScaling();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateScaling();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Vector2f simPos = screenToSim(new Vector2f(e.getX(), e.getY()));

                Entity newSquare = new Entity(simPos, 0f, new Vector2f(40, 40), false);
                newSquare.updateOBB();
                engine.addEntity(newSquare);

                repaint();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "resetEntities");
        getActionMap().put("resetEntities", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.clearDynamicEntities();
                repaint();
            }
        });
    }

    private void updateScaling() {
        float SIM_WIDTH = 800f;
        float scaleX = getWidth() / SIM_WIDTH;
        float SIM_HEIGHT = 600f;
        float scaleY = getHeight() / SIM_HEIGHT;
        uniformScale = Math.min(scaleX, scaleY);
        offsetX = (int) ((getWidth() - (SIM_WIDTH * uniformScale)) / 2);
        offsetY = (int) ((getHeight() - (SIM_HEIGHT * uniformScale)) / 2);
    }

    private Vector2f screenToSim(Vector2f screenPos) {
        float simX = (screenPos.x - offsetX) / uniformScale;
        float simY = (screenPos.y - offsetY) / uniformScale;
        return new Vector2f(simX, simY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.translate(offsetX, offsetY);
        g2d.scale(uniformScale, uniformScale);

        for (Entity ent : engine.getEntities()) {
            g2d.setColor(ent.rigidBody.isStatic() ? Color.DARK_GRAY : Color.RED);

            Vector2f[] v = ent.obb.getVertices();
            int[] xPoints = new int[4];
            int[] yPoints = new int[4];
            for (int i = 0; i < 4; i++) {
                xPoints[i] = (int) v[i].x;
                yPoints[i] = (int) v[i].y;
            }
            g2d.fillPolygon(xPoints, yPoints, 4);
        }
        g2d.setTransform(new java.awt.geom.AffineTransform());
        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + fps, 10, 20);
    }
}