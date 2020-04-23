package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.PlayerDetail;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.List;

public final class PlayersView extends Control {

    private final ObservableList<PlayerDetail> playerDetails = FXCollections.observableArrayList();
    private final ObjectProperty<PlayerDetail> myPlayer = new SimpleObjectProperty<>(this, "myPlayer", null);
    private final ObjectProperty<PlayerDetail> selectedPlayer = new SimpleObjectProperty<>(this, "selectedPlayer", null);

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
        playerDetails.add(playerDetail);
    }

    public PlayerDetail getMyPlayer() {
        return myPlayer.get();
    }

    public ObjectProperty<PlayerDetail> myPlayerProperty() {
        return myPlayer;
    }

    public void setMyPlayer(PlayerDetail myPlayer) {
        this.myPlayer.set(myPlayer);
    }

    public PlayerDetail getSelectedPlayer() {
        return selectedPlayer.get();
    }

    public ObjectProperty<PlayerDetail> selectedPlayerProperty() {
        return selectedPlayer;
    }

    public void setSelectedPlayer(PlayerDetail selectedPlayer) {
        this.selectedPlayer.set(selectedPlayer);
    }
}
