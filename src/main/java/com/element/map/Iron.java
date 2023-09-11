package com.element.map;

public class Iron extends MapElement {

    public Iron(int x, int y) {
        super(x, y);
        type = 5;
        getImage(type);
        isIron = true;
    }
}
