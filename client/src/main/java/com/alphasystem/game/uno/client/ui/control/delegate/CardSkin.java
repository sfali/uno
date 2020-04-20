package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.Card;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import scala.Tuple2;

import static com.alphasystem.game.uno.client.ui.control.delegate.Util.cropImage;

class CardSkin extends SkinBase<CardView> {

    private static final Image BASE_IMAGE = new Image(CardSkin.class.getResource("/deck.png").toExternalForm());

    private static final Image BLANK_IMAGE = cropImage(BASE_IMAGE, 0, 1440);

    private final GridPane pane = new GridPane();
    private final ImageView image = new ImageView(BLANK_IMAGE);
    private final Label label = new Label();

    protected CardSkin(CardView control) {
        super(control);
        pane.setAlignment(Pos.TOP_CENTER);

        control.cardProperty().addListener((observable, oldValue, newValue) -> setImage(newValue));
        image.setOnMouseClicked(event -> control.setSelected(!control.isSelected()));
        control.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            Integer height = (Integer) newValue;
            if (height == null || height < 128) {
                height = 128;
            }
            image.setFitHeight(height);
        });
        control.playerNameProperty().addListener((observable, oldValue, newValue) -> setPlayerName(newValue));
        image.setPreserveRatio(true);

        pane.add(image, 0, 0);
        setPlayerName(control.getPlayerName());

        getChildren().addAll(pane);
    }

    private void setImage(Card newValue) {
        if (newValue == null) {
            image.setImage(BLANK_IMAGE);
        } else {
            final Tuple2<Object, Object> coordinates = newValue.toImageCoordinates();
            final Image value = cropImage(BASE_IMAGE, (Integer) coordinates._1(), (Integer) coordinates._2());
            image.setImage(value);
            image.setUserData(newValue);
        }
        image.setFitHeight(getSkinnable().getFitHeight());
    }

    private void setPlayerName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            pane.getChildren().remove(label);
        } else {
            label.setText(playerName);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setFont(Font.font("Georgia", 24));
            GridPane.setHalignment(label, HPos.CENTER);
            pane.add(label, 0, 1);
        }
    }


}
