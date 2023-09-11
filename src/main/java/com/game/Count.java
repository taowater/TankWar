package com.game;

import com.scene.Scene;
import com.scene.Stage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

public class Count extends Scene {
    private int[] level;
    private int[] level2;
    private final int[] lev = {0, 0, 0, 0};
    private final int[] lev2 = {0, 0, 0, 0};
    private int index = 0;
    private int sum;
    private int sum2;
    private int sum_mask;
    private int sum_mask2;
    private int x;

    public Count() {
        setBounds(0, 0, TankWar.WIDTH, TankWar.HEIGHT);
        this.level = Stage.CountData.level;
        this.level2 = Stage.CountData.level2;
        sum_mask = level[0] * 100 + level[1] * 200 + level[2] * 300 + level[3] * 500;
        sum_mask2 = level2[0] * 100 + level2[1] * 200 + level2[2] * 300 + level2[3] * 500;
        sum = this.level[0] + this.level[1] + this.level[2] + this.level[3];
        sum2 = this.level2[0] + this.level2[1] + this.level2[2] + this.level2[3];
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        drawBasis(g);
    }

    private void drawBasis(Graphics g) {
        Game.drawText("HI SCORE", 128, 32, 4, g, this);
        Game.drawText("20000", 256 + 64, 32, 3, g, this);
        Game.drawText("STAGE " + Game.stagesth, (this.getWidth() - 9 * 16) / 2, 32 * 2, 2, g, this);
        Game.drawText("1PLAYER", 48, 32 * 3, 4, g, this);
        if (Game.player_number > 1) {
            Game.drawText("2PLAYER", this.getWidth() - 64 - 16 * 9, 32 * 3, 4, g, this);
            Game.drawText("TOTAL", this.getWidth() - 128 - 16 * 4, 32 * 9 + 16 * 4, 2, g, this);
            Game.drawText(sum_mask2 + "", this.getWidth() - 64 - 16 * 10, 32 * 4, 2, g, this);
        }
        ImageIcon tankmodel = new ImageIcon("image/tankmodel.png");
        g.drawImage(tankmodel.getImage(), (this.getWidth() - 26) / 2, 32 * 5 + 16 - 10, 26, 174, this);
        for (int i = 0; i < 4; i++) {
            Game.drawText("PTS", 112, 32 * (5 + i) + 16 * (i + 1), 2, g, this);
            if ((lev[i] + "").length() < 2) {
                x = 16;
            } else {
                x = 0;
            }
            Game.drawText(lev[i] + "", 128 + 48 + x, 32 * (5 + i) + 16 * (i + 1), 2, g, this);
            Game.drawText("<", 128 + 64 + 16 + 8, 32 * (5 + i) + 16 * (i + 1), 2, g, this);
            if (Game.player_number > 1) {
                Game.drawText(lev2[i] + "", 128 + 48 + 96 + x, 32 * (5 + i) + 16 * (i + 1), 2, g, this);
                Game.drawText(">", 128 + 64 + 32 * 3 - 16 - 8, 32 * (5 + i) + 16 * (i + 1), 2, g, this);
                Game.drawText("PTS", this.getWidth() - 128 - 16 * 4, 32 * (5 + i) + 16 * (i + 1), 2, g, this);
            }
        }
        Game.drawText("_________", 128 + 48, 32 * 8 + 16 * 5, 2, g, this);
        Game.drawText("TOTAL", 128 - 48, 32 * 9 + 16 * 4, 2, g, this);
        Game.drawText(sum_mask + "", 112 - 16 + 64, 32 * 4, 2, g, this);
        if(index==3){
            if (lev[index] == level[index]) {
                Game.drawText(sum + "", 128 + 48 + x, 32 * 9 + 16 * 4, 2, g, this);
                repaint();
            }
            if(Game.stage.players.size()>1){
                if (lev2[index] == level2[index]) {
                    Game.drawText(sum2 + "", 128 + 48 + 96 + x, 32 * 9 + 16 * 4, 2, g, this);
                    repaint();
                }
            }
        }
    }

    public void KeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!Game.fail) {
                if (Game.stagesth < 6) {
                    Game.stagesth++;
                } else {
                    Game.stagesth = 1;
                }
                Game.tankWar.toGameStart();
            } else {
                Game.GameInit();
                Game.tankWar.toTitle(true);
            }
        }
    }

    public void run() {
        while (true) {
            Game.Sleep(30);
            if (lev[index] < level[index]) {
                lev[index]++;
            } else if (lev2[index] < level2[index]) {
                lev2[index]++;
            } else if (index < 3) {
                index++;
            }
            repaint();
        }
    }
}
