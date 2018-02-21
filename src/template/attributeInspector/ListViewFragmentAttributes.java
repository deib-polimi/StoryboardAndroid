package template.attributeInspector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import template.sample.DraggableActivity;
import template.sample.IsInitialActivity;
import template.sample.ListViewActivity;

import java.io.IOException;

public class ListViewFragmentAttributes extends GridPane{

    @FXML
    private TextField name_txt;
    @FXML
    private ChoiceBox<String> adapter_type_box;

    public ListViewFragmentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("ListViewFragmentAttributes.fxml")
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
        ObservableList<String> boxValues = FXCollections.observableArrayList();
        boxValues.addAll("String","Custom");
        adapter_type_box.setItems(boxValues);
        adapter_type_box.setValue(((ListViewActivity)activity).getAdapterType());
    }

    public void createListeners(DraggableActivity activity){
        name_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            activity.updateName(newValue);
        });

        adapter_type_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue() == 0){
                    ((ListViewActivity)activity).setAdapterType("String");
                }else if (newValue.intValue() == 1){
                    ((ListViewActivity)activity).setAdapterType("Custom");
                }

            }
        });
    }

}
