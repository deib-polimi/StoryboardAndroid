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
    public String createJavaCode(){
        String template = classTemplate;
        String imports = "";
        String declarations = "";
        String setViews = "";
        String intent = "";
        //create code of the button click intents outgoing from the activity
        if (super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0){
            //set imports
            imports =imports.concat(Imports.BUTTON+"\n");
            imports = imports.concat(Imports.INTENT+"\n");
            imports = imports.concat(Imports.VIEW+"\n");
            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                //set buttons declarations
                declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n");
                setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) findViewById(R.id."
                        +((ButtonClickIntent)i).getButtonId()+"_button);\n");
                intent = intent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");
            }
        }

        template = template.replace("${IMPORTS}",imports);
        template = template.replace("${DECLARATIONS}",declarations);
        template = template.replace("${SET_VIEWS}",setViews);
        template = template.replace("${BUTTON_CLICK_INTENT}",intent);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        return template;
    }

    @Override
    public String createFragmentCode() throws IOException {
        String template = fragmentTemplate;
        String imports = "";
        String declarations = "";
        String setViews = "";
        String intent = "";
        //create code of the button click intents outgoing from the activity
        if (super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0){
            //set imports
            imports =imports.concat(Imports.BUTTON+"\n");
            imports = imports.concat(Imports.INTENT+"\n");
            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                //set buttons declarations
                declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n");
                setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) getView().findViewById(R.id."
                        +((ButtonClickIntent)i).getButtonId()+"_button);\n");
                intent = intent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");
            }
        }

        template = template.replace("${IMPORTS}",imports);
        template = template.replace("${DECLARATIONS}",declarations);
        template = template.replace("${SET_VIEWS}",setViews);
        template = template.replace("${BUTTON_CLICK_INTENT}",intent);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        return template;
    }
    public String createXMLCode() throws IOException {
        String template = layoutTemplate;
        String buttons="";
        if(super.getOutgoingIntentsForType(IntentType.buttonClick).size() == 1){
            Intent i = super.getOutgoingIntentsForType(IntentType.buttonClick).get(0);
            buttons = buttons.concat(((ButtonClickIntent)i).getIntentLayoutCode()+"\n");
        }else if(super.getOutgoingIntentsForType(IntentType.buttonClick).size() > 1){
            String LinearLayout = codeGenerator.provideTemplateForName("templates/LinearLayout");
            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                buttons = buttons.concat(((ButtonClickIntent)i).getIntentLayoutCode()+"\n");
            }
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
        manifest = manifest.replace("${ATTRIBUTES}"," android:label=\""+super.getName()+"\"");
        if (IsInitialActivity.getInstance().isInitialActivity(this)){
            manifest = manifest.replace("${INTENT_FILTER}","\n"+codeGenerator.provideTemplateForName("templates/IntentFilterLauncher")+"\n\t\t");
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
        }

        return items;
    };



}
