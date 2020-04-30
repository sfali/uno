package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.Card;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.List;

class CardsSkin extends SkinBase<CardsView> {

    private final AnchorPane anchorPane = new AnchorPane();
    private final ObservableList<CardView> cardViews = FXCollections.observableArrayList();
    private CardView selectedCard = null;

    CardsSkin(CardsView control) {
        super(control);

        cardViews.addListener((ListChangeListener<? super CardView>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(cardView -> anchorPane.getChildren().remove(cardView));
                } else if (c.wasAdded()) {
                    initializePane(c.getAddedSubList());
                }
            }
        });

        control.getCards().addListener(this::onChanged);

        final HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(anchorPane);
        getChildren().add(hBox);
    }

    private void onChanged(ListChangeListener.Change<? extends Card> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(card -> cardViews.add(new CardView(card)));
                reInitializePane();
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(deckCard -> cardViews.removeIf(cardView -> cardView.getCard().equals(deckCard)));
                reInitializePane();
            } else {
                System.err.println("Unhandled");
            }
        }
    }

    private double anchor = 0.0;

    private void initializePane(List<? extends CardView> cardViews) {
        anchorPane.getChildren().addAll(cardViews);
        cardViews.forEach(cardView -> {
            AnchorPane.setTopAnchor(cardView, 20.0);
            AnchorPane.setLeftAnchor(cardView, anchor);
            cardView.selectedProperty().addListener((o, ov, nv) -> {
                if (nv) {
                    if (selectedCard != null) {
                        selectedCard.setSelected(false);
                    }
                    getSkinnable().setSelectedCard(cardView.getCard());
                    selectedCard = cardView;
                    AnchorPane.setTopAnchor(cardView, 0.0);
                } else {
                    getSkinnable().setSelectedCard(null);
                    selectedCard = null;
                    AnchorPane.setTopAnchor(cardView, 20.0);
                }
            });
            cardView.setOnDragDetected(event -> {
                if (getSkinnable().isIllegalMove()) {
                    // TODO: show notification
                } else {
                    final Card card = cardView.getCard();
                    getSkinnable().setSelectedCard(card);
                    final Dragboard dragboard = cardView.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(String.format("%s=%s", card.color(), card.card()));
                    dragboard.setContent(content);
                }
                event.consume();
            });
            anchor += 50.0;
        });
    }

    private void reInitializePane() {
        anchorPane.getChildren().clear();
        anchor = 0.0;
        initializePane(cardViews);
        anchorPane.setDisable(false);
    }
}
