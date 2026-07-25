package com.element;

import com.element.tank.Player;
import com.game.Game;
import com.util.ImageUtil;

import java.awt.*;

public class Flash extends Element {

    private final Player master;
    public Flash(Player player) {
        super(player.getX(), player.getY());
        this.master = player;
        setLife(32);
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage32("material", getLife() % 2 * 32, 32));
        g.drawImage(getImage(), master.getX(), master.getY(), getWidth(), getHeight(), Game.getStage());
    }

    @Override
    public void update() {
        downLife();
    }
}
