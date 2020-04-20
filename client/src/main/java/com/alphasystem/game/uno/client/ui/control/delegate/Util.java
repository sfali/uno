package com.alphasystem.game.uno.client.ui.control.delegate;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public final class Util {

    private static final int WIDTH = 242;
    private static final int HEIGHT = 362;

    // do not let anyone instantiate this class
    private Util() {
    }

    public static Image cropImage(Image src, int x, int y) {
        final PixelReader pixelReader = src.getPixelReader();
        return new WritableImage(pixelReader, x, y, WIDTH, HEIGHT);
    }
}
