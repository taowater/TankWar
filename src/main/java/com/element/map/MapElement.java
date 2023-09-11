package com.element.map;

import com.element.ElementOld;
import com.element.enums.MapElementType;
import com.game.Game;
import lombok.Getter;

/**
 * 地图元素
 *
 * @author zhu56
 * @date 2023/09/12 01:10
 */
public class MapElement extends ElementOld {

    @Getter
    public MapElementType mapType;

    public MapElement(int x, int y, MapElementType mapType) {
        super(x, y);
        this.width = 16;
        this.height = 16;
        this.mapType = mapType;
        getImage(mapType.ordinal() + 1);
    }

    public void getImage(int i) {
        image = Game.getMaterial("map").getSubimage((i - 1) * 16, 0, 16, 16);
    }
}

