package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.PlayerDetail;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.List;

public final class PlayersView extends Control {

    private final ObservableList<PlayerDetail> playerDetails = FXCollections.observableArrayList();
    private final IntegerProperty selectedPosition = new SimpleIntegerProperty(this, "selectedPosition", -1);

    public PlayersView() {
        setSkin(createDefaultSkin());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PlayersSkin(this);
    }

    public ObservableList<PlayerDetail> getPlayerDetails() {
        return playerDetails;
    }

    public void setPlayerDetails(List<PlayerDetail> playerDetails) {
        playerDetails.forEach(playerDetail -> getPlayerDetails().add(playerDetail));
    }

    public void addPlayer(PlayerDetail playerDetail) {
        playerDetails.add(playerDetail.position(), playerDetail);
    }

    public int getSelectedPosition() {
        return selectedPosition.get();
    }

    public IntegerProperty selectedPositionProperty() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition.set(selectedPosition);
    }
}
