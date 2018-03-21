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
import template.activities.GridViewActivity;
import template.appInterface.*;

import java.io.IOException;

public class GridViewAttributes extends GridPane {
    @FXML
    private TextField name_txt;
    @FXML
    private CheckBox is_initial;
    @FXML
    private ChoiceBox<String> adapter_type_box;
    @FXML
    private Label columns_number;
    @FXML
    private Button plus_btn;
    @FXML
    private Button minus_btn;

    GridViewActivity gridViewActivity;


    public GridViewAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("GridViewAttributes.fxml")
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
        ObservableList<String> boxValues = FXCollections.observableArrayList();
        boxValues.addAll("String","Custom");
        adapter_type_box.setItems(boxValues);
        adapter_type_box.setValue(((GridViewActivity)activity).getAdapterType());
        columns_number.setText(Integer.toString(((GridViewActivity)activity).getColumns()));
        gridViewActivity = (GridViewActivity)activity;
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

    public void setCheckBox(boolean isSelected){
        is_initial.setSelected(isSelected);
    }

    public void plusClick (MouseEvent mouseEvent){
        int col = gridViewActivity.getColumns();
        if(col <20){
            gridViewActivity.setColumns(col+1);
            columns_number.setText(Integer.toString(col+1));
        }
    }

    public void minusClick (MouseEvent mouseEvent){
        int col = gridViewActivity.getColumns();
        if(col >2){
            gridViewActivity.setColumns(col-1);
            columns_number.setText(Integer.toString(col-1));
        }
    }
}
