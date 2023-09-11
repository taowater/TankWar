package com.scene;

import com.element.map.MapElement;
import com.game.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MapEditor extends Scene {

    private int index;
    private int Mouse_x;
    private int Mouse_y;
    private ArrayList<MapElement> map = new ArrayList<>();
    private int[][] map_temp;

    public MapEditor(int[][] map) {
        setBounds(0, 0, 16 * map[0].length, 16 * map.length);
        index = 2;
        this.map_temp = map;
        this.setLayout(null);
        this.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int i = (e.getWheelRotation() < 0) ? -1 : 1;
                index += i;
                if (index < 0) {
                    index = 4;
                } else if (index > 4) {
                    index = 0;
                }
                repaint();
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.gray);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getWidth());
        drawMap(g);
        drawMouse(index, g);
    }

    private void drawMouse(int index, Graphics g) {
        BufferedImage image = Game.getMaterial("map");
        BufferedImage mouse = image.getSubimage(index * 16, 0, 16, 16);
        g.drawImage(mouse, Mouse_x, Mouse_y, 16, 16, this);
    }

    private void drawMap(Graphics g) {
        int length = map.size();
        for (int i = 0; i < length; i++) {
            MapElement mapElement = map.get(i);
            mapElement.draw(g, this);
            map_temp[mapElement.y / 16][mapElement.x / 16] = mapElement.type;
        }
    }

    private MapElement getMapElement(int X, int Y) {
        MapElement mapElement_temp = null;
        int length = map.size();
        for (int i = 0; i < length; i++) {
            MapElement mapElement = map.get(i);
            if (mapElement.x == X && mapElement.y == Y) {
                mapElement_temp = mapElement;
            }
        }
        return mapElement_temp;
    }

    private void setXandY(MouseEvent e) {
        this.Mouse_x = e.getX() - 8;
        this.Mouse_y = e.getY() - 8;
    }

    private void creatMapElement(int X, int Y) {
        MapElement mapElement_tamp = getMapElement(X, Y);
        if (mapElement_tamp != null) {
            map.remove(mapElement_tamp);
        }
        MapElement element = Game.creatMapElement(index + 1, X, Y);
        map_temp[Y / 16][X / 16] = element.type;
        map.add(element);
        repaint();
    }

    public void mouseClicked(MouseEvent arg0) {

    }

    public void mouseEntered(MouseEvent e) {
//        Toolkit tk= Toolkit.getDefaultToolkit();
//        Image img=tk.getImage("");
//        Cursor cu=tk.createCustomCursor(img,new Point(10,10)," ");
//        this.setCursor(cu);
    }

    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent e) {

        setXandY(e);
        int X = Mouse_x / 16 * 16;
        int Y = Mouse_y / 16 * 16;
        creatMapElement(X, Y);
    }

    public void mouseReleased(MouseEvent arg0) {

    }

    public void mouseDragged(MouseEvent e) {
        setXandY(e);
        int X = Mouse_x / 16 * 16;
        int Y = Mouse_y / 16 * 16;
        creatMapElement(X, Y);
    }

    public void KeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Game.edited = true;
            Game.editon_map = map_temp;
            Game.tankWar.toTitle(true);

        }
    }


    public void mouseMoved(MouseEvent e) {
        setXandY(e);
        repaint();
    }

    public void run() {
        Game.Sleep(30);
        repaint();
    }
}
