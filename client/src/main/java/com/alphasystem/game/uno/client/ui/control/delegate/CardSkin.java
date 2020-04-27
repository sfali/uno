package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.Card;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import scala.Tuple2;

import static com.alphasystem.game.uno.client.ui.control.delegate.Util.cropImage;

class CardSkin extends SkinBase<CardView> {

    private static final Image BASE_IMAGE = new Image(CardSkin.class.getResource("/deck.png").toExternalForm());

    private static final Image BLANK_IMAGE = cropImage(BASE_IMAGE, 0, 1440);

    private final ImageView image = new ImageView(BLANK_IMAGE);

    protected CardSkin(final CardView control) {
        super(control);

        control.cardProperty().addListener((observable, oldValue, newValue) -> setImage(newValue));
        control.playerNameProperty().addListener((observable, oldValue, newValue) ->
                setup(control.getCard(), newValue, control.isSelected()));
        image.setOnMouseClicked(event -> {
            final String playerName = control.getPlayerName();
            // only make image selectable if player name is null
            if (playerName == null || playerName.trim().isEmpty()) {
                control.setSelected(!control.isSelected());
            }
        });
        control.selectedProperty().addListener((observable, oldValue, newValue) -> {
            final String playerName = control.getPlayerName();
            if (playerName != null && !playerName.trim().isEmpty()) {
                setup(control.getCard(), playerName, newValue);
            }
        });
        control.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            Integer height = (Integer) newValue;
            if (height == null) {
                height = 128;
            }
            image.setFitHeight(height);
        });
        image.setPreserveRatio(true);

        setup(control.getCard(), control.getPlayerName(), control.isSelected());
    }

    private void setup(Card card, String playerName, boolean selected) {
        getChildren().clear();
        setImage(card);
        final VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(image);
        if (playerName != null && !playerName.trim().isEmpty()) {
            final TitledPane titledPane = new TitledPane();
            titledPane.setTextAlignment(TextAlignment.CENTER);
            titledPane.setAlignment(Pos.CENTER);
            titledPane.setCollapsible(false);
            if (selected) {
                titledPane.setFont(Util.FONT_TITLE_ACTIVE);
                titledPane.setTextFill(Color.RED);
            } else {
                titledPane.setFont(Util.FONT_TITLE_INACTIVE);
                titledPane.setFont(Util.FONT_TITLE_INACTIVE);
                titledPane.setTextFill(Color.BLACK);
            }

            titledPane.setText(playerName);
            titledPane.textProperty().bind(getSkinnable().playerNameProperty());
            titledPane.setContent(vBox);
            getChildren().addAll(titledPane);
        } else {
            getChildren().addAll(vBox);
        }
    }

    private void setImage(Card card) {
        if (card == null) {
            image.setImage(BLANK_IMAGE);
            image.setVisible(false);
            image.setUserData(null);
        } else {
            final Tuple2<Object, Object> coordinates = card.toImageCoordinates();
            final Image value = cropImage(BASE_IMAGE, (Integer) coordinates._1(), (Integer) coordinates._2());
            image.setVisible(true);
            image.setImage(value);
            image.setUserData(card);
        }
        image.setFitHeight(getSkinnable().getFitHeight());
    }

}
