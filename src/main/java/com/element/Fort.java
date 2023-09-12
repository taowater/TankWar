package com.element;

import com.game.Game;

import java.awt.*;

public class Fort extends ElementOld {
    public Fort(int x, int y) {
        super(x, y);
        setImage(Game.getMaterial("material").getSubimage(0, 3 * 32, 32, 32));
    }

    @Override
    public void draw(Graphics g) {
        if (!getIsLive()) {
            setImage(Game.getMaterial("material").getSubimage(32, 3 * 32, 32, 32));
        }
        g.drawImage(getImage(), getX(), getY(), 32, 32, Game.stage);
    }
}
