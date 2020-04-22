package com.alphasystem.game.uno.client.ui.control.delegate;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

class PlayerSkin extends SkinBase<PlayerView> {

    private final Label pointsLabel = new Label();
    private final Label cardsLeftLabel = new Label();

    protected PlayerSkin(PlayerView control) {
        super(control);

        pointsLabel.setFont(Util.FONT_NORMAL);
        pointsLabel.setText("0");
        control.pointsProperty().addListener((observable, oldValue, newValue) -> pointsLabel.setText(String.valueOf(newValue)));
        cardsLeftLabel.setFont(Util.FONT_NORMAL);
        cardsLeftLabel.setText("N/A");
        control.numberOfCardsLeftProperty().addListener((observable, oldValue, newValue) -> {
            String value;
            final Integer cardsLeft = (Integer) newValue;
            if (cardsLeft < 0) value = "N/A";
            else value = String.valueOf(cardsLeft);
            cardsLeftLabel.setText(value);
        });
        getChildren().addAll(initPane());
    }

    private TitledPane initPane() {
        final TitledPane titledPane = new TitledPane();
        titledPane.setCollapsible(false);
        titledPane.setFont(Util.FONT_TITLE_INACTIVE);
        titledPane.textProperty().bind(getSkinnable().nameProperty());
        getSkinnable().activeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                titledPane.setFont(Util.FONT_TITLE_ACTIVE);
                titledPane.setTextFill(Color.RED);
            } else {
                titledPane.setFont(Util.FONT_TITLE_INACTIVE);
                titledPane.setTextFill(Color.BLACK);
            }
        });

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(createStaticLabel("Number of points:"), 0, 0);
        gridPane.add(pointsLabel, 1, 0);
        gridPane.add(createStaticLabel("Number of cards left:"), 0, 1);
        gridPane.add(cardsLeftLabel, 1, 1);
        titledPane.setContent(gridPane);

        return titledPane;
    }

    private Label createStaticLabel(String text) {
        final Label label = new Label(text);
        label.setFont(Util.FONT_BOLD);
        return label;
    }
}
