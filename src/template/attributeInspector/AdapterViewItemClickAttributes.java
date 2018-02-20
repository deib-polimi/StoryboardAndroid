package template.attributeInspector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import template.sample.AdapterViewItemClick;
import template.sample.ButtonClickIntent;

import java.io.IOException;

/**
 * Created by utente on 20/02/2018.
 */
public class AdapterViewItemClickAttributes extends IntentAttributes {

    @FXML
    private Label type_label;

    public AdapterViewItemClickAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("AdapterViewItemClickAttributes.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {

    }

    public void fillValues(AdapterViewItemClick intent){

        type_label.setText(intent.getType().toString());

    }

    public void createListeners(AdapterViewItemClick intent){

    }
}
