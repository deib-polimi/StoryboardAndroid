package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.FABIntentAttributes;
import template.attributeInspector.TabIntentAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;

/**
 * Created by utente on 15/02/2018.
 */
public class FABIntent extends Intent {
    private FABIntentAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;
    private String layoutTemplate;

    public FABIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new FABIntentAttributes();
        super.setName("newIntent");
        classTemplate = codeGenerator.provideTemplateForName("templates/FABIntent");
        layoutTemplate =codeGenerator.provideTemplateForName("templates/FABIntentLayout");
        intentInspector.createListeners(this);
    }

    public FABIntentAttributes getIntentInspector (){
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
        if(super.getBelongingLink().getSource().isFragment()){
            template = template.replace("${GET_VIEW}","view.");
            template = template.replace("${CONTEXT}","getActivity()");
        }else{
            template = template.replace("${GET_VIEW}","");
            template = template.replace("${CONTEXT}","getApplicationContext()");
        }
        return template;
    }
    public String getIntentLayoutCode(){
        return layoutTemplate;
    }
}
