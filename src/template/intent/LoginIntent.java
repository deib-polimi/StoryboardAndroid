package template.intent;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.LoginIntentAttributes;
import template.managers.AttributeInspectorManager;
import template.codeGenerator.CodeGenerator;
import template.utils.IntentType;

import java.io.IOException;

public class LoginIntent extends Intent {
    private LoginIntentAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;
    private String buttonTxt;
    private String extraTemplate;
    private String extraReceiverTemplate;
    private String extraType;

    public LoginIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new LoginIntentAttributes();
        super.setName("LoginIntent");
        buttonTxt = "Sign in or register";
        extraType = "None";
        classTemplate = codeGenerator.provideTemplateForName("templates/intentTemplates/Intent");
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
        template = template.replace("${DESTINATION_ACTIVITY}",super.getBelongingLink().getTarget().getName());

        //intent extra
        if(!extraType.equals("None")){
            String extra = extraTemplate;
            extra = extra.replace("${TYPE}",super.convertExtraType(extraType));
            extra = extra.replace("${CONTENT}",super.getExtraValue(extraType));
            extra = extra.replace("${N}","");
            template = template.replace("${EXTRA}","\n"+extra+"\n");
        }else{
            template = template.replace("${EXTRA}","");
        }

        return template;
    }

    public String getExtraIdDeclaration(){
        return "public final static String EXTRA_MESSAGE = \"Message from "+super.getBelongingLink().getSource().getName()+"\";\n";
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
        template = template.replace("${EXTRA_ID}","EXTRA_MESSAGE");
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
}
