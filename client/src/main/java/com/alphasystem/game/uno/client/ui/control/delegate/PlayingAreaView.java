package com.alphasystem.game.uno.client.ui.control.delegate;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class PlayingAreaView extends Control {

    public PlayingAreaView() {
        setSkin(createDefaultSkin());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PlayingAreaSkin(this);
    }
}
