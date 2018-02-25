package template.sample;

import com.google.common.io.Resources;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import template.ProjectHandler;
import template.attributeInspector.EmptyActivityAttributes;
import template.attributeInspector.FragmentAttributes;
import template.managers.AttributeInspectorManager;
import template.sample.DraggableActivity;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

/**
 * Created by utente on 03/02/2018.
 */
public class EmptyActivity extends DraggableActivity {
    private String classTemplate;
    private String layoutTemplate;
    private String fragmentTemplate;
    private EmptyActivityAttributes activityInspector= null;
    private FragmentAttributes fragmentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    //private boolean isFragment = false;

    public EmptyActivity() throws IOException {
        super();
        //get activity and layout templates in String
        classTemplate = codeGenerator.provideTemplateForName("templates/EmptyActivity");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/EmptyActivityLayout");
        fragmentTemplate = codeGenerator.provideTemplateForName("templates/EmptyFragment");
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
        super.setName("NewEmptyActivity");
        loadInspector();
    }



    @Override
    public String createJavaCode() throws IOException {
        String template = classTemplate;
        String imports = "";
        String declarations = "";
        String setViews = "";
        String intent = "";
        String extraId= "";
        //create code of the button click intents outgoing from the activity
        if (super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0 ||
                super.getOutgoingIntentsForType(IntentType.forResult).size()>0 ||
                super.getIngoingIntentsForType(IntentType.forResult).size()>0){
            //set imports
            imports =imports.concat(Imports.BUTTON+"\n");
            imports = imports.concat(Imports.INTENT+"\n");
            imports = imports.concat(Imports.VIEW+"\n");

            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                //set buttons declarations
                declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n");
                if(!((ButtonClickIntent) i).getExtraType().equals("None")){
                    extraId = extraId.concat(((ButtonClickIntent)i).getExtraIdDeclaration()+"\n");
                }
                intent = intent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");
                setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) findViewById(R.id."
                        +((ButtonClickIntent)i).getButtonId()+"_button);\n");
            }
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

        //onActivityResult
        String results ="";
        for (Intent i :super.getOutgoingIntentsForType(IntentType.forResult)){
            results = results.concat(((ButtonClickWithResultIntent)i).getOnResultCode()+"\n");
            declarations = declarations.concat("private Button "+((ButtonClickWithResultIntent)i).getButtonId()+";\n");
            if(!((ButtonClickWithResultIntent) i).getExtraType().equals("None")){
                extraId = extraId.concat(((ButtonClickWithResultIntent)i).getExtraIdDeclaration()+"\n");
            }
            intent = intent.concat(((ButtonClickWithResultIntent)i).getIntentCode()+"\n");
            setViews = setViews.concat(((ButtonClickWithResultIntent)i).getButtonId()+" = (Button) findViewById(R.id."
                    +((ButtonClickWithResultIntent)i).getButtonId()+"_button);\n");

        }
        if(!results.equals("")){
            template = template.replace("${ON_RESULT}","\n"+codeGenerator.provideTemplateForName("templates/ButtonClickOnResult"));
            template = template.replace("${VISIBILITY}","protected");

            template = template.replace("${RESULTS}",results);
        }else{
            template = template.replace("${ON_RESULT}","");
        }

        //set result intent
        for(Intent i:super.getIngoingIntentsForType(IntentType.forResult)){
            declarations = declarations.concat("private Button "+((ButtonClickWithResultIntent)i).getResultButtonId()+";\n");
            if(!((ButtonClickWithResultIntent) i).getResultExtra().equals("None")){
                extraId = extraId.concat(((ButtonClickWithResultIntent)i).getResultExtraIdDeclaration()+"\n");
            }
            intent = intent.concat(((ButtonClickWithResultIntent)i).getResultSetCode()+"\n");
            setViews = setViews.concat(((ButtonClickWithResultIntent)i).getResultButtonId()+" = (Button) findViewById(R.id."
                    +((ButtonClickWithResultIntent)i).getResultButtonId()+"_button);\n");
        }



        //up navigation
        if(super.getIngoingIntentsForType(IntentType.itemClick).size()>0 ||
                super.getIngoingIntentsForType(IntentType.cardClick).size()>0 ){
            template = template.replace("${UP_NAVIGATION}","getSupportActionBar().setDisplayHomeAsUpEnabled(true);"+"\n");
        }else{
            template = template.replace("${UP_NAVIGATION}","");
        }

        template = template.replace("${INTENT_EXTRA_ID}","\n"+extraId);
        template = template.replace("${DECLARATIONS}",declarations);
        template = template.replace("${SET_VIEWS}",setViews);
        template = template.replace("${BUTTON_CLICK_INTENT}",intent);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        if (!receivers.equals("")){
            if(super.getOutgoingIntentsForType(IntentType.buttonClick).size()==0){
                imports = imports.concat(Imports.INTENT+"\n");
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
        String intent = "";
        String extraId= "";
        //create code of the button click intents outgoing from the activity
        if (super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0 ||
                super.getOutgoingIntentsForType(IntentType.forResult).size()>0){
            //set imports
            imports =imports.concat(Imports.BUTTON+"\n");
            imports = imports.concat(Imports.INTENT+"\n");
            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                //set buttons declarations
                declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n");
                if(!((ButtonClickIntent) i).getExtraType().equals("None")){
                    extraId = extraId.concat(((ButtonClickIntent)i).getExtraIdDeclaration()+"\n");
                }
                intent = intent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");
                setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) view.findViewById(R.id."
                        +((ButtonClickIntent)i).getButtonId()+"_button);\n");
            }
        }
        if(super.getOutgoingIntentsForType(IntentType.forResult).size()>0){
            imports= imports.concat(Imports.RESULT_OK);
        }

