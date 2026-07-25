package com.scene;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public abstract class Scene extends JPanel implements Runnable, MouseMotionListener, MouseListener {

    private final Timer timer;

    protected Scene(){
        this(30);
    }

    protected Scene(int tickMillis) {
        super(true);
        timer = new Timer(tickMillis, event -> {
            updateScene();
            repaint();
        });
    }

    int flag = 16;
    int starttime = 8;

    void drawStart(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, this.getWidth(), this.getHeight() / 2 - (8 - starttime) * 32);
        g.fillRect(0, this.getHeight() / 2 + (8 - starttime) * 32, this.getWidth(), this.getHeight() / 2);
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    public void mouseDragged(MouseEvent arg0) {
    }

    public void mouseMoved(MouseEvent arg0) {
    }

    protected void updateScene() {
    }

    public final void startScene() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stopScene() {
        timer.stop();
    }

    @Override
    public final void run() {
        startScene();
    }
}
