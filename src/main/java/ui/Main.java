package ui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine();

        JFrame frame = new JFrame("2D physics engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Panel panel = new Panel(engine);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new Timer(16, e -> {
            engine.update();
            panel.repaint();
        }).start();
    }
}