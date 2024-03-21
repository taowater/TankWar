package com.element;

import com.game.Game;
import com.util.ImageUtil;

import java.awt.*;

public class Fort extends Element {
    public Fort(int x, int y) {
        super(x, y);
        setImage(ImageUtil.getSubImage32("material", 0, 3 * 32));
    }

    @Override
    public void draw(Graphics g) {
        if (!getIsLive()) {
            setImage(ImageUtil.getSubImage32("material", 32, 3 * 32));
        }
        g.drawImage(getImage(), getX(), getY(), 32, 32, Game.stage);
    }
}
