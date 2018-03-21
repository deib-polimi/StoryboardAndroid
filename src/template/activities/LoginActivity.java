package template.activities;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import template.appInterface.RootLayout;
import template.codeGenerator.CodeGenerator;
import template.intent.AdapterViewItemClick;
import template.intent.CardViewItemClick;
import template.intent.Intent;
import template.intent.LoginIntent;
import template.managers.ProjectHandler;
import template.attributeInspector.EmptyActivityAttributes;
import template.managers.AttributeInspectorManager;
import template.managers.GraphHandler;
import template.appInterface.*;
import template.utils.DragControllerType;
import template.utils.Imports;
import template.utils.IntentType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginActivity extends DraggableActivity {
    private String classTemplate;
    private String layoutTemplate;
    private EmptyActivityAttributes activityInspector= null;
    private CodeGenerator codeGenerator = new CodeGenerator();

    public LoginActivity() throws IOException {
        super();
        //get activity and layout templates in String
        classTemplate = codeGenerator.provideTemplateForName("templates/activityTemplates/LoginActivity");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/activityTemplates/LoginActivityLayout");
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
        super.setName("LoginActivity"
                + GraphHandler.getInstance().getActivityIndex(DragControllerType.loginActivity,"LoginActivity"));
        loadInspector();
    }

    @Override
    public String createJavaCode() throws IOException {
        String template = classTemplate;
        String imports = "";
        String intent = "";
        String extraId="";
        //create code of the button click intents outgoing from the activity
        if (super.getOutgoingIntentsForType(IntentType.loginClick).size()>0){
            //set imports
            imports = imports.concat(Imports.INTENT+"\n");
            Intent i = super.getOutgoingIntentsForType(IntentType.loginClick).get(0);
            //set intent
            if(!((LoginIntent) i).getExtraType().equals("None")){
                extraId = extraId.concat(((LoginIntent)i).getExtraIdDeclaration()+"\n");

            }
            intent = intent.concat(((LoginIntent)i).getIntentCode()+"\n");
            template = template.replace("${INTENT}",intent);
        }else{
            template = template.replace("${INTENT}","");
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

        //up navigation
        if(super.getIngoingIntentsForType(IntentType.itemClick).size()>0 ||
                super.getIngoingIntentsForType(IntentType.cardClick).size()>0 ){
            template = template.replace("${UP_NAVIGATION}","getSupportActionBar().setDisplayHomeAsUpEnabled(true);"+"\n");
        }else{
            template = template.replace("${UP_NAVIGATION}","");
        }

        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());
        template = template.replace("${INTENT_EXTRA_ID}","\n"+extraId);
        if (!receivers.equals("")){
            if(super.getOutgoingIntentsForType(IntentType.loginClick).size()==0){
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
        String attributes ="";
        String manifest = codeGenerator.provideTemplateForName("templates/activityTemplates/ManifestActivity");
        manifest = manifest.replace("${ACTIVITY}",super.getName());

        attributes = attributes.concat(" android:label=\""+super.getName()+"\"");
        if (IsInitialActivity.getInstance().isInitialActivity(this)){
            manifest = manifest.replace("${INTENT_FILTER}","\n"+codeGenerator.provideTemplateForName("templates/activityTemplates/IntentFilterLauncher")+"\n\t\t");
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

        return "activity"+out;

    }

    @Override
    public java.util.List<MenuItem> getMenuItems(RootLayout root, DragContainer container, DraggableActivity target){
        List<MenuItem> items = new ArrayList<MenuItem>();
        //allow only one login intent
        if(!target.isFragment())
        {
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
        }

        return items;
    };
}
