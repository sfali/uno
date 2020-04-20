package com.alphasystem.uno.test;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ImageViewMain extends Application {

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

        final Image image = new Image(getClass().getResource("/deck.png").toExternalForm());

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // Red
        gridPane.add(cropImage(image, 0, 0), 0, 0);
        gridPane.add(cropImage(image, 240, 0), 1, 0);
        gridPane.add(cropImage(image, 480, 0), 2, 0);
        gridPane.add(cropImage(image, 720, 0), 3, 0);
        gridPane.add(cropImage(image, 960, 0), 4, 0);
        gridPane.add(cropImage(image, 1200, 0), 5, 0);
        gridPane.add(cropImage(image, 1440, 0), 6, 0);
        gridPane.add(cropImage(image, 1680, 0), 7, 0);
        gridPane.add(cropImage(image, 1920, 0), 8, 0);
        gridPane.add(cropImage(image, 2160, 0), 9, 0);
        gridPane.add(cropImage(image, 2400, 0), 10, 0);
        gridPane.add(cropImage(image, 2640, 0), 11, 0);
        gridPane.add(cropImage(image, 2880, 0), 12, 0);

        // Yellow
        gridPane.add(cropImage(image, 0, 360), 0, 1);
        gridPane.add(cropImage(image, 240, 360), 1, 1);
        gridPane.add(cropImage(image, 480, 360), 2, 1);
        gridPane.add(cropImage(image, 720, 360), 3, 1);
        gridPane.add(cropImage(image, 960, 360), 4, 1);
        gridPane.add(cropImage(image, 1200, 360), 5, 1);
        gridPane.add(cropImage(image, 1440, 360), 6, 1);
        gridPane.add(cropImage(image, 1680, 360), 7, 1);
        gridPane.add(cropImage(image, 1920, 360), 8, 1);
        gridPane.add(cropImage(image, 2160, 360), 9, 1);
        gridPane.add(cropImage(image, 2400, 360), 10, 1);
        gridPane.add(cropImage(image, 2640, 360), 11, 1);
        gridPane.add(cropImage(image, 2880, 360), 12, 1);

        //Green
        gridPane.add(cropImage(image, 0, 720), 0, 2);
        gridPane.add(cropImage(image, 240, 720), 1, 2);
        gridPane.add(cropImage(image, 480, 720), 2, 2);
        gridPane.add(cropImage(image, 720, 720), 3, 2);
        gridPane.add(cropImage(image, 960, 720), 4, 2);
        gridPane.add(cropImage(image, 1200, 720), 5, 2);
        gridPane.add(cropImage(image, 1440, 720), 6, 2);
        gridPane.add(cropImage(image, 1680, 720), 7, 2);
        gridPane.add(cropImage(image, 1920, 720), 8, 2);
        gridPane.add(cropImage(image, 2160, 720), 9, 2);
        gridPane.add(cropImage(image, 2400, 720), 10, 2);
        gridPane.add(cropImage(image, 2640, 720), 11, 2);
        gridPane.add(cropImage(image, 2880, 720), 12, 2);

        //Blue
        gridPane.add(cropImage(image, 0, 1080), 0, 2);
        gridPane.add(cropImage(image, 240, 1080), 1, 2);
        gridPane.add(cropImage(image, 480, 1080), 2, 2);
        gridPane.add(cropImage(image, 720, 1080), 3, 2);
        gridPane.add(cropImage(image, 960, 1080), 4, 2);
        gridPane.add(cropImage(image, 1200, 1080), 5, 2);
        gridPane.add(cropImage(image, 1440, 1080), 6, 2);
        gridPane.add(cropImage(image, 1680, 1080), 7, 2);
        gridPane.add(cropImage(image, 1920, 1080), 8, 2);
        gridPane.add(cropImage(image, 2160, 1080), 9, 2);
        gridPane.add(cropImage(image, 2400, 1080), 10, 2);
        gridPane.add(cropImage(image, 2640, 1080), 11, 2);
        gridPane.add(cropImage(image, 2880, 1080), 12, 2);

        //Wild
        gridPane.add(cropImage(image, 3120, 360), 0, 3);
        gridPane.add(cropImage(image, 3120, 1440), 1, 3);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        Scene scene = new Scene(borderPane);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ImageView cropImage(Image src, int x, int y) {
        final PixelReader pixelReader = src.getPixelReader();
        final ImageView imageView = new ImageView(new WritableImage(pixelReader, x, y, 242, 362));
        imageView.setFitHeight(128);
        imageView.setPreserveRatio(true);
        return imageView;
    }

}
