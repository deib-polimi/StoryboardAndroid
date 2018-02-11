package template.sample;

import com.google.common.io.Resources;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import template.ProjectHandler;
import template.attributeInspector.EmptyActivityAttributes;
import template.managers.AttributeInspectorManager;
import template.sample.DraggableActivity;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by utente on 03/02/2018.
 */
public class EmptyActivity extends DraggableActivity {
    private String classTemplate;
    private String layoutTemplate;
    private EmptyActivityAttributes activityInspector= null;
    private CodeGenerator codeGenerator = new CodeGenerator();

    public EmptyActivity() throws IOException {
        super();
        //get activity and layout templates in String
        classTemplate = codeGenerator.provideTemplateForName("templates/EmptyActivity");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/EmptyActivityLayout");
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

    @FXML
    private void initialize() {
        super.init();
        activityInspector = new EmptyActivityAttributes();
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
                declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n\t");
                setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) findViewById(R.id."
                        +((ButtonClickIntent)i).getButtonId()+"_button);\n\t\t");
                intent = intent.concat(((ButtonClickIntent)i).getIntentCode()+"\n\t\t");
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
    public void loadInspector(){
        AttributeInspectorManager inspectorManager = AttributeInspectorManager.getInstance();
        activityInspector.fillValues(this);
        inspectorManager.loadActivityInspector(activityInspector,this);
    }

    @Override
    public void isInitialActivity(boolean isInitial){
        activityInspector.setCheckBox(isInitial);
    }

    @Override
    public String getManifest() throws IOException {
        String manifest = codeGenerator.provideTemplateForName("templates/ManifestActivity");
        manifest = manifest.replace("${ACTIVITY}",super.getName());
        if (IsInitialActivity.getInstance().isInitialActivity(this)){
            manifest = manifest.replace("${INTENT_FILTER}","\n"+codeGenerator.provideTemplateForName("templates/IntentFilterLauncher")+"\n\t\t");
        }else {
            manifest = manifest.replace("${INTENT_FILTER}","");
        }
        return manifest;
    }

    //generate layout name from object name. Ex: MyClass -> _my_class
    private String generateLayoutName(String objectName){
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
        return out;
    }



}
