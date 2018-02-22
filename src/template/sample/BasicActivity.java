package template.sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import template.ProjectHandler;
import template.attributeInspector.EmptyActivityAttributes;
import template.attributeInspector.FragmentAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by utente on 11/02/2018.
 */
public class BasicActivity extends DraggableActivity {

    private String classTemplate;
    private String layoutTemplate;
    private String fragmentTemplate;
    private EmptyActivityAttributes activityInspector= null;
    private FragmentAttributes fragmentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();

    public BasicActivity() throws IOException {
        super();
        //get activity and layout templates in String
        classTemplate = codeGenerator.provideTemplateForName("templates/BasicActivity");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/BasicActivityLayout");
        fragmentTemplate = codeGenerator.provideTemplateForName("templates/BasicFragment");
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("DraggableActivity.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public boolean isFragment() {
        return super.isFragment();
    }

    @FXML
    private void initialize() {
        super.init();
        activityInspector = new EmptyActivityAttributes();
        fragmentInspector = new FragmentAttributes();
        super.setName("NewBasicActivity");
        loadInspector();
    }

    @Override
    public String createJavaCode() throws IOException {
        String template = classTemplate;
        String imports = "";
        String declarations = "";
        String setViews = "";
        String buttonClickintent = "";
        String fabIntent = "";
        String extraId= "";
        //create code of the button click intents outgoing from the activity

        if((super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0)||(super.getOutgoingIntentsForType(IntentType.fabClick).size()>0)){
            imports = imports.concat(Imports.INTENT+"\n");
        }
        if (super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0){
            //set imports
            imports =imports.concat(Imports.BUTTON+"\n");
            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                //set buttons declarations
                declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n");
                if(!((ButtonClickIntent) i).getExtraType().equals("None")){
                    extraId = extraId.concat(((ButtonClickIntent)i).getExtraIdDeclaration()+"\n");
                    buttonClickintent = buttonClickintent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");

                }else{
                    buttonClickintent = buttonClickintent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");
                }

                setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) findViewById(R.id."
                        +((ButtonClickIntent)i).getButtonId()+"_button);\n");
            }
        }

        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){

            Intent intent = super.getOutgoingIntentsForType(IntentType.fabClick).get(0);
            if(!intent.getExtraType().equals("None")){
                extraId = extraId.concat(((FABIntent)intent).getExtraIdDeclaration()+"\n");
                fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode()+"\n");

            }else{
                fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode()+'\n');
            }
            template = template.replace("${INTENT}",fabIntent);
        }else{
            String emptyFAB =codeGenerator.provideTemplateForName("templates/FABIntentEmpty");
            emptyFAB = emptyFAB.replace("${GET_VIEW}","");
            template = template.replace("${INTENT}",emptyFAB);
        }

        //intent receivers
        String receivers = "";
        int nReceiver = 1;
        for(Intent i : getIngoingIntents()){
            if(i.getExtraType()!=null && !i.getExtraType().equals("None")){
                receivers = receivers.concat(i.getExtraReceiver(nReceiver)+"\n");
                nReceiver++;
            }
        }


        template = template.replace("${DECLARATIONS}",declarations);
        template = template.replace("${SET_VIEWS}",setViews);
        template = template.replace("${BUTTON_CLICK_INTENT}",buttonClickintent);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());

        template = template.replace("${INTENT_EXTRA_ID}","\n"+extraId);
        if (!receivers.equals("")){
            if(super.getOutgoingIntentsForType(IntentType.buttonClick).size()==0 && super.getOutgoingIntentsForType(IntentType.fabClick).size()==0){
                imports = imports = imports.concat(Imports.INTENT+"\n");
            }
            template = template.replace("${INTENT_RECEIVER}","Intent intent = getIntent();\n"+receivers);
        }else{
            template = template.replace("${INTENT_RECEIVER}",receivers);
        }

        template = template.replace("${IMPORTS}",imports);

        return template;
    }

    @Override
    public String createFragmentCode() throws IOException {
        String template = fragmentTemplate;
        String imports = "";
        String declarations = "";
        String setViews = "";
        String buttonClickintent = "";
        String fabIntent = "";
        String extraId="";
        //create code of the button click intents outgoing from the activity

        if((super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0)||(super.getOutgoingIntentsForType(IntentType.fabClick).size()>0)){
            imports = imports.concat(Imports.INTENT+"\n");
        }
        if (super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0){
            //set imports
            imports =imports.concat(Imports.BUTTON+"\n");
            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                //set buttons declarations
                declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n");
                if(!((ButtonClickIntent) i).getExtraType().equals("None")){
                    extraId = extraId.concat(((ButtonClickIntent)i).getExtraIdDeclaration()+"\n");
                    buttonClickintent = buttonClickintent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");

                }else{
                    buttonClickintent = buttonClickintent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");
                }

                setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) view.findViewById(R.id."
                        +((ButtonClickIntent)i).getButtonId()+"_button);\n");
            }
        }
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){

            Intent intent = super.getOutgoingIntentsForType(IntentType.fabClick).get(0);
            if(!intent.getExtraType().equals("None")){
                extraId = extraId.concat(((FABIntent)intent).getExtraIdDeclaration()+"\n");
                fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode()+"\n");

            }else{
                fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode()+'\n');
            }
            template = template.replace("${INTENT}",fabIntent);
        }else{
            String emptyFAB =codeGenerator.provideTemplateForName("templates/FABIntentEmpty");
            emptyFAB = emptyFAB.replace("${GET_VIEW}","view.");
            template = template.replace("${INTENT}",emptyFAB);
        }

        template = template.replace("${IMPORTS}",imports);
        template = template.replace("${DECLARATIONS}",declarations);
        template = template.replace("${SET_VIEWS}",setViews);
        template = template.replace("${BUTTON_CLICK_INTENT}",buttonClickintent);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());

        template = template.replace("${INTENT_EXTRA_ID}","\n"+extraId);

        return template;
    }

    @Override
    public String createXMLCode(){
        String template = layoutTemplate;
        String buttons="";
        for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
            buttons = buttons.concat(((ButtonClickIntent)i).getIntentLayoutCode()+"\n\t");
        }
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        template = template.replace("${BUTTONS}",buttons);
        return template;
    }

    @Override
    public void loadInspectorListeners(){
        activityInspector.createListeners(this);
        fragmentInspector.createListeners(this);
    }

    @Override
    public void loadInspector(){
        AttributeInspectorManager inspectorManager = AttributeInspectorManager.getInstance();
        if(isFragment()){
            fragmentInspector.fillValues(this);
            inspectorManager.loadActivityInspector(fragmentInspector,this);
        }else{
            activityInspector.fillValues(this);
            inspectorManager.loadActivityInspector(activityInspector,this);
        }


    }

    @Override
    public void isInitialActivity(boolean isInitial){
        activityInspector.setCheckBox(isInitial);
    }

    @Override
    public String getManifest() throws IOException {
        String manifest = codeGenerator.provideTemplateForName("templates/ManifestActivity");
        manifest = manifest.replace("${ACTIVITY}",super.getName());

        manifest = manifest.replace("${ATTRIBUTES}"," android:label=\""+this.getName()+"\"");
        if (IsInitialActivity.getInstance().isInitialActivity(this)){
            manifest = manifest.replace("${INTENT_FILTER}","\n"+codeGenerator.provideTemplateForName("templates/IntentFilterLauncher")+"\n");
        }else {
            manifest = manifest.replace("${INTENT_FILTER}","");
        }
        return manifest;
    }

    //generate layout name from object name. Ex: MyClass -> _my_class
    @Override
    public String generateLayoutName(String objectName){
        Scanner in = new Scanner(objectName);
        String out = "";
        String x = in.next();
        int z = x.length();
        for(int y = 0; y < z; y++){
            if(Character.isUpperCase(x.charAt(y))){
                out = out+"_"+(Character.toLowerCase(x.charAt(y)));

            }else{
                out = out+x.charAt(y);
            }
        }
        if(isFragment()){
            return "fragment"+out;
        }else{
            return "activity"+out;
        }
    }

    @Override
    public java.util.List<MenuItem> getMenuItems(RootLayout root, DragContainer container,DraggableActivity target){
        List<MenuItem> items = new ArrayList<MenuItem>();
        if(!target.isFragment()){
            MenuItem item1 = new MenuItem("Button Click");
            item1.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    IntentType intentType = IntentType.buttonClick;
                    try {
                        root.createLink(container,intentType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            items.add(item1);
            //allow only one FAB intent
            if(super.getOutgoingIntentsForType(IntentType.fabClick).size() ==0){
                MenuItem item2 = new MenuItem("FAB Intent");
                item2.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        IntentType intentType = IntentType.fabClick;
                        try {
                            root.createLink(container,intentType);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                items.add(item2);
            }
        }

        return items;
    };

}
