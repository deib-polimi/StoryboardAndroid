package template.attributeInspector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import template.sample.AdapterViewItemClick;
import template.sample.CardViewItemClick;

import java.io.IOException;

/**
 * Created by utente on 21/02/2018.
 */
public class CardViewItemClickAttributes extends IntentAttributes {
    @FXML
    private Label type_label;

    public CardViewItemClickAttributes() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("CardViewItemClickAttributes.fxml")
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

    public void fillValues(CardViewItemClick intent){

        type_label.setText(intent.getType().toString());

    }

    public void createListeners(CardViewItemClick intent){

    }
}
