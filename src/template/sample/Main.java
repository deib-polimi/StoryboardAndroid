package template.sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    /*@FXML
    Label helloWorld;

    @FXML
    Button btn;*/

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("My Plugin");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,1150,650);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setCenter(new RootLayout());

        /*primaryStage.setTitle("Hello World");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader()); //risolve problema "java.lang.IllegalStateException: Toolkit not initialized"
        GridPane root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();*/

    }


    public static void main(String[] args) {

        launch(args);
    }

    /*@FXML
    public void initialize(){
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                helloWorld.setText("Hello World!");
            }
        });

    }*/




}
