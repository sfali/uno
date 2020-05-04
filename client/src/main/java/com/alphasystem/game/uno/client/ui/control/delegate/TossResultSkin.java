package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.response.Cards;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.controlsfx.tools.Borders;

class TossResultSkin extends SkinBase<TossResultView> {

    private final GridPane cardsPane = new GridPane();

    TossResultSkin(final TossResultView control) {
        super(control);

        cardsPane.setPadding(new Insets(5, 5, 5, 5));
        cardsPane.setHgap(5);
        cardsPane.setVgap(5);
        cardsPane.setAlignment(Pos.CENTER);

        control.getCardsRow1().addListener(updateCardsView(0));
        control.getCardsRow2().addListener(updateCardsView(1));
        control.getCardsRow3().addListener(updateCardsView(2));

        getChildren().addAll(Borders.wrap(new BorderPane(cardsPane)).lineBorder().build().build());
    }

    private ListChangeListener<? super Cards> updateCardsView(final int row) {
        return c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(cards -> addCard(cards, row));
                }

                if (c.wasRemoved()) {
                    c.getRemoved().forEach(cards -> removeCard(cards, row));
                }
            }
        };
    }

    private void addCard(final Cards cards, final int row) {
        final CardView cardView = new CardView();
        final String playerName = cards.playerName().get();
        String displayName = playerName;
        if (playerName.equals(getSkinnable().getMyPlayer().name())) {
            displayName = "You";
        }
        cardView.setPlayerName(displayName);
        cardView.setCard(cards.cards().head());
        if (getSkinnable().getWinners().contains(playerName)) {
            cardView.setSelected(true);
        }
        final int column = cardsPane.getChildren().size() % 3;
        cardsPane.add(cardView, column, row);
    }

    private void removeCard(final Cards cards, final int row) {

    }
}
