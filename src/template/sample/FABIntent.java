package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.FABIntentAttributes;
import template.attributeInspector.TabIntentAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by utente on 15/02/2018.
 */
public class FABIntent extends Intent {
    private FABIntentAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;
    private String layoutTemplate;
    private String extraTemplate;
    private String extraReceiverTemplate;
    private String extraType;

    public FABIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new FABIntentAttributes();
        super.setName("newIntent");
        extraType = "None";
        classTemplate = codeGenerator.provideTemplateForName("templates/FABIntent");
        layoutTemplate =codeGenerator.provideTemplateForName("templates/FABIntentLayout");
        extraTemplate =codeGenerator.provideTemplateForName("templates/IntentExtra");
        extraReceiverTemplate =codeGenerator.provideTemplateForName("templates/IntentExtraReceiver");
        intentInspector.createListeners(this);
    }

    @Override
    public String getExtraType() {
        return extraType;
    }

    public void setExtraType(String extraType) {
        this.extraType = extraType;
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
        //template = template.replace("${INTENT_ID}",super.getName());
        template = template.replace("${DESTINATION_ACTIVITY}",super.getBelongingLink().getTarget().getName());
        if(super.getBelongingLink().getSource().isFragment()){
            template = template.replace("${GET_VIEW}","view.");
            template = template.replace("${CONTEXT}","getActivity()");
        }else{
            template = template.replace("${GET_VIEW}","");
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
        return layoutTemplate;
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
