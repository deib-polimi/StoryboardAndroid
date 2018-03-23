package template.appInterface;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import static com.intellij.vcs.log.ui.frame.ProgressStripeIcon.getHeight;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("My Plugin");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());


        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double centerX =primaryScreenBounds.getMinX() + (primaryScreenBounds.getWidth() - primaryScreenBounds.getWidth())
                * (1.0f / 2);
        double centerY = primaryScreenBounds.getMinY() + (primaryScreenBounds.getHeight() - primaryScreenBounds.getHeight())
                * (1.0f / 3);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setX(centerX);
        primaryStage.setY(centerY);
        root.setCenter(new RootLayout());



    }


    public static void main(String[] args) {

        launch(args);
    }


}
