package com.alphasystem.uno.test;

import com.alphasystem.game.uno.client.ui.control.delegate.CardView;
import com.alphasystem.game.uno.model.Card;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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

        final BorderPane borderPane = new BorderPane();
        borderPane.setRight(createCardView());

        Scene scene = new Scene(borderPane);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private CardView createCardView() {
        final CardView cardView = new CardView();
        cardView.setFitHeight(256);
        cardView.setPlayerName("Player1");
        cardView.setSelected(true);
        cardView.setCard(Card.create("Red", "Six"));
        return cardView;
    }

}
