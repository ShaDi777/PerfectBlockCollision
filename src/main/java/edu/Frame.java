package edu;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    public Frame() {
        setTitle("Pi Collisions");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT,
                (int) (Constants.WINDOW_WIDTH * 0.9), (int) (Constants.WINDOW_HEIGHT * 1.1));

        JPanel jPanel = new Draw(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setContentPane(jPanel);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Frame frame = new Frame();
            frame.setVisible(true);
        });
    }
}