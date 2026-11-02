package org.opus;


import org.opus.engine.EngineManager;
import org.opus.engine.WindowManager;
import org.opus.game.Game;

import static org.opus.utils.Consts.TITLE;

public class Launcher {

        private static WindowManager window;
        private static Game game;

         static void main(String[] args) {
                window = new WindowManager(TITLE, 1600, 900, false);
                game = new Game();
                EngineManager engine = new EngineManager();
                try {
                        engine.start();
                } catch (Exception e) {
                        e.printStackTrace();
                }

        }

        public static WindowManager getWindow() {
                return window;
        }

        public static Game getGame() {
                return game;
        }
}
