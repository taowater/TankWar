package com.element.enums;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.base.Enums;
import com.util.RangeUtil;

public enum Direct {

    /**
     * 上
     */
    UP,
    /**
     * 右
     */
    RIGHT,
    /**
     * 下
     */
    DOWN,
    /**
     * 左
     */
    LEFT,
    /**
     * 左上
     */
    LEFT_UP,
    /**
     * 右上
     */
    RIGHT_UP,
    /**
     * 右下
     */
    RIGHT_DOWN,
    /**
     * 左下
     */
    LEFT_DOWN,
    ;

    public static Direct getR(Direct direct) {
        return EnumUtil.getEnumAt(Direct.class, RangeUtil.right(0, direct.ordinal(), Direct.LEFT.ordinal()));
    }

    public static Direct getL(Direct direct) {
        return EnumUtil.getEnumAt(Direct.class, RangeUtil.left(0, direct.ordinal(), Direct.LEFT.ordinal()));
    }

    public static Direct get(int index) {
        return EnumUtil.getEnumAt(Direct.class, index);
    }
}
