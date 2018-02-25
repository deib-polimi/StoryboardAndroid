package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.ButtonClickIntentAttributes;
import template.attributeInspector.ButtonClickResultAttributes;
import template.attributeInspector.ButtonClickWithResultAttributes;
import template.managers.AttributeInspectorManager;
import template.sample.ButtonClickIntent;
import template.sample.CodeGenerator;
import template.sample.Intent;
import template.sample.IntentType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by utente on 25/02/2018.
 */
public class ButtonClickWithResultIntent extends Intent {

    private String buttonId;
    private String buttonText;
    private String resultButtonText;
    private String resultButtonId;
    private ButtonClickWithResultAttributes intentInspector = null;
    private ButtonClickResultAttributes resultInspector =null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;
    private String layoutTemplate;
    private String extraTemplate;
    private String extraReceiverTemplate;
    private String extraType;
    private String resultExtra;
    private String setResult;

    public ButtonClickWithResultIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new ButtonClickWithResultAttributes();
        this.resultInspector = new ButtonClickResultAttributes();
        super.setName("newIntent");
        buttonId = "newButton";
        resultButtonId="newResultButton";
        extraType = "None";
        resultExtra = "None";
        buttonText="";
        resultButtonText = "";
        layoutTemplate = codeGenerator.provideTemplateForName("templates/ButtonLayoutTemplate");
        extraTemplate =codeGenerator.provideTemplateForName("templates/IntentExtra");
        extraReceiverTemplate =codeGenerator.provideTemplateForName("templates/IntentExtraReceiver");
        classTemplate =codeGenerator.provideTemplateForName("templates/ButtonClickForResultTemplate");
        setResult =codeGenerator.provideTemplateForName("templates/ButtonClickSetResult");

        intentInspector.createListeners(this);
        resultInspector.createListeners(this);
    }

    public String getResultExtra() {
        return resultExtra;
    }

    public void setResultExtra(String resultExtra) {
        this.resultExtra = resultExtra;
    }

    public String getResultButtonText() {
        return resultButtonText;
    }

    public void setResultButtonText(String resultButtonText) {
        this.resultButtonText = resultButtonText;
    }

    public String getResultButtonId() {
        return resultButtonId;
    }

    public void setResultButtonId(String resultButtonId) {
        this.resultButtonId = resultButtonId;
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

    public ButtonClickWithResultAttributes getIntentInspector (){
        intentInspector.fillValues(this);
        return intentInspector;
    }
    public ButtonClickResultAttributes getResultInspector (){
        resultInspector.fillValues(this);
        return resultInspector;
    }

    @Override
    public void loadAttributeInspector(){
        AttributeInspectorManager inspectorManager = AttributeInspectorManager.getInstance();
        intentInspector.fillValues(this);
        inspectorManager.loadIntentInspector(intentInspector,this);
    }
    public String getIntentCode(){

        String template = classTemplate;
        template = template.replace("${REQ_CODE}",Integer.toString(getRequestCode()));

        template = template.replace("${BUTTON_ID}",buttonId);
        //template = template.replace("${INTENT_ID}",super.getName());
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
    public String getResultExtraIdDeclaration(){
        return "public final static String EXTRA_MESSAGE_RESULT = \"Result message from "+super.getBelongingLink().getSource().getName()+"\";\n";
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

    public String getOnResultCode() throws IOException {
        String template = codeGenerator.provideTemplateForName("templates/ButtonClickResult");
        template = template.replace("${REQ_CODE}",Integer.toString(getRequestCode()));
        String extra = "";
        if(!resultExtra.equals("None")){
            extra= codeGenerator.provideTemplateForName("templates/IntentExtraResult");
            extra = extra.replace("${TYPE_VAR}",super.convertExtraType(resultExtra));
            extra = extra.replace("${SOURCE}",super.getBelongingLink().getTarget().getName());
            if(resultExtra.equals("Integer")){
                extra = extra.replace("${TYPE_GET}","Int");
            }else{
                extra = extra.replace("${TYPE_GET}",resultExtra);
            }
            if(resultExtra.equals("String")){
                extra = extra.replace("${DEF_VALUE}","");
            }else{
                extra = extra.replace("${DEF_VALUE}",","+super.getExtraValue(resultExtra));
            }

        }
        template = template.replace("${EXTRA_RESULT}",extra);
        return template;
    }

    public String getResultSetCode(){
        String template = setResult;

        template = template.replace("${BUTTON_ID}",resultButtonId);

        //intent extra
        if(!resultExtra.equals("None")){
            String extra = extraTemplate;
            extra = extra.replace("${TYPE}",super.convertExtraType(resultExtra));
            extra = extra.replace("${CONTENT}",super.getExtraValue(resultExtra));
            extra = extra.replace("${N}","_RESULT");
            template = template.replace("${EXTRA}","\n"+extra+"\n");
        }else{
            template = template.replace("${EXTRA}","");
        }

        return template;
    }
    public String getIntentLayoutCode(){
        String template = layoutTemplate;
        template = template.replace("${BUTTON_ID}",buttonId+"_button");
        template = template.replace("${BUTTON_TXT}",buttonText);
        return template;
    }
    public String getResultIntentLayoutCode(){
        String template = layoutTemplate;
        template = template.replace("${BUTTON_ID}",resultButtonId+"_button");
        template = template.replace("${BUTTON_TXT}",resultButtonText);
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

    private int getRequestCode(){
        List<Intent> intentForResult = new ArrayList<Intent>();
        for(Intent i: super.getBelongingLink().getSource().getOutgoingIntentsForType(IntentType.forResult)){

            intentForResult.add(i);
        }
        return intentForResult.indexOf(this);
    }

}
