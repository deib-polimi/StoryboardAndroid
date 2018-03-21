package template.attributeInspector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import template.intent.TabIntent;
import template.activities.TabbedActivity;

import java.io.IOException;

public class TabIntentAttributes extends IntentAttributes {
    @FXML
    private TextField tab_text;
    @FXML
    private ChoiceBox<Integer> choice_box_order;
    private TabbedActivity container;

    public TabIntentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("TabIntentAttributes.fxml")
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

    public void fillValues(TabIntent intent){
        tab_text.setText(intent.getName());
        container =(TabbedActivity)intent.getBelongingLink().getSource();
        //container.updateTabs();
        int order = container.getOrder(intent)+1;
        ObservableList<Integer> tabNum = FXCollections.observableArrayList();
        for (int i =1; i<= container.getTabs().size();i++){
            tabNum.add(i);
        }
        choice_box_order.setItems(tabNum);
        choice_box_order.setValue(order);

    }

    public void createListeners(TabIntent intent){
        tab_text.textProperty().addListener((observable, oldValue, newValue) -> {
            intent.updateName(newValue);
        });
        choice_box_order.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(oldValue!=newValue && newValue.intValue()!=(-1) && oldValue.intValue()!=(-1)){
                    container.changeOrder(oldValue.intValue(),newValue.intValue());
                }
            }
        });

    }
}
