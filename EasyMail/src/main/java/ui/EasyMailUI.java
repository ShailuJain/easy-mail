package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EasyMailUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Easy Mail");
        FXMLLoader loader = new FXMLLoader();
//        String path = new File("EasyMailUI.fxml").getAbsolutePath();
        Parent root = loader.load(getClass().getResource("/EasyMailUI.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}