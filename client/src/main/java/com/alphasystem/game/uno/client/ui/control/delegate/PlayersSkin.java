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
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class PlayersSkin extends SkinBase<PlayersView> {

    private final GridPane playerViewerPane = new GridPane();
    private final List<PlayerView> players = new ArrayList<>();

    protected PlayersSkin(PlayersView control) {
        super(control);

        playerViewerPane.setPadding(new Insets(5, 5, 5, 5));
        playerViewerPane.setHgap(5);
        playerViewerPane.setAlignment(Pos.CENTER);

        control.selectedPlayerProperty().addListener((observable, oldValue, newValue) -> {
            makePlayerActive(oldValue, false);
            makePlayerActive(newValue, true);
        });

        getSkinnable().getPlayerDetails().addListener(updatePlayersView());

        final BorderPane mainPane = new BorderPane();
        mainPane.setTop(playerViewerPane);
        getChildren().addAll(mainPane);
    }

    private ListChangeListener<? super PlayerDetail> updatePlayersView() {
        return c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(this::addPlayer);
                }

                if (c.wasRemoved()) {
                    c.getRemoved().forEach(this::removePlayer);
                }
            }
        };
    }

    private void addPlayer(PlayerDetail playerDetail) {
        final PlayerView playerView = createPlayerView(playerDetail);
        players.add(playerView);
        playerViewerPane.addColumn(players.size() - 1, playerView);
        playerViewerPane.getColumnConstraints().add(createColumnConstraints());
    }

    private void removePlayer(PlayerDetail playerDetail) {
        final int size = players.size();
        final int position = getPlayerPosition(playerDetail);
        if (position > -1) {
            players.remove(position);
            playerViewerPane.getChildren().remove(position);
            playerViewerPane.getColumnConstraints().remove(size - 1);
            reinitializeView();
        }
    }

    private void reinitializeView() {
        final List<PlayerDetail> playerDetails = players
                .stream()
                .map(playerView -> (PlayerDetail) playerView.getUserData())
                .collect(Collectors.toList());
        players.clear();
        final int size = playerDetails.size();
        playerViewerPane.getChildren().remove(0, size);
        playerViewerPane.getColumnConstraints().remove(0, size);
        playerDetails.forEach(this::addPlayer);
    }

    private PlayerView createPlayerView(PlayerDetail playerDetail) {
        final PlayerView playerView = new PlayerView();
        String playerName = playerDetail.name();
        playerView.setName(playerName);
        if (playerName.equals(getSkinnable().getMyPlayer().name())) {
            playerName = "You";
        }
        playerView.setDisplayName(playerName);
        playerView.setUserData(playerDetail);
        return playerView;
    }

    private ColumnConstraints createColumnConstraints() {
        final ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMaxWidth(Double.MAX_VALUE);
        columnConstraints.setHgrow(Priority.ALWAYS);
        return columnConstraints;
    }

    private void makePlayerActive(PlayerDetail playerDetail, boolean active) {
        if (playerDetail != null) {
            final Optional<PlayerView> optionalPlayerView = players
                    .stream()
                    .filter(playerView -> playerView.getName().equals(playerDetail.name()))
                    .findFirst();
            optionalPlayerView.ifPresent(playerView -> playerView.setActive(active));
        }
    }

    private int getPlayerPosition(final PlayerDetail playerDetail) {
        int position = -1;
        if (playerDetail != null) {
            final Optional<Integer> first = IntStream
                    .range(0, players.size())
                    .mapToObj(index -> new Pair<>(index, players.get(index).getName()))
                    .filter(pair -> pair.getValue().equals(playerDetail.name()))
                    .map(Pair::getKey)
                    .findFirst();
            if (first.isPresent()) position = first.get();
        }
        return position;
    }
}
