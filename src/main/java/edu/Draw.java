package edu;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;

public class Draw extends JPanel implements ActionListener {
    private final int windowWidth;
    private final int windowHeight;
    private final int floorY;
    private final int wallX;

    //Mass
    private final double m1 = 1;
    private final double m2 = m1 * Math.pow(10, Constants.N);

    //Velocity
    private final AtomicReference<Double> v1 = new AtomicReference<>((double) 0);
    private final AtomicReference<Double> v2 = new AtomicReference<>(-1 / Math.pow(10, 6));

    //Position
    private final AtomicReference<Double> x1;
    private final AtomicReference<Double> x2;

    //Rectangles
    private Rectangle2D smallBox, bigBox;
    private final int smallSize;
    private final int bigSize;

    private final AtomicLong countCollisions = new AtomicLong(0);

    public Draw(int windowWidth, int windowHeight) {
        int offset = windowWidth / 10;

        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.floorY = windowHeight - offset;
        this.wallX = offset;

        this.smallSize = offset / 2;
        this.bigSize = 3 * this.smallSize;

        this.x1 = new AtomicReference<>((double) wallX + 2 * offset);
        this.x2 = new AtomicReference<>(this.x1.get() + this.smallSize + offset);

        Timer timer = new Timer(10, this);
        timer.start();

        thread = new Thread(
                () -> {
                    while (true) {
                        if (x1.get() <= wallX) {
                            wallCollision();
                        }

                        if (x1.get() + smallSize > x2.get()) {
                            x1.set(x2.get() - smallSize);
                            rectangleCollision();
                        }

                        update();
                    }
                }
        );
    }


    private final Thread thread;
    private boolean started = false;

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setBackground(Constants.BACKGROUND_COLOR);
        g2d.clearRect(0, 0, windowWidth, windowHeight);

        //Floor and Wall
        g2d.setColor(Constants.PRIMARY_COLOR);
        g2d.drawLine(0, floorY, windowWidth, floorY);
        g2d.drawLine(wallX, 0, wallX, windowHeight);

        if (!started) {
            thread.start();
            started = true;
        }

        if (x1.get() < windowWidth) {
            smallBox = new Rectangle2D.Double(x1.get(), floorY - smallSize, smallSize, smallSize);
            bigBox = new Rectangle2D.Double(x2.get(), floorY - bigSize, bigSize, bigSize);

            g2d.setColor(Constants.RECTANGLE_COLOR);
            drawBoxes(g2d);
        }

        drawText(g2d);
    }

    private void update() {
        if (x1.get() + v1.get() <= wallX) {
            x1.set((double) wallX);
        } else {
            x1.set(x1.get() + v1.get());
        }

        if (x2.get() + v2.get() <= wallX + smallSize) {
            x2.set((double) (wallX + smallSize));
        } else {
            // Here is jump to answer can be performed
            // if (v2.get() > 0 && v2.get() < Math.abs(v1.get())) {
            //     return;
            // }
            x2.set(x2.get() + v2.get());
        }
    }

    private void wallCollision() {
        v1.set(-1 * v1.get());
        countCollisions.incrementAndGet();
    }

    private void rectangleCollision() {
        double u1 = v1.get();
        double u2 = v2.get();

        v1.set(((m1 - m2) / (m1 + m2)) * u1 + ((2 * m2) / (m1 + m2)) * u2);
        v2.set(((2 * m1) / (m1 + m2)) * u1 + ((m2 - m1) / (m1 + m2)) * u2);

        countCollisions.incrementAndGet();
    }

    private void drawBoxes(Graphics2D g2d) {
        g2d.fill(smallBox);
        g2d.fill(bigBox);
    }

    private void drawText(Graphics2D g2d) {
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.setFont(new Font("Default", Font.BOLD, Constants.TEXT_SIZE));
        int textX = (int) (wallX * 1.1);
        g2d.drawString(Long.toString(countCollisions.get()), textX, 2 * Constants.TEXT_SIZE);
    }

    @Override
    public void actionPerformed(ActionEvent arg) {
        repaint();
    }
}