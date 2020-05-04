package com.alphasystem.game.uno.client.ui.control.delegate;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class Util {

    public static final String SYSTEM_FONT_NAME = "Candara";
    public static final Font FONT_NORMAL = Font.font(SYSTEM_FONT_NAME, 12);
    public static final Font FONT_BOLD = Font.font(SYSTEM_FONT_NAME, FontWeight.BOLD, 12);
    public static final Font FONT_TITLE_INACTIVE = Font.font(SYSTEM_FONT_NAME, 16);
    public static final Font FONT_TITLE_ACTIVE = Font.font(SYSTEM_FONT_NAME, FontWeight.BOLD, 16);
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
