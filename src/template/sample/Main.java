package template.sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //java.net.URL p = getClass().getResource("template.sample.fxml");

        primaryStage.setTitle("Hello World");

        SampleController sampleController = new SampleController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        //loader.setController(sampleController);
        GridPane root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        /*creo controller da codice: FUNZIONANTE
        SampleController sampleController = new SampleController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("template.sample.fxml"));
        loader.setController(sampleController);
        primaryStage.hide();
        primaryStage.setScene(new Scene((GridPane) loader.load()));
        primaryStage.show();*/


        /*Originale
        Parent root = FXMLLoader.load(getClass().getResource("template.sample.fxml"));

        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();*/
    }


    public static void main(String[] args) {

        launch(args);
    }

}
