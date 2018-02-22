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
    private String extraTemplate;
    private String extraReceiverTemplate;
    private String extraType;
    private String extraId;

    public ButtonClickIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new ButtonClickIntentAttributes();
        super.setName("newIntent");
        buttonId = "newButton";
        extraType = "None";
        buttonText="";
        classTemplate = codeGenerator.provideTemplateForName("templates/ButtonClickTemplate");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/ButtonLayoutTemplate");
        extraTemplate =codeGenerator.provideTemplateForName("templates/IntentExtra");
        extraReceiverTemplate =codeGenerator.provideTemplateForName("templates/IntentExtraReceiver");

        intentInspector.createListeners(this);
    }

    public String getExtraId() {
        return extraId;
    }

    public void setExtraId(String extraId) {
        this.extraId = extraId;
    }

    @Override
    public String getExtraType() {
        return extraType;
    }

    public void setExtraType(String extraType) {
        this.extraType = extraType;
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
    public String getIntentCode(int nID){
        String template = classTemplate;
        template = template.replace("${BUTTON_ID}",buttonId);
        template = template.replace("${INTENT_ID}",super.getName());
        template = template.replace("${DESTINATION_ACTIVITY}",super.getBelongingLink().getTarget().getName());
        if(super.getBelongingLink().getSource().isFragment()){
            template = template.replace("${CONTEXT}","getActivity()");
        }else{
            template = template.replace("${CONTEXT}","getApplicationContext()");
        }

        //intent extra
        if(!extraType.equals("None")){
            String extra = extraTemplate;
            extra = extra.replace("${TYPE}",super.convertExtraType(extraType));
            extra = extra.replace("${CONTENT}",super.getExtraValue(extraType));
            extra = extra.replace("${N}",Integer.toString(nID));
            template = template.replace("${EXTRA}","\n"+extra+"\n");
            extraId ="EXTRA_MESSAGE"+Integer.toString(nID);
        }else{
            template = template.replace("${EXTRA}","");
        }

        return template;
    }

    public String getExtraIdDeclaration(int nID){
        return "public final static String EXTRA_MESSAGE"+Integer.toString(nID)
                +" = \"Message"+Integer.toString(nID) +" from "+super.getBelongingLink().getSource().getName()+"\";\n";
    }
    @Override
    public String getExtraReceiver(int nID){
        String template = extraReceiverTemplate;
        template = template.replace("${TYPE_VAR}",super.convertExtraType(extraType));
        if(extraType.equals("Integer")){
            template = template.replace("${TYPE_GET}","Int");
        }else{
            template = template.replace("${TYPE_GET}",extraType);
        }
        template = template.replace("${EXTRA_ID}",extraId);
        template = template.replace("${TYPE_GET}",extraType);
        template = template.replace("${N}",Integer.toString(nID));
        template = template.replace("${SOURCE}",super.getBelongingLink().getSource().getName());
        if(extraType.equals("String")){
            template = template.replace("${DEF_VALUE}","");
        }else{
            template = template.replace("${DEF_VALUE}",","+super.getExtraValue(extraType));
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
