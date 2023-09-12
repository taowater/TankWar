package com.element;

import com.game.Game;
import com.util.MusicUtil;

import java.awt.*;

public class Bomb extends ElementOld {

    public Bomb(int x, int y) {
        super(x, y);
        this.setLife(4);
        MusicUtil.play("攻击结束");
    }

    @Override
    public void draw(Graphics g) {
        setImage(Game.getMaterial("material").getSubimage(2 * 32 + (4 - getLife()) * 32, 32, 32, 32));
        g.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), Game.getStage());
        downLife();
    }
}
