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
import template.sample.CardViewActivity;
import template.sample.DraggableActivity;
import template.sample.IsInitialActivity;

import java.io.IOException;

/**
 * Created by utente on 21/02/2018.
 */
public class CardViewFragmentAttributes extends GridPane {
    @FXML
    private TextField name_txt;
    @FXML
    private ChoiceBox<String> layout_type_box;
    @FXML
    private Label columns_number;
    @FXML
    private Button plus_btn;
    @FXML
    private Button minus_btn;
    @FXML
    private Label columns_label;

    CardViewActivity cardViewActivity;


    public CardViewFragmentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("CardViewFragmentAttributes.fxml")
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
        boxValues.addAll("List","Grid");
        layout_type_box.setItems(boxValues);
        layout_type_box.setValue(((CardViewActivity)activity).getLayout());
        if(layout_type_box.getValue().equals("List")){
            columns_label.setVisible(false);
            columns_number.setVisible(false);
            plus_btn.setVisible(false);
            minus_btn.setVisible(false);
        }else if (layout_type_box.getValue().equals("Grid")){
            columns_label.setVisible(true);
            columns_number.setVisible(true);
            plus_btn.setVisible(true);
            minus_btn.setVisible(true);
        }
        columns_number.setText(Integer.toString(((CardViewActivity)activity).getColumns()));
        cardViewActivity = (CardViewActivity)activity;
    }

    public void createListeners(DraggableActivity activity){
        name_txt.textProperty().addListener((observable, oldValue, newValue) -> {
            activity.updateName(newValue);
        });

        layout_type_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue() == 0){
                    ((CardViewActivity)activity).setLayout("List");
                    columns_label.setVisible(false);
                    columns_number.setVisible(false);
                    plus_btn.setVisible(false);
                    minus_btn.setVisible(false);
                }else if (newValue.intValue() == 1){
                    ((CardViewActivity)activity).setLayout("Grid");
                    columns_label.setVisible(true);
                    columns_number.setVisible(true);
                    plus_btn.setVisible(true);
                    minus_btn.setVisible(true);
                }

            }
        });
    }

    public void plusClick (MouseEvent mouseEvent){
        int col = cardViewActivity.getColumns();
        if(col <20){
            cardViewActivity.setColumns(col+1);
            columns_number.setText(Integer.toString(col+1));
        }
    }

    public void minusClick (MouseEvent mouseEvent){
        int col = cardViewActivity.getColumns();
        if(col >2){
            cardViewActivity.setColumns(col-1);
            columns_number.setText(Integer.toString(col-1));
        }
    }
}
