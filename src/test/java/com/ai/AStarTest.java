package com.ai;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AStarTest {

    @Test
    void findsShortestStraightPathUsingReversePathContract() {
        int[][] map = new int[5][5];

        List<Point> path = new AStar(map, 2, 1, 2, 4).search();

        assertEquals(3, path.size());
        assertEquals(new Point(2, 4), path.getFirst());
        assertEquals(new Point(2, 2), path.getLast());
    }

    @Test
    void routesAroundWallsWithAdjacentSteps() {
        int[][] map = {
                {0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0},
                {0, 0, 0, 1, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        List<Point> path = new AStar(map, 2, 0, 2, 4).search();

        assertFalse(path.isEmpty());
        Point previous = new Point(2, 0);
        for (int i = path.size() - 1; i >= 0; i--) {
            Point current = path.get(i);
            assertEquals(1, Math.abs(previous.x - current.x) + Math.abs(previous.y - current.y));
            previous = current;
        }
        assertEquals(new Point(2, 4), previous);
    }

    @Test
    void targetsReachableTileNextToOccupiedDestination() {
        int[][] map = new int[5][5];
        map[2][3] = 1;

        List<Point> path = new AStar(map, 2, 0, 2, 3).search();

        assertFalse(path.isEmpty());
        Point destination = path.getFirst();
        assertEquals(1, Math.abs(destination.x - 2) + Math.abs(destination.y - 3));
    }

    @Test
    void returnsEmptyPathWhenNoRouteExists() {
        int[][] map = {
                {0, 1, 0},
                {1, 1, 1},
                {0, 1, 0}
        };

        assertTrue(new AStar(map, 0, 0, 2, 2).search().isEmpty());
    }
}
