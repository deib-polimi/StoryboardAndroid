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
import template.intent.ButtonClickWithResultIntent;

import java.io.IOException;

public class ButtonClickResultAttributes extends IntentAttributes{
    @FXML
    private Label type_label;
    @FXML
    private ChoiceBox<String> extra_box;
    @FXML
    private Label requested_label;
    @FXML
    private TextField button_txt;
    @FXML
    private TextField button_id_txt;

    public ButtonClickResultAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("ButtonClickResultAttributes.fxml")
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

    public void fillValues(ButtonClickWithResultIntent intent){
        requested_label.setText(intent.getBelongingLink().getSource().getName());
        type_label.setText(intent.getType().toString());
        button_id_txt.setText(intent.getResultButtonId());
        button_txt.setText(intent.getResultButtonText());
        ObservableList<String> boxValues = FXCollections.observableArrayList();
        boxValues.addAll("None","String","Boolean","Integer","Float","Double");
        extra_box.setItems(boxValues);
        extra_box.setValue(intent.getResultExtra());
    }

    public void createListeners(ButtonClickWithResultIntent intent){
        button_id_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.substring(0, 1).toLowerCase() + newValue.substring(1);
            intent.setResultButtonId(newValue);
        });
        button_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            intent.setResultButtonText(newValue);
        });

        extra_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue()!=-1 || oldValue.intValue()==newValue.intValue()){
                    intent.setResultExtra(boxValueToString(newValue.intValue()));
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
