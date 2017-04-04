package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        controller.closeBtn.setOnAction(e -> {
            primaryStage.close();
        });

        controller.minimizeBtn.setOnAction(e -> {
            primaryStage.setIconified(true);
        });

        controller.minimizeBtn.setOnMouseEntered(e -> {
            controller.minimizeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        });
        controller.minimizeBtn.setOnMouseExited(e -> {
            controller.minimizeBtn.setStyle("-fx-background-color: rgba(255,255,255,.05);");
        });

        controller.closeBtn.setOnMouseEntered(e -> {
            controller.closeBtn.setStyle("-fx-background-color: -fx-red;");
        });
        controller.closeBtn.setOnMouseExited(e -> {
            controller.closeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        });

        primaryStage.setTitle("Information System");
        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
