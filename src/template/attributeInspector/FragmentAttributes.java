package template.attributeInspector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import template.appInterface.DraggableActivity;

import java.io.IOException;

public class FragmentAttributes extends GridPane {

    @FXML
    private TextField name_txt;

    public FragmentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("FragmentAttributes.fxml")
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
    }

    public void createListeners(DraggableActivity activity){
        name_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            activity.updateName(newValue);
        });
    }

}
