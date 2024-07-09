package com.scene;

import com.element.map.Brick;
import com.game.Game;
import com.game.TankWar;
import com.taowater.ztream.Ztream;
import com.util.ImageUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Zhu_wuliu
 */
public class Title extends Scene {

    private final int MENU_INDEX = 256;
    public boolean open = false;
    public int index = 0;
    public List<Brick> logo2;
    public List<Brick> logoRemove = new ArrayList<>();
    long startTime = 0;
    TankWar tankWar;
    List<Brick> logo;
    private String[] menu;
    private BufferedImage indexItem = null;
    private boolean flag = false;
    private boolean pressFlag = true;
    private BufferedImage tankImage;

    public Title(TankWar tankWar) {
        this.tankWar = tankWar;
        this.menu = Game.title_menu;
        this.logo = Game.getLogo(1);
        this.logo2 = Game.getLogo(2);
        setBounds(0, 0, TankWar.WIDTH, TankWar.HEIGHT);
        tankImage = ImageUtil.getMaterial("player1");
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (open) {
            drawBackground(0, 0, g);
        }

        Ztream.of(logo).append(logo2).append(logoRemove).forEach(e -> e.draw(g, this));

        Ztream.of(logoRemove).forEach(e -> {
            if (e.getY() > 0 && e.getX() < TankWar.WIDTH) {
                e.setY(e.getY() - Game.rand(10));
                e.setX(e.getX() + Game.rand(24));
            } else {
                e.setIsLive(false);
            }
        });
        logoRemove.removeIf(e -> !e.getIsLive());
        if (!open) {
            drawPressAnyKey(g);
        }
    }

    private void drawPressAnyKey(Graphics g) {
        if (pressFlag) {
            Game.drawText("PRESS ANYKEY", (TankWar.WIDTH - 14 * 16) / 2, 300, 2, g, this);
        }
        if (System.currentTimeMillis() - startTime > 500) {
            pressFlag = !pressFlag;
            startTime = System.currentTimeMillis();
        }
    }

    private void drawBackground(int x, int y, Graphics g) {
        g.setColor(Color.black);
        g.fillRect(x, y, TankWar.WIDTH, TankWar.HEIGHT);
        drawMenu(x, y, g);
        drawIndexItem(x, y, g);
        Game.drawText("* 2019 CODE BY ZHU 56", x + 64 + 32, y + 264 + 32 * 4, 2, g, this);
        Game.drawText("* JUST FOR FUN", x + 64 + 64 + 16, y + 264 + 32 * 5, 2, g, this);
    }

    private void drawMenu(int x, int y, Graphics g) {
        Ztream.of(menu).forEach((e, i) -> Game.drawText(e, x + 196, y + MENU_INDEX + 8 + i * 32, 2, g, this));
    }

    private void drawIndexItem(int x, int y, Graphics g) {
        flag = !flag;
        indexItem = tankImage.getSubimage(flag ? 28 : 0, 28, 28, 28);
        g.drawImage(indexItem, x + 160, y + MENU_INDEX + 32 * index, 28, 28, this);
    }

    private void upIndex() {
        index = Math.max(0, index - 1);
    }

    private void downIndex() {
        index = Math.min(index + 1, menu.length - 1);
    }

    public void toSelect() {
        this.index = 0;
        this.menu = Game.title_menu2;
    }

    public void keyPressed(KeyEvent e) {

        if (!open) {
            open = true;
            logo2.clear();
            logoRemove.clear();
            return;
        }
        if (open) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                upIndex();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downIndex();
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (menu == Game.title_menu) {
                    switch (index) {
                        case 0 -> toSelect();
                        case 1 -> tankWar.toEditor();
                        default -> {
                        }
                    }
                } else if (menu == Game.title_menu2) {
                    Game.player_number = index + 1;
                    tankWar.toGameStart();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && menu == Game.title_menu2) {
                this.menu = Game.title_menu;
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (Game.rand(5) < 2 && logo2.size() > 1) {
                Collections.shuffle(logo2);
                Brick brick = logo2.get(0);
                logo2.remove(brick);
                Brick brick2 = logo2.get(logo2.size() - 1);
                logo2.remove(brick2);

                logoRemove.add(brick);
                logoRemove.add(brick2);
            }
            repaint();
            Game.Sleep(30);
        }
    }
}