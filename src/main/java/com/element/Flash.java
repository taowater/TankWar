package com.element;

import com.element.tank.Player;
import com.game.Game;
import com.util.ImageUtil;

import java.awt.*;

public class Flash extends ElementOld {

    private final Player master;
    public int life;

    public Flash(Player player) {
        super(player.getX(), player.getY());
        this.master = player;
        this.life = 32;
    }

    @Override
    public void draw(Graphics g) {
        setImage(ImageUtil.getSubImage32("material", life % 2 * 32, 32));
        g.drawImage(getImage(), master.getX(), master.getY(), getWidth(), getHeight(), Game.getStage());
        downLife();
    }
}
