package template.attributeInspector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import template.sample.DraggableActivity;
import template.sample.GridViewActivity;
import template.sample.IsInitialActivity;

import java.io.IOException;

/**
 * Created by utente on 20/02/2018.
 */
public class GridViewFragmentAttributes extends GridPane {
    @FXML
    private TextField name_txt;
    @FXML
    private ChoiceBox<String> adapter_type_box;
    @FXML
    private Label columns_number;
    @FXML
    private Button plus_btn;
    @FXML
    private Button minus_btn;

    GridViewActivity gridViewFragment;


    public GridViewFragmentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("GridViewFragmentAttributes.fxml")
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
        adapter_type_box.setValue(((GridViewActivity)activity).getAdapterType());
        columns_number.setText(Integer.toString(((GridViewActivity)activity).getColumns()));
        gridViewFragment = (GridViewActivity)activity;
    }

    public void createListeners(DraggableActivity activity){
        name_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            activity.updateName(newValue);
        });

        adapter_type_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue() == 0){
                    ((GridViewActivity)activity).setAdapterType("String");
                }else if (newValue.intValue() == 1){
                    ((GridViewActivity)activity).setAdapterType("Custom");
                }

            }
        });
    }

    public void plusClick (MouseEvent mouseEvent){
        int col = gridViewFragment.getColumns();
        if(col <20){
            gridViewFragment.setColumns(col+1);
            columns_number.setText(Integer.toString(col+1));
        }
    }

    public void minusClick (MouseEvent mouseEvent){
        int col = gridViewFragment.getColumns();
        if(col >2){
            gridViewFragment.setColumns(col-1);
            columns_number.setText(Integer.toString(col-1));
        }
    }
}
