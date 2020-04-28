package com.alphasystem.game.uno.client.ui.control.delegate;

import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;

class ToolsSkin extends SkinBase<ToolsView> {

    private final ToolBar toolBar = new ToolBar();

    ToolsSkin(ToolsView control) {
        super(control);

        setup();
        getChildren().addAll(toolBar);
    }

    private void setup() {
        final Button startGameButton = new Button("Start Game");
        startGameButton.setDisable(true);
        startGameButton.disableProperty().bind(getSkinnable().enableStartGameButtonProperty().not());
        toolBar.getItems().add(startGameButton);
    }
}
