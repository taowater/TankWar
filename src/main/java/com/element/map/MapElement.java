package com.element.map;

import com.element.ElementOld;
import com.element.enums.MapElementType;
import com.util.ImageUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 地图元素
 *
 * @author zhu56
 * @date 2023/09/12 01:10
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MapElement extends ElementOld {

    private MapElementType mapType;

    public MapElement(int x, int y, MapElementType mapType) {
        super(x, y);
        this.setWidth(16);
        this.setHeight(16);
        this.mapType = mapType;
        getImage(mapType.ordinal() + 1);
    }

    public void getImage(int i) {
        setImage(ImageUtil.getSubImage16("map", (i - 1) * 16, 0));
    }
}

