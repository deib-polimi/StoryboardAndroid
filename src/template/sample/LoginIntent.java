package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.FABIntentAttributes;
import template.attributeInspector.LoginIntentAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;

/**
 * Created by utente on 12/02/2018.
 */
public class LoginIntent extends Intent{
    private LoginIntentAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;
    private String buttonTxt;

    public LoginIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new LoginIntentAttributes();
        super.setName("newIntent");
        buttonTxt = "Sign in or register";

        classTemplate = codeGenerator.provideTemplateForName("templates/Intent");
        intentInspector.createListeners(this);
    }

    public String getButtonTxt() {
        return buttonTxt;
    }

    public void setButtonTxt(String buttonTxt) {
        this.buttonTxt = buttonTxt;
    }

    public LoginIntentAttributes getIntentInspector (){
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
        template = template.replace("${INTENT_ID}",super.getName());
        template = template.replace("${DESTINATION_ACTIVITY}",super.getBelongingLink().getTarget().getName());
        return template;
    }
}
