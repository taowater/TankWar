package com.element.map;

public class Snow extends MapElement {

    public Snow(int x, int y) {
        super(x, y);
        type = 2;
        getImage(type);
        isSnow = true;
    }
}
