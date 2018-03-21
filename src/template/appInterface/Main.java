package template.appInterface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("My Plugin");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,1150,650);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setCenter(new RootLayout());


    }


    public static void main(String[] args) {

        launch(args);
    }


}
