package template.managers;

import javafx.scene.control.TextField;

public class AttributeInspectorManager {

    private TextField text;

    private static AttributeInspectorManager instance = null;

    public static AttributeInspectorManager getInstance() {

        if(instance == null) {
            instance = new AttributeInspectorManager();
        }
        return instance;
    }

    private AttributeInspectorManager() {
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String nText) {
        text.setText(nText);
    }

    public void setTextField(TextField textField){
        this.text = textField;
    }

}
