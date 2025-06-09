package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import object.Entity;
import org.joml.Vector2f;

public class Panel extends JPanel {
    private final float SIM_WIDTH = 800f;
    private final float SIM_HEIGHT = 600f;
    private float uniformScale;
    private int offsetX;
    private int offsetY;
    private final Engine engine;

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
    }

    private int simToScreenX(float x) {
        return Math.round(x * getWidth() / (float) SIM_WIDTH);
    }

    private int simToScreenY(float y) {
        return Math.round(y * getHeight() / (float) SIM_HEIGHT);
    }

    private void updateScaling() {
        float scaleX = getWidth() / SIM_WIDTH;
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
    }
}