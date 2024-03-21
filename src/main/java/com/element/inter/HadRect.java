package com.element.inter;


import java.awt.*;
import java.util.Objects;

public interface HadRect {

    Rectangle getRect();

    default boolean isTouch(HadRect other) {
        if(Objects.isNull(other)){
            return false;
        }
        return getRect().intersects(other.getRect());
    }
}
