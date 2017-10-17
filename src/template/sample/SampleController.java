package template.sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

/**
 * Created by utente on 19/09/2017.
 */
public class SampleController {

    public Label helloWorld;

    public void sayHelloWorld(ActionEvent actionEvent) {
        helloWorld.setText("Hello World!");
    }
}

