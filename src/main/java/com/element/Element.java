package com.element;

import com.element.inter.Printable;
import lombok.Data;

@Data
public abstract class Element implements Printable {
    // x坐标
    private Integer x;
    // y坐标
    private Integer y;
    // 宽度
    private Integer width;
    //高度
    private Integer height;
}