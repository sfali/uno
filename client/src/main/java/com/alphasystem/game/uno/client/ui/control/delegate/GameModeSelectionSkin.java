package com.alphasystem.game.uno.client.ui.control.delegate;

import com.alphasystem.game.uno.model.GameType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

class GameModeSelectionSkin extends SkinBase<GameModeSelectionView> {

    GameModeSelectionSkin(final GameModeSelectionView control) {
        super(control);

        control.setSelectedMode(GameType.Classic$.MODULE$);
        setup();
    }

    private void setup() {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(5, 5, 5, 5));

        final Label label = new Label("Please choose game mode.");
        label.setFont(Font.font(Util.SYSTEM_FONT_NAME, 24));
        gridPane.add(label, 0, 0, 2, 2);

        final ToggleGroup toggleGroup = new ToggleGroup();

        final RadioButton classicRB = new RadioButton(GameType.Classic$.MODULE$.entryName());
        classicRB.setOnAction(event -> getSkinnable().setSelectedMode(GameType.Classic$.MODULE$));
        classicRB.setToggleGroup(toggleGroup);
        classicRB.setSelected(true);
        gridPane.add(classicRB, 0, 2, 2, 1);

        final RadioButton progressiveRB = new RadioButton(GameType.Progressive$.MODULE$.entryName());
        progressiveRB.setOnAction(event -> getSkinnable().setSelectedMode(GameType.Progressive$.MODULE$));
        progressiveRB.setToggleGroup(toggleGroup);
        gridPane.add(progressiveRB, 0, 3, 2, 1);

        getChildren().addAll(gridPane);
    }
}
