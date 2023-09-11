package com.game;

import com.element.Player;
import com.scene.Scene;
import com.scene.Stage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Data extends Scene {

    private final Stage stage;

    public Data(Stage stage) {
        this.stage = stage;
        setBounds(32 + stage.getWidth(), 32, 32 * 2, 32 * 13);
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.gray);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        drawData(g);
    }

    private void drawData(Graphics g) {
        BufferedImage enemyIcon = Game.materialImage.getSubimage(144, 112, 16, 16);
        g.drawImage(enemyIcon, 16, 16, 16, 16, this);
        Game.drawText(stage.enumber + "", 16, 16, 0, g, this);
//		for (int i = 0; i < stage.enumber; i++) {
//			g.drawImage(enemyIcon, 16 + i % 2 * 16, 16 + i / 2 * 16, 16, 16, this);
//		}
        for (int i = 0; i < Game.player_number; i++) {
            Player player = Game.stage.players.get(i);
            drawPlayer(player, g, i);
        }

        BufferedImage qizhi = Game.materialImage.getSubimage(192, 64, 32, 32);
        g.drawImage(qizhi, 16, 32 * 11 - 16, 32, 32, this);
        Game.drawText(Game.stagesth + "", 32, 32 * 11 + 16, 0, g, this);
    }

    private void drawPlayer(Player player, Graphics g, int i) {
        BufferedImage hero = Game.materialImage.getSubimage(160 + i * 32, 112, 32, 16);
        BufferedImage tank = Game.materialImage.getSubimage(160, 128, 15, 16);
        BufferedImage bullet = Game.getMaterial("bullet").getSubimage(5, 4, 6, 8);

        g.drawImage(hero, 16, 72 + 128 * i, 32, 16, this);
        g.drawImage(tank, 16, 72 + 128 * i + 32, 15, 16, this);
        Game.drawText(player.maxlife + "", 16, 72 + 128 * i + 32, 0, g, this);
        g.drawImage(bullet, 16 + 2, 72 + 128 * i + 64, 12, 16, this);
        Game.drawText(player.bulletType + 1 + "", 32, 72 + 128 * i + 64 + 2, 0, g, this);
    }

    public void run() {
        while (true) {
            repaint();
            Game.Sleep(50);
        }
    }
}
