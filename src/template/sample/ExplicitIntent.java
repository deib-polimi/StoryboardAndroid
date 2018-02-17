package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.ButtonClickIntentAttributes;
import template.attributeInspector.FABIntentAttributes;
import template.managers.AttributeInspectorManager;
import template.sample.CodeGenerator;
import template.sample.Intent;
import template.sample.IntentType;

import java.io.IOException;

/**
 * Created by utente on 11/02/2018.
 */
public class ExplicitIntent extends Intent {
    private FABIntentAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;

    public ExplicitIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new FABIntentAttributes();
        super.setName("newIntent");
        classTemplate = codeGenerator.provideTemplateForName("templates/Intent");
    }

    /*public FABIntentAttributes getIntentInspector (){
        intentInspector.fillValues(this);
        return intentInspector;
    }

    @Override
    public void loadAttributeInspector(){
        AttributeInspectorManager inspectorManager = AttributeInspectorManager.getInstance();
        intentInspector.fillValues(this);
        inspectorManager.loadIntentInspector(intentInspector);
    }
    public String getIntentCode(){
        String template = classTemplate;
        template = template.replace("${INTENT_ID}",super.getName());
        template = template.replace("${DESTINATION_ACTIVITY}",super.getBelongingLink().getTarget().getName());
        return template;
    }*/

}
