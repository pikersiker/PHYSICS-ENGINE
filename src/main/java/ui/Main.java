package ui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine();

        JFrame frame = new JFrame("2D physics engine ('r' to reset)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Panel panel = new Panel(engine);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new Thread(() -> {
            final int targetFps = 60;
            final long frameTime = 1_000_000_000L / targetFps;

            long lastTime = System.nanoTime();
            long fpsTimer = System.nanoTime();
            int frames = 0;

            while (true) {
                long now = System.nanoTime();
                if (now - lastTime >= frameTime) {
                    engine.update();
                    panel.repaint();
                    lastTime += frameTime;
                    frames++;
                }

                if (now - fpsTimer >= 1_000_000_000L) {
                    panel.setFps(frames);
                    frames = 0;
                    fpsTimer += 1_000_000_000L;
                }

                try {
                    //noinspection BusyWait
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }
}
