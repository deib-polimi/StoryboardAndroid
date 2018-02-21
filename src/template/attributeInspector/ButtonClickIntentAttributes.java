package template.attributeInspector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import template.sample.ButtonClickIntent;
import template.sample.EmptyActivity;
import template.sample.Intent;

import java.io.IOException;

/**
 * Created by utente on 07/02/2018.
 */
public class ButtonClickIntentAttributes extends IntentAttributes {

    @FXML
    private Label target_label;
    @FXML
    private Label type_label;
    @FXML
    private TextField name_txt;
    @FXML
    private TextField button_id_txt;
    @FXML
    private TextField button_txt;
    @FXML
    private ChoiceBox<String> extra_box;

    public ButtonClickIntentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("ButtonClickIntentAttributes.fxml")
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
        button_id_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            button_id_txt.setText(newValue = newValue.substring(0, 1).toLowerCase() + newValue.substring(1));
        });

    }

    public void fillValues(ButtonClickIntent intent){
        target_label.setText(intent.getBelongingLink().getTarget().getName());
        type_label.setText(intent.getType().toString());
        name_txt.setText(intent.getName());
        button_id_txt.setText(intent.getButtonId());
        button_txt.setText(intent.getButtonText());
    }

    public void createListeners(ButtonClickIntent intent){
        name_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.substring(0, 1).toLowerCase() + newValue.substring(1);
            intent.updateName(newValue);
        });
        button_id_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.substring(0, 1).toLowerCase() + newValue.substring(1);
            intent.setButtonId(newValue);
        });
        button_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            intent.setButtonText(newValue);
        });
    }
}
