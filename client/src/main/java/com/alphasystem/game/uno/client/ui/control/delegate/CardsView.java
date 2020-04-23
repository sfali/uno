package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.Card;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Collections;
import java.util.List;

public final class CardsView extends Control {

    private final ObservableList<Card> cards = FXCollections.observableArrayList();
    private final ObjectProperty<Card> selectedCard = new SimpleObjectProperty<>(this, "selectedCard", null);

    public CardsView() {
        setSkin(createDefaultSkin());
    }

    public CardsView(CardView cardView) {
        setSkin(createDefaultSkin());
        setCards(Collections.singletonList(cardView.getCard()));
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CardsSkin(this);
    }

    public ObservableList<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        cards.forEach(card -> getCards().add(card));
    }

    public void removeCards() {
        cards.remove(0, getCards().size());
    }

    public Card getSelectedCard() {
        return selectedCard.get();
    }

    public ObjectProperty<Card> selectedCardProperty() {
        return selectedCard;
    }

    public void setSelectedCard(Card selectedCard) {
        this.selectedCard.set(selectedCard);
    }

    public boolean isIllegalMove() {
        return false;
    }
}
