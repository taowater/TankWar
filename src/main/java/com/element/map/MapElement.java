package com.element.map;

import com.element.ElementOld;
import com.element.enums.MapElementType;
import com.game.Game;

import java.awt.*;

public class MapElement extends ElementOld {

    public boolean isTree = false;
    public boolean isSnow = false;
    public boolean isBrick = false;
    public boolean isWater = false;
    public boolean isIron = false;
    public MapElementType type2 = MapElementType.BRICK;
    public int type = 1;
    public MapElement(int x, int y) {
        super(x, y);
        this.width = 16;
        this.height = 16;
    }

    public void getImage(int i) {
        image = Game.getMaterial("map").getSubimage((i - 1) * 16, 0, 16, 16);
    }

    @Override
    public void draw(Graphics g){
        if(isLive){
            super.draw(g);
        }else{
            Game.stage.elements.remove(this);
        }

    }
}

