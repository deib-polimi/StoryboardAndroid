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
import template.intent.LoginIntent;

import java.io.IOException;

public class LoginIntentAttributes extends IntentAttributes{
    @FXML
    private Label target_label;
    @FXML
    private Label type_label;
    @FXML
    private TextField button_txt;
    @FXML
    private ChoiceBox<String> extra_box;

    public LoginIntentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("LoginIntentAttributes.fxml")
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

    public void fillValues(LoginIntent intent){
        target_label.setText(intent.getBelongingLink().getTarget().getName());
        type_label.setText(intent.getType().toString());
        button_txt.setText(intent.getButtonTxt());
        ObservableList<String> boxValues = FXCollections.observableArrayList();
        boxValues.addAll("None","String","Boolean","Integer","Float","Double");
        extra_box.setItems(boxValues);
        extra_box.setValue(intent.getExtraType());
    }

    public void createListeners(LoginIntent intent){

        button_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            intent.setButtonTxt(newValue);
        });

        extra_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue()!=-1 || oldValue.intValue()==newValue.intValue()){
                    intent.setExtraType(boxValueToString(newValue.intValue()));
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
