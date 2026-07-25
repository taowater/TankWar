package com.game;

import com.util.ImageUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceLoadingTest {

    @Test
    void loadsAndValidatesEveryPlayableMapFromClasspath() {
        for (int stage = 0; stage <= Game.MAX_STAGE; stage++) {
            int[][] map = Game.getMap(stage);
            assertTrue(map.length > 0);
            assertTrue(map[0].length > 0);
            for (int[] row : map) {
                assertEquals(map[0].length, row.length);
                for (int tile : row) {
                    assertTrue(tile >= 0 && tile <= 5);
                }
            }
        }
    }

    @Test
    void loadsImagesFromClasspath() {
        assertDoesNotThrow(() -> ImageUtil.getMaterial("material"));
        assertDoesNotThrow(() -> ImageUtil.getMaterial("tankmodel"));
        assertDoesNotThrow(() -> ImageUtil.getMaterial("player1"));
    }

    @Test
    void reportsMissingResourcesClearly() {
        assertThrows(IllegalArgumentException.class, () -> Game.getResource("missing/resource.bin"));
    }
}
