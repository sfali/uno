package com.alphasystem.game.uno.client.ui.control.delegate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class ToolsView extends Control {

    private final BooleanProperty enableStartGameButton = new SimpleBooleanProperty(this, "enableStartButton", false);

    public ToolsView() {
        setSkin(createDefaultSkin());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ToolsSkin(this);
    }

    public boolean getEnableStartGameButton() {
        return enableStartGameButton.get();
    }

    public BooleanProperty enableStartGameButtonProperty() {
        return enableStartGameButton;
    }

    public void setEnableStartGameButton(boolean enableStartGameButton) {
        this.enableStartGameButton.set(enableStartGameButton);
    }
}
