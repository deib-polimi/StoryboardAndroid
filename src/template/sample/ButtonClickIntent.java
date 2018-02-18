package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.ButtonClickIntentAttributes;
import template.managers.AttributeInspectorManager;
import template.managers.StructureTreeManager;

import java.io.IOException;

/**
 * Created by utente on 11/02/2018.
 */
public class ButtonClickIntent extends Intent {

    private String buttonId;
    private String buttonText;
    private ButtonClickIntentAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;
    private String layoutTemplate;

    public ButtonClickIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new ButtonClickIntentAttributes();
        super.setName("newIntent");
        buttonId = "newButton";
        buttonText="";
        classTemplate = codeGenerator.provideTemplateForName("templates/ButtonClickTemplate");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/ButtonLayoutTemplate");
        intentInspector.createListeners(this);
    }

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public ButtonClickIntentAttributes getIntentInspector (){
        intentInspector.fillValues(this);
        return intentInspector;
    }

    @Override
    public void loadAttributeInspector(){
        AttributeInspectorManager inspectorManager = AttributeInspectorManager.getInstance();
        intentInspector.fillValues(this);
        inspectorManager.loadIntentInspector(intentInspector,this);
    }
    public String getIntentCode(){
        String template = classTemplate;
        template = template.replace("${BUTTON_ID}",buttonId);
        template = template.replace("${INTENT_ID}",super.getName());
        template = template.replace("${DESTINATION_ACTIVITY}",super.getBelongingLink().getTarget().getName());
        if(super.getBelongingLink().getSource().isFragment()){
            template = template.replace("${CONTEXT}","getActivity()");
        }else{
            template = template.replace("${CONTEXT}","getApplicationContext()");
        }
        return template;
    }
    public String getIntentLayoutCode(){
        String template = layoutTemplate;
        template = template.replace("${BUTTON_ID}",buttonId+"_button");
        template = template.replace("${BUTTON_TXT}",buttonText);
        return template;
    }
}
