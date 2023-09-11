package com.element.map;

public class Tree extends MapElement {

    public Tree(int x, int y) {
        super(x, y);
        type = 1;
        getImage(type);
        isTree = true;
    }
}
