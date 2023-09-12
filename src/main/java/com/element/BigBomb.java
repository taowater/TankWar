package com.element;

import com.element.Bomb;
import com.element.ElementOld;
import com.game.Game;
import com.util.MusicUtil;
import lombok.Data;

import java.awt.*;

@Data
public class BigBomb extends Bomb {
    public BigBomb(int x, int y) {
        super(x, y);
        this.setLife(4);
        MusicUtil.play("爆炸");
    }

    public boolean isTouch(ElementOld material) {
        return getRect().intersects(material.getRect()) && material.getIsLive();
    }

    @Override
    public void draw(Graphics g) {
        setImage(Game.getMaterial("material").getSubimage(64 + (4 - getLife()) * 32, 32, 32, 32));
        super.draw(g);
    }

}