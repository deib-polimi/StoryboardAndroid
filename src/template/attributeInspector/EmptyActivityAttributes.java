package template.attributeInspector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import template.sample.DraggableActivity;
import template.sample.EmptyActivity;
import template.sample.IsInitialActivity;

import java.io.IOException;

/**
 * Created by utente on 06/02/2018.
 */
public class EmptyActivityAttributes extends GridPane{
    @FXML
    private TextField name_txt;
    @FXML
    private CheckBox is_initial;

    public EmptyActivityAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("EmptyActAttributes.fxml")
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
            name_txt.setText(newValue = newValue.substring(0, 1).toUpperCase() + newValue.substring(1));
        });

    }

    public void fillValues(DraggableActivity activity){
        name_txt.setText(activity.getName());
        is_initial.setSelected(IsInitialActivity.getInstance().isInitialActivity(activity));
    }

    public void createListeners(DraggableActivity activity){
        name_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            activity.updateName(newValue);
        });
        is_initial.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue == true){
                    //uncheck previous initial activity box
                    if(IsInitialActivity.getInstance().getInitialActivity()!=null && IsInitialActivity.getInstance().getInitialActivity()!=activity){
                        IsInitialActivity.getInstance().getInitialActivity().isInitialActivity(false);
                    }
                    //set new initial activity
                    IsInitialActivity.getInstance().setInitialActivity(activity);
                }else if(newValue == false){
                    IsInitialActivity.getInstance().deselectInitialActivity();
                }
            }
        });
    }

    public void setCheckBox(boolean isSelected){
        is_initial.setSelected(isSelected);
    }
}
