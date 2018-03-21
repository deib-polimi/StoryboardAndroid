package template.intent;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.ButtonClickIntentAttributes;
import template.intent.Intent;
import template.managers.AttributeInspectorManager;
import template.codeGenerator.CodeGenerator;
import template.utils.IntentType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public ButtonClickIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new ButtonClickIntentAttributes();
        super.setName("ButtonClickIntent");
        buttonId = "";
        extraType = "None";
        buttonText="";
        classTemplate = codeGenerator.provideTemplateForName("templates/intentTemplates/ButtonClickTemplate");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/intentTemplates/ButtonLayoutTemplate");
        extraTemplate =codeGenerator.provideTemplateForName("templates/intentTemplates/IntentExtra");
        extraReceiverTemplate =codeGenerator.provideTemplateForName("templates/intentTemplates/IntentExtraReceiver");

        intentInspector.createListeners(this);
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
    public String getIntentCode(){
        String template = classTemplate;

        template = template.replace("${BUTTON_ID}",buttonId);
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
            extra = extra.replace("${N}",Integer.toString(getExtraIndex()));
            template = template.replace("${EXTRA}","\n"+extra+"\n");
        }else{
            template = template.replace("${EXTRA}","");
        }

        return template;
    }

    public String getExtraIdDeclaration(){
        return "public final static String EXTRA_MESSAGE"+Integer.toString(getExtraIndex())
                +" = \"Message"+Integer.toString(getExtraIndex()) +" from "+super.getBelongingLink().getSource().getName()+"\";\n";
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
        template = template.replace("${EXTRA_ID}",getExtraId());
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

    private int getExtraIndex(){
        List<Intent> intentExtra = new ArrayList<Intent>();
        for(Intent i: super.getBelongingLink().getSource().getOutgoingIntents()){

            if(!i.getExtraType().equals("None") && i.getExtraType()!=null ){
                intentExtra.add(i);
            }
        }
        return intentExtra.indexOf(this);
    }
    private String getExtraId(){
        return "EXTRA_MESSAGE"+Integer.toString(getExtraIndex());
    }

}
