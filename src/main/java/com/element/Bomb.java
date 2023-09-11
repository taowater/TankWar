package com.element;

import com.game.Game;
import com.util.MusicUtil;

import java.awt.*;

public class Bomb extends ElementOld {

    public Bomb(int x, int y) {
        super(x, y);
        this.life = 4;
        MusicUtil.play("攻击结束");
    }

    @Override
    public void draw(Graphics g) {
        image = Game.getMaterial("material").getSubimage(2 * 32 + (4 - life) * 32, 32, 32, 32);
        g.drawImage(image, x, y, width, height, Game.getStage());
        downLife();
    }
}
