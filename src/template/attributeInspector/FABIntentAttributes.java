package template.attributeInspector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import template.sample.FABIntent;

import java.io.IOException;

/**
 * Created by utente on 11/02/2018.
 */
public class FABIntentAttributes extends IntentAttributes {
    @FXML
    private Label target_label;
    @FXML
    private Label type_label;
    @FXML
    private TextField name_txt;

    public FABIntentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("FABIntentAttributes.fxml")
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
        name_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            name_txt.setText(newValue = newValue.substring(0, 1).toLowerCase() + newValue.substring(1));
        });

    }

    public void fillValues(FABIntent intent){
        target_label.setText(intent.getBelongingLink().getTarget().getName());
        type_label.setText(intent.getType().toString());
        name_txt.setText(intent.getName());
    }

    public void createListeners(FABIntent intent){
        name_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.substring(0, 1).toLowerCase() + newValue.substring(1);
            intent.updateName(newValue);
        });

    }
}
