package com.contant;

import cn.hutool.core.map.MapUtil;
import com.element.enums.MapElementType;
import lombok.experimental.UtilityClass;

import java.util.EnumMap;
import java.util.Map;

/**
 * 通行性策略
 *
 * @author zhu56
 * @date 2023/09/17 23:21
 */
@UtilityClass
public class CanGoStrategy {

    public static Map<MapElementType, Boolean> TANK_MAP = MapUtil.builder(new EnumMap<MapElementType, Boolean>(MapElementType.class))
            .put(MapElementType.TREE, true)
            .put(MapElementType.SNOW, true)
            .put(MapElementType.BRICK, false)
            .put(MapElementType.WATER, false)
            .put(MapElementType.IRON, false)
            .build();

    public static Map<MapElementType, Boolean> BULLET_MAP = MapUtil.builder(new EnumMap<MapElementType, Boolean>(MapElementType.class))
            .put(MapElementType.TREE, true)
            .put(MapElementType.SNOW, true)
            .put(MapElementType.BRICK, false)
            .put(MapElementType.WATER, true)
            .put(MapElementType.IRON, false)
            .build();

}
