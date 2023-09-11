package com.element.map;

import com.element.ElementOld;
import com.element.enums.MapElementType;
import com.game.Game;
import lombok.Getter;

import java.awt.*;

public class MapElement extends ElementOld {

    @Getter
    public MapElementType mapType;
    public MapElement(int x, int y,MapElementType mapType) {
        super(x, y);
        this.width = 16;
        this.height = 16;
        this.mapType = mapType;
        getImage(mapType.ordinal() + 1);
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

