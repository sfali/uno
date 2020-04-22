package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.PlayerDetail;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class GameSkin extends SkinBase<GameView> {

    private final GridPane playerViewerPane = new GridPane();
    private final PlayerView[] playerViews = new PlayerView[9];

    protected GameSkin(GameView control) {
        super(control);

        playerViewerPane.setPadding(new Insets(5, 5, 5, 5));
        playerViewerPane.setHgap(5);
        playerViewerPane.setAlignment(Pos.CENTER);

        control.selectedPositionProperty().addListener((observable, oldValue, newValue) -> {
            final int totalNodes = playerViewerPane.getChildren().size();
            final int previousActivePlayer = (int) oldValue;
            if (previousActivePlayer >= 0 && previousActivePlayer < totalNodes) {
                // make previous active player inactive
                playerViews[previousActivePlayer].setActive(false);
            }
            playerViews[(int) newValue].setActive(true);
        });
        getSkinnable().getPlayerDetails().addListener(updatePlayersView());

        final BorderPane mainPane = new BorderPane();
        mainPane.setTop(playerViewerPane);
        getChildren().addAll(mainPane);
    }

    private ListChangeListener<? super PlayerDetail> updatePlayersView() {
        return c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(
                        playerDetail -> {
                            final PlayerView playerView = createPlayerView(playerDetail);
                            playerViews[playerDetail.position()] = playerView;
                            playerViewerPane.addColumn(playerDetail.position(), playerView);
                            playerViewerPane.getColumnConstraints().add(createColumnConstraints());
                        }
                );
                c.getRemoved().forEach(
                        playerDetail -> {
                            playerViewerPane.getChildren().remove(playerDetail.position());
                            playerViewerPane.getColumnConstraints().remove(playerDetail.position());
                        }
                );
            }
        };
    }

    private PlayerView createPlayerView(PlayerDetail playerDetail) {
        final PlayerView playerView = new PlayerView();
        playerView.setName(playerDetail.name());
        return playerView;
    }

    private ColumnConstraints createColumnConstraints() {
        final ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMaxWidth(Double.MAX_VALUE);
        columnConstraints.setHgrow(Priority.ALWAYS);
        return columnConstraints;
    }
}
