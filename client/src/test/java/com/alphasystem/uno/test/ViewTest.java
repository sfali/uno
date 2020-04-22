package com.alphasystem.uno.test;

import com.alphasystem.game.uno.client.ui.control.delegate.PlayerView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ViewTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Test Viewer");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        primaryStage.setWidth(bounds.getWidth() / 4);
        primaryStage.setHeight(bounds.getHeight() / 4);

        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.addColumn(0, createPlayer("Player1", 100, false));
        gridPane.addColumn(1, createPlayer("Player2", 0, false));
        gridPane.addColumn(2, createPlayer("Player3", 0, true));
        gridPane.addColumn(3, createPlayer("Player4", 0, false));
        gridPane.addColumn(4, createPlayer("Player5", 0, false));
        gridPane.addColumn(5, createPlayer("Player6", 0, false));
        gridPane.addColumn(6, createPlayer("Player7", 0, false));
        gridPane.addColumn(7, createPlayer("Player8", 0, false));
        gridPane.addColumn(8, createPlayer("Player9", 0, false));
        gridPane.getColumnConstraints().addAll(
                createColumnConstraints(),
                createColumnConstraints(),
                createColumnConstraints(),
                createColumnConstraints(),
                createColumnConstraints(),
                createColumnConstraints(),
                createColumnConstraints(),
                createColumnConstraints(),
                createColumnConstraints());

        final BorderPane borderPane = new BorderPane();
        borderPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        borderPane.setTop(gridPane);

        Scene scene = new Scene(borderPane);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private PlayerView createPlayer(String name, int points, boolean active) {
        final PlayerView playerView = new PlayerView();
        playerView.setName(name);
        playerView.setPoints(points);
        playerView.setActive(active);
        return playerView;
    }

    private ColumnConstraints createColumnConstraints() {
        final ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMaxWidth(Double.MAX_VALUE);
        columnConstraints.setHgrow(Priority.ALWAYS);
        return columnConstraints;
    }
}
