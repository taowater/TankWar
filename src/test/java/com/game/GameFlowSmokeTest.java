package com.game;

import com.scene.Data;
import com.scene.Stage;
import com.scene.StageStart;
import com.scene.Title;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class GameFlowSmokeTest {

    @Test
    void navigatesFromTitleToPlayableStageOnEventDispatchThread() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        AtomicReference<TankWar> gameReference = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> gameReference.set(new TankWar()));
        TankWar game = gameReference.get();
        try {
            SwingUtilities.invokeAndWait(() -> {
                press(game, KeyEvent.VK_SPACE);
                assertTrue(game.title.open);

                press(game, KeyEvent.VK_ENTER);
                press(game, KeyEvent.VK_ENTER);
                assertTrue(hasComponent(game, StageStart.class));

                press(game, KeyEvent.VK_SPACE);
                assertTrue(hasComponent(game, Stage.class));
                assertTrue(hasComponent(game, Data.class));
                assertNotNull(Game.stage);
                assertEquals(1, Game.stage.getPlayers().size());
                assertTrue(Game.stage.getMap().length > 0);
            });
        } finally {
            SwingUtilities.invokeAndWait(game::dispose);
        }
    }

    private static void press(TankWar game, int keyCode) {
        game.keyPressed(new KeyEvent(game, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED));
    }

    private static boolean hasComponent(TankWar game, Class<?> type) {
        for (var component : game.getContentPane().getComponents()) {
            if (type.isInstance(component)) {
                return true;
            }
        }
        return false;
    }
}
