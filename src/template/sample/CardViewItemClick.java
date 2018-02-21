package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.AdapterViewItemClickAttributes;
import template.attributeInspector.CardViewItemClickAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;

/**
 * Created by utente on 21/02/2018.
 */
public class CardViewItemClick extends Intent {
    private CardViewItemClickAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;

    public CardViewItemClick(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new CardViewItemClickAttributes();
        super.setName("newIntent");
        classTemplate = codeGenerator.provideTemplateForName("templates/CardViewItemClick");
        intentInspector.createListeners(this);
    }

    public CardViewItemClickAttributes getIntentInspector (){
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

        template = template.replace("${ACTIVITY_NAME}",super.getBelongingLink().getSource().getName());
        template = template.replace("${DESTINATION_ACTIVITY}",super.getBelongingLink().getTarget().getName());
        if(super.getBelongingLink().getSource().isFragment()){
            template = template.replace("${CONTEXT}","getActivity()");
        }else{
            template = template.replace("${CONTEXT}","getApplicationContext()");
        }
        return template;
    }
}
