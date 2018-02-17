package template.sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import template.ProjectHandler;
import template.attributeInspector.EmptyActivityAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by utente on 12/02/2018.
 */
public class LoginActivity extends DraggableActivity {
    private String classTemplate;
    private String layoutTemplate;
    private EmptyActivityAttributes activityInspector= null;
    private CodeGenerator codeGenerator = new CodeGenerator();

    public LoginActivity() throws IOException {
        super();
        //get activity and layout templates in String
        classTemplate = codeGenerator.provideTemplateForName("templates/LoginActivity");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/LoginActivityLayout");
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
        super.setName("NewLoginActivity");
        loadInspector();
    }

    @Override
    public String createJavaCode() throws IOException {
        String template = classTemplate;
        String imports = "";
        String intent = "";
        //create code of the button click intents outgoing from the activity
        if (super.getOutgoingIntentsForType(IntentType.loginClick).size()>0){
            //set imports
            imports = imports.concat(Imports.INTENT+"\n");
            Intent i = super.getOutgoingIntentsForType(IntentType.loginClick).get(0);
            //set intent
            intent = intent.concat(((LoginIntent)i).getIntentCode());
            template = template.replace("${INTENT}",intent);
        }else{
            template = template.replace("${INTENT}","");
        }

        template = template.replace("${IMPORTS}",imports);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());

        return template;
    }
    @Override
    public String createXMLCode(){
        String template = layoutTemplate;
        if (super.getOutgoingIntentsForType(IntentType.loginClick).size()>0){
            Intent i = super.getOutgoingIntentsForType(IntentType.loginClick).get(0);
            template = template.replace("${BUTTON_TXT}",((LoginIntent)i).getButtonTxt());
        }else{
            template = template.replace("${BUTTON_TXT}","Sign in or register");
        }
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());

        return template;
    }

    @Override
    public void loadInspectorListeners(){
        activityInspector.createListeners(this);
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
        return out;
    }

    @Override
    public java.util.List<MenuItem> getMenuItems(RootLayout root, DragContainer container,DraggableActivity target){
        List<MenuItem> items = new ArrayList<MenuItem>();
        //allow only one login intent
        if(super.getOutgoingIntentsForType(IntentType.loginClick).size() ==0){
            MenuItem item1 = new MenuItem("Login Intent");
            item1.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    IntentType intentType = IntentType.loginClick;
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
