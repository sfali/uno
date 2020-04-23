package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.Card;
import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public final class CardView extends Control {

    private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);
    private final ObjectProperty<Card> card = new SimpleObjectProperty<>(this, "card", null);
    private final StringProperty playerName = new SimpleStringProperty(this, "playerName", null);
    private final IntegerProperty fitHeight = new SimpleIntegerProperty(this, "fitHeight", 128);

    public CardView() {
        this(null, null, 128);
    }

    public CardView(Card card) {
        this(card, null, 128);
    }

    public CardView(Card card, String playerName, int height) {
        setSkin(createDefaultSkin());
        setCard(card);
        setPlayerName(playerName);
        setSelected(false);
        setFitHeight(height);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CardSkin(this);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public Card getCard() {
        return card.get();
    }

    public ObjectProperty<Card> cardProperty() {
        return card;
    }

    public void setCard(Card card) {
        this.card.set(card);
    }

    public String getPlayerName() {
        return playerName.get();
    }

    public StringProperty playerNameProperty() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName.set(playerName);
    }

    public int getFitHeight() {
        return fitHeight.get();
    }

    public IntegerProperty fitHeightProperty() {
        return fitHeight;
    }

    public void setFitHeight(int fitHeight) {
        this.fitHeight.set(fitHeight);
    }
}
