package com.element.map;

import com.element.enums.MapElementType;
import com.game.Game;

import java.awt.*;

public class Water extends MapElement {

    int flag = 0;

    public Water(int x, int y) {
        super(x, y, MapElementType.WATER);
    }

    @Override
    public void draw(Graphics g) {
        if (isLive) {
            image = Game.getMaterial("map").getSubimage(3 * 16, (flag % 2) * 16, 16, 16);
            g.drawImage(image, x, y, 16, 16, Game.getStage());
            if (flag < 4) {
                flag++;
            } else {
                flag = 0;
            }
        }
    }
}
