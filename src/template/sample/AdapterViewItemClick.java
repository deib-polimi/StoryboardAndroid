package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.AdapterViewItemClickAttributes;
import template.attributeInspector.ButtonClickIntentAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;

/**
 * Created by utente on 20/02/2018.
 */
public class AdapterViewItemClick extends Intent {

    private AdapterViewItemClickAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;

    public AdapterViewItemClick(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new AdapterViewItemClickAttributes();
        super.setName("newIntent");
        classTemplate = codeGenerator.provideTemplateForName("templates/AdapterViewItemClick");
        intentInspector.createListeners(this);
    }

    public AdapterViewItemClickAttributes getIntentInspector (){
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

        if(super.getBelongingLink().getTarget().getType() == DragControllerType.listView){
            template = template.replace("${ADAPTER_VIEW}","listView");
        }else if(super.getBelongingLink().getTarget().getType() == DragControllerType.gridView){
            template = template.replace("${ADAPTER_VIEW}","gridView");
        }
        template = template.replace("${DESTINATION_ACTIVITY}",super.getBelongingLink().getTarget().getName());
        if(super.getBelongingLink().getSource().isFragment()){
            template = template.replace("${CONTEXT}","getActivity()");
        }else{
            template = template.replace("${CONTEXT}","getApplicationContext()");
        }
        return template;
    }

}
