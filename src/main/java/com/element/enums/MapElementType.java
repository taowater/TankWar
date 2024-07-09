package com.element.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MapElementType {

    /**
     * 树
     */
    TREE(true, true),
    /**
     *
     */
    SNOW(true, true),
    /**
     * 砖
     */
    BRICK(false, false),
    /**
     * 水
     */
    WATER(false, true),
    /**
     * 铁
     */
    IRON(false, false),
    ;

    private final boolean tankGo;
    private final boolean bulletGo;

}
