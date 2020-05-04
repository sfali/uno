package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.PlayerDetail;
import com.alphasystem.game.uno.model.response.Cards;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class TossResultView extends Control {

    private final PlayerDetail myPlayer;
    private final ObservableList<Cards> cardsRow1 = FXCollections.observableArrayList();
    private final ObservableList<Cards> cardsRow2 = FXCollections.observableArrayList();
    private final ObservableList<Cards> cardsRow3 = FXCollections.observableArrayList();
    private final ObservableList<String> winners = FXCollections.observableArrayList();

    public TossResultView(PlayerDetail myPlayer) {
        this.myPlayer = myPlayer;
        setSkin(createDefaultSkin());
    }

    PlayerDetail getMyPlayer() {
        return myPlayer;
    }

    public void reset() {
        winners.clear();
        cardsRow1.clear();
        cardsRow2.clear();
        cardsRow3.clear();
    }

    public ObservableList<Cards> getCardsRow1() {
        return cardsRow1;
    }

    public ObservableList<Cards> getCardsRow2() {
        return cardsRow2;
    }

    public ObservableList<Cards> getCardsRow3() {
        return cardsRow3;
    }

    public ObservableList<String> getWinners() {
        return winners;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TossResultSkin(this);
    }
}
