package template.attributeInspector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import template.sample.BottomNavigationActivity;
import template.sample.BottomNavigationIntent;
import template.sample.TabIntent;
import template.sample.TabbedActivity;

import java.io.IOException;

/**
 * Created by utente on 17/02/2018.
 */
public class BottomNavigationIntentAttributes extends IntentAttributes {
    @FXML
    private TextField tab_text;
    @FXML
    private ChoiceBox<Integer> choice_box_order;
    @FXML
    private ComboBox<String> icon_box;
    private BottomNavigationActivity container;

    public BottomNavigationIntentAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("BottomNavigationIntentAttributes.fxml")
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
        icon_box.getItems().addAll("Android","Build","Dashboard","Edit","Home","Notifications","Person","Share");
        icon_box.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> p) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon;
                            try {
                                String iconPath = getPath(this.getIndex());
                                icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                            } catch(NullPointerException ex) {
                                // in case the above image doesn't exist, use a default one
                                icon = null;
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(15);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        });

    }

    public void fillValues(BottomNavigationIntent intent){
        tab_text.setText(intent.getName());
        container =(BottomNavigationActivity)intent.getBelongingLink().getSource();
        int order = container.getOrder(intent)+1;
        ObservableList<Integer> tabNum = FXCollections.observableArrayList();
        for (int i =1; i<= container.getTabs().size();i++){
            tabNum.add(i);
        }
        choice_box_order.setItems(tabNum);
        choice_box_order.setValue(order);

        icon_box.setVisibleRowCount(4);
        icon_box.setValue(intent.getIcon());

    }

    public void createListeners(BottomNavigationIntent intent){
        tab_text.textProperty().addListener((observable, oldValue, newValue) -> {
            intent.updateName(newValue);
        });
        choice_box_order.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(oldValue!=newValue && newValue.intValue()!=(-1) && oldValue.intValue()!=(-1)){
                    container.changeOrder(oldValue.intValue(),newValue.intValue());
                    System.out.println("change order: "+tab_text.getText()+" from "+oldValue+" to "+newValue);
                }
            }
        });
        icon_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //if(oldValue!=newValue && newValue.intValue()!=(-1) && oldValue.intValue()!=(-1)){
                    intent.setIcon(icon_box.getValue());
                    System.out.println(intent.getIcon());
                //}
            }
        });

    }

    private String getPath(int index){
        switch(index){
            case 0:
                return "/icons/ic_android.png";

            case 1:
                return "/icons/ic_build.png";
            case 2:
                return "/icons/ic_dashboard.png";
            case 3:
                return "/icons/ic_edit.png";
            case 4:
                return "/icons/ic_home.png";

            case 5:
                return "/icons/ic_notifications.png";
            case 6:
                return "/icons/ic_person.png";
            case 7:
                return "/icons/ic_share.png";

            default:
                return null;

        }
    }
}