        //onActivityResult
        String results ="";
        for (Intent i :super.getOutgoingIntentsForType(IntentType.forResult)){
            results = results.concat(((ButtonClickWithResultIntent)i).getOnResultCode()+"\n");
            declarations = declarations.concat("private Button "+((ButtonClickWithResultIntent)i).getButtonId()+";\n");
            if(!((ButtonClickWithResultIntent) i).getExtraType().equals("None")){
                extraId = extraId.concat(((ButtonClickWithResultIntent)i).getExtraIdDeclaration()+"\n");
            }
            intent = intent.concat(((ButtonClickWithResultIntent)i).getIntentCode()+"\n");
            setViews = setViews.concat(((ButtonClickWithResultIntent)i).getButtonId()+" = (Button) findViewById(R.id."
                    +((ButtonClickWithResultIntent)i).getButtonId()+"_button);\n");

        }
        if(!results.equals("")){
            template = template.replace("${ON_RESULT}","\n"+codeGenerator.provideTemplateForName("templates/ButtonClickOnResult"));
            template = template.replace("${VISIBILITY}","public");

            template = template.replace("${RESULTS}",results);
        }else{
            template = template.replace("${ON_RESULT}","");
        }

        template = template.replace("${INTENT_EXTRA_ID}","\n"+extraId);
        template = template.replace("${DECLARATIONS}",declarations);
        template = template.replace("${SET_VIEWS}",setViews);
        template = template.replace("${BUTTON_CLICK_INTENT}",intent);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        template = template.replace("${INTENT_RECEIVER}","");
        template = template.replace("${IMPORTS}",imports);
        return template;
    }

    public String createXMLCode() throws IOException {
        String template = layoutTemplate;
        String buttons="";

         if((super.getOutgoingIntentsForType(IntentType.buttonClick).size() +
                super.getOutgoingIntentsForType(IntentType.forResult).size() +
                super.getIngoingIntentsForType(IntentType.forResult).size())>1){
            template = template.replace("${BUTTONS}",codeGenerator.provideTemplateForName("templates/LinearLayout"));
            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                buttons = buttons.concat(((ButtonClickIntent)i).getIntentLayoutCode()+"\n");
            }
            for(Intent i : super.getOutgoingIntentsForType(IntentType.forResult)){
                buttons = buttons.concat(((ButtonClickWithResultIntent)i).getIntentLayoutCode()+"\n");
            }
            for (Intent i: super.getIngoingIntentsForType(IntentType.forResult)){
                buttons = buttons.concat(((ButtonClickWithResultIntent)i).getResultIntentLayoutCode()+"\n");
            }
        }else if(super.getOutgoingIntentsForType(IntentType.buttonClick).size() == 1){
            Intent i = super.getOutgoingIntentsForType(IntentType.buttonClick).get(0);
            buttons = buttons.concat(((ButtonClickIntent)i).getIntentLayoutCode()+"\n");
        } else if(super.getOutgoingIntentsForType(IntentType.forResult).size() == 1){
            Intent i = super.getOutgoingIntentsForType(IntentType.forResult).get(0);
            buttons = buttons.concat(((ButtonClickWithResultIntent)i).getIntentLayoutCode()+"\n");
        }
         else if(super.getIngoingIntentsForType(IntentType.forResult).size() == 1){
             Intent i = super.getIngoingIntentsForType(IntentType.forResult).get(0);
             buttons = buttons.concat(((ButtonClickWithResultIntent)i).getResultIntentLayoutCode()+"\n");
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
        String attributes ="";
        String manifest = codeGenerator.provideTemplateForName("templates/ManifestActivity");
        manifest = manifest.replace("${ACTIVITY}",super.getName());
        attributes = attributes.concat(" android:label=\""+super.getName()+"\"");
        if (IsInitialActivity.getInstance().isInitialActivity(this)){
            manifest = manifest.replace("${INTENT_FILTER}","\n"+codeGenerator.provideTemplateForName("templates/IntentFilterLauncher")+"\n\t\t");
        }else {
            manifest = manifest.replace("${INTENT_FILTER}","");
        }

        //up navigation
        if(super.getIngoingIntentsForType(IntentType.itemClick).size()==1){
            AdapterViewItemClick i = (AdapterViewItemClick)super.getIngoingIntentsForType(IntentType.itemClick).get(0);
            if(i.getBelongingLink().getSource().isFragment()){
                attributes = attributes.concat("\nandroid:parentActivityName=\"."+getContainerActivity(i.getBelongingLink().getSource()).getName()+"\"");
            }else{
                attributes = attributes.concat("\nandroid:parentActivityName=\"."+i.getBelongingLink().getSource().getName()+"\"");
            }
        } else if(super.getIngoingIntentsForType(IntentType.cardClick).size()==1){
            CardViewItemClick i = (CardViewItemClick) super.getIngoingIntentsForType(IntentType.cardClick).get(0);
            if(i.getBelongingLink().getSource().isFragment()){
                attributes = attributes.concat("\nandroid:parentActivityName=\"."+getContainerActivity(i.getBelongingLink().getSource()).getName()+"\"");
            }else{
                attributes = attributes.concat("\nandroid:parentActivityName=\"."+i.getBelongingLink().getSource().getName()+"\"");
            }
        }
        manifest = manifest.replace("${ATTRIBUTES}",attributes);
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
    public java.util.List<MenuItem> getMenuItems(RootLayout root,DragContainer container,DraggableActivity target){
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

            if(target.getIngoingIntentsForType(IntentType.forResult).size() == 0 &&
                    (target.getType()==DragControllerType.emptyActivity ||
                            target.getType()==DragControllerType.basicActivity )){
                MenuItem item2 = new MenuItem("Button Click With Result");
                item2.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        IntentType intentType = IntentType.forResult;
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
