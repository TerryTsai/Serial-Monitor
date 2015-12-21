package email.com.gmail.ttsai0509.serialmonitor;

import email.com.gmail.ttsai0509.serialmonitor.controller.HomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class SerialMonitor extends Application {

    public static void main(String[] args) {
        SerialMonitor.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
        loader.load();

        HomeController home = loader.getController();
        SplitPane homeRoot = loader.getRoot();

        primaryStage.setTitle("Serial Monitor");
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(homeRoot));
        primaryStage.setOnCloseRequest(event -> home.dispose());
        primaryStage.show();

    }
}
