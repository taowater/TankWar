package com.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RangeUtil {

    public int right(int min, int num, int max) {
        if (num == max) {
            return min;
        }
        return num + 1;
    }

    public int left(int min, int num, int max) {
        if (num == min) {
            return max;
        }
        return num - 1;
    }
}
