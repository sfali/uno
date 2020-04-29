package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.GameType;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class GameModeSelectionView extends Control {

    private final ReadOnlyObjectWrapper<GameType> selectedMode = new ReadOnlyObjectWrapper<>(this, "selectedMode", null);

    public GameModeSelectionView() {
        setSkin(createDefaultSkin());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new GameModeSelectionSkin(this);
    }

    public GameType getSelectedMode() {
        return selectedMode.get();
    }

    public ReadOnlyObjectProperty<GameType> selectedModeProperty() {
        return selectedMode.getReadOnlyProperty();
    }

    void setSelectedMode(GameType selectedMode) {
        this.selectedMode.set(selectedMode);
    }
}
