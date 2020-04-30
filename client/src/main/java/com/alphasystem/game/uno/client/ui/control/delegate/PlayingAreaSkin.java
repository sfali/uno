package com.alphasystem.game.uno.client.ui.control.delegate;

import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.controlsfx.tools.Borders;

class PlayingAreaSkin extends SkinBase<PlayingAreaView> {

    PlayingAreaSkin(PlayingAreaView control) {
        super(control);

        final BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(createText());
        getChildren().addAll(Borders.wrap(borderPane).lineBorder().build().build());
    }

    private Text createText() {
        InnerShadow is = new InnerShadow();
        is.setOffsetX(4.0f);
        is.setOffsetY(4.0f);

        Text t = new Text();
        t.setEffect(is);
        t.setText("Welcome to UNO");
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFill(Color.GREENYELLOW);
        t.setFont(Font.font(null, FontWeight.BOLD, 80));

        return t;
    }
}
