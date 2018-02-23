package template.attributeInspector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import template.sample.ButtonClickIntent;
import template.sample.CardViewActivity;
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
        button_id_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            button_id_txt.setText(newValue = newValue.substring(0, 1).toLowerCase() + newValue.substring(1));
        });

    }

    public void fillValues(ButtonClickIntent intent){
        target_label.setText(intent.getBelongingLink().getTarget().getName());
        type_label.setText(intent.getType().toString());
        button_id_txt.setText(intent.getButtonId());
        button_txt.setText(intent.getButtonText());
        ObservableList<String> boxValues = FXCollections.observableArrayList();
        boxValues.addAll("None","String","Boolean","Integer","Float","Double");
        extra_box.setItems(boxValues);
        extra_box.setValue(intent.getExtraType());
    }

    public void createListeners(ButtonClickIntent intent){
        button_id_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.substring(0, 1).toLowerCase() + newValue.substring(1);
            intent.setButtonId(newValue);
        });
        button_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            intent.setButtonText(newValue);
        });
        extra_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue()!=-1 || oldValue.intValue()==newValue.intValue()){
                    intent.setExtraType(boxValueToString(newValue.intValue()));
                    System.out.println("type: " +intent.getExtraType());
                }
            }
        });
    }

    public String boxValueToString(int value){
        switch(value){
            case 0:
                return "None";
            case 1 :
                return "String";
            case 2 :
                return "Boolean";
            case 3:
                return "Integer";
            case 4:
                return "Float";
            case 5:
                return "Double";
            default :
                return null;
        }
    }
}
