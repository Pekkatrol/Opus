package org.opus;


import org.lwjgl.Version;
import org.opus.core.EngineManager;
import org.opus.core.WindowManager;
import org.opus.test.TestGame;

import static org.opus.core.utils.Consts.TITLE;

public class Launcher {

        private static WindowManager window;
        private static TestGame game;

         static void main(String[] args) {
                window = new WindowManager(TITLE, 1600, 900, false);
                game = new TestGame();
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

        public static TestGame getGame() {
                return game;
        }
}
