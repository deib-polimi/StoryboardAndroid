package template.intent;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.TabIntentAttributes;
import template.managers.AttributeInspectorManager;
import template.codeGenerator.CodeGenerator;
import template.utils.IntentType;
import template.activities.TabbedActivity;

import java.io.IOException;

public class TabIntent extends Intent {

    private TabIntentAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;
    private String layoutTemplate;

    public TabIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new TabIntentAttributes();
        super.setName("Tab");
        classTemplate = codeGenerator.provideTemplateForName("templates/intentTemplates/TabIntent");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/intentTemplates/TabIntentLayout");
        intentInspector.createListeners(this);
    }

    public int getOrder() {
        TabbedActivity container = (TabbedActivity) super.getBelongingLink().getSource();
        return container.getOrder(this);
    }

    public TabIntentAttributes getIntentInspector (){
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
        template = template.replace("${N}",Integer.toString(getOrder()+1));
        template = template.replace("${N_CASE}",Integer.toString(getOrder()));
        template = template.replace("${FRAGMENT}",super.getBelongingLink().getTarget().getName());
        return template;
    }
    public String getIntentLayoutCode(){
        String template = layoutTemplate;
        template = template.replace("${N}",Integer.toString(getOrder()+1));
        template = template.replace("${TEXT}",super.getName());
        return template;
    }
    public void fillValues(TabIntent intent){
        intentInspector.fillValues(this);
    }
}
