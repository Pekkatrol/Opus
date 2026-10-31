package org.opus;


import org.lwjgl.Version;
import org.opus.core.WindowManager;

public class Launcher {

    static void main(String[] args) {
            System.out.println(Version.getVersion());

            WindowManager window = new WindowManager("Opus", 1600, 900, false);
            window.init();

            while (!window.windowShouldClose()) {
                    window.update();
            }

            window.cleanup();
    }
}
