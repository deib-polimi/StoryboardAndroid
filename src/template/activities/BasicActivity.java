package template.activities;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import template.appInterface.RootLayout;
import template.codeGenerator.CodeGenerator;
import template.intent.*;
import template.managers.ProjectHandler;
import template.attributeInspector.EmptyActivityAttributes;
import template.attributeInspector.FragmentAttributes;
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
        classTemplate = codeGenerator.provideTemplateForName("templates/activityTemplates/BasicActivity");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/activityTemplates/BasicActivityLayout");
        fragmentTemplate = codeGenerator.provideTemplateForName("templates/activityTemplates/BasicFragment");
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
        super.setName("BasicActivity"
                + GraphHandler.getInstance().getActivityIndex(DragControllerType.basicActivity,"BasicActivity"));
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

        if((super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0)||(super.getOutgoingIntentsForType(IntentType.fabClick).size()>0)||
                super.getOutgoingIntentsForType(IntentType.forResult).size()>0){
            imports = imports.concat(Imports.INTENT+"\n");
        }
        if (super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0 ||
                super.getOutgoingIntentsForType(IntentType.forResult).size()>0 ||
                super.getIngoingIntentsForType(IntentType.forResult).size()>0){
            //set imports
            imports =imports.concat(Imports.BUTTON+"\n");

        }
        for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
            //set buttons declarations
            declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n");
            if(!((ButtonClickIntent) i).getExtraType().equals("None")){
                extraId = extraId.concat(((ButtonClickIntent)i).getExtraIdDeclaration()+"\n");
            }
            buttonClickintent = buttonClickintent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");

            setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) findViewById(R.id."
                    +((ButtonClickIntent)i).getButtonId()+"_button);\n");
        }

        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){

            Intent intent = super.getOutgoingIntentsForType(IntentType.fabClick).get(0);
            if(!intent.getExtraType().equals("None")){
                extraId = extraId.concat(((FABIntent)intent).getExtraIdDeclaration()+"\n");
            }
            fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode()+"\n");
            template = template.replace("${INTENT}",fabIntent);
        }else{
            String emptyFAB =codeGenerator.provideTemplateForName("templates/intentTemplates/FABIntentEmpty");
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

        //onActivityResult
        String results ="";
        for (Intent i :super.getOutgoingIntentsForType(IntentType.forResult)){
            results = results.concat(((ButtonClickWithResultIntent)i).getOnResultCode()+"\n");
            declarations = declarations.concat("private Button "+((ButtonClickWithResultIntent)i).getButtonId()+";\n");
            if(!((ButtonClickWithResultIntent) i).getExtraType().equals("None")){
                extraId = extraId.concat(((ButtonClickWithResultIntent)i).getExtraIdDeclaration()+"\n");
            }
            buttonClickintent = buttonClickintent.concat(((ButtonClickWithResultIntent)i).getIntentCode()+"\n");
            setViews = setViews.concat(((ButtonClickWithResultIntent)i).getButtonId()+" = (Button) findViewById(R.id."
                    +((ButtonClickWithResultIntent)i).getButtonId()+"_button);\n");

        }
        if(!results.equals("")){
            template = template.replace("${ON_RESULT}","\n"+codeGenerator.provideTemplateForName("templates/activityTemplates/ButtonClickOnResult"));
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
            buttonClickintent = buttonClickintent.concat(((ButtonClickWithResultIntent)i).getResultSetCode()+"\n");
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

        template = template.replace("${DECLARATIONS}",declarations);
        template = template.replace("${SET_VIEWS}",setViews);
        template = template.replace("${BUTTON_CLICK_INTENT}",buttonClickintent);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());

        template = template.replace("${INTENT_EXTRA_ID}","\n"+extraId);
        if (!receivers.equals("")){
            if(super.getOutgoingIntentsForType(IntentType.buttonClick).size()==0 && super.getOutgoingIntentsForType(IntentType.fabClick).size()==0){
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
        String buttonClickintent = "";
        String fabIntent = "";
        String extraId="";
        //create code of the button click intents outgoing from the activity

        if((super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0)||(super.getOutgoingIntentsForType(IntentType.fabClick).size()>0)||
                super.getOutgoingIntentsForType(IntentType.forResult).size()>0){
            imports = imports.concat(Imports.INTENT+"\n");
        }
        if (super.getOutgoingIntentsForType(IntentType.buttonClick).size()>0 ||
                super.getOutgoingIntentsForType(IntentType.forResult).size()>0){
            //set imports
            imports =imports.concat(Imports.BUTTON+"\n");
            for(Intent i : super.getOutgoingIntentsForType(IntentType.buttonClick)){
                //set buttons declarations
                declarations = declarations.concat("private Button "+((ButtonClickIntent)i).getButtonId()+";\n");
                if(!((ButtonClickIntent) i).getExtraType().equals("None")){
                    extraId = extraId.concat(((ButtonClickIntent)i).getExtraIdDeclaration()+"\n");
                }
                buttonClickintent = buttonClickintent.concat(((ButtonClickIntent)i).getIntentCode()+"\n");

                setViews = setViews.concat(((ButtonClickIntent)i).getButtonId()+" = (Button) view.findViewById(R.id."
                        +((ButtonClickIntent)i).getButtonId()+"_button);\n");
            }
        }
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){

            Intent intent = super.getOutgoingIntentsForType(IntentType.fabClick).get(0);
            if(!intent.getExtraType().equals("None")){
                extraId = extraId.concat(((FABIntent)intent).getExtraIdDeclaration()+"\n");
            }
            fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode()+"\n");
            template = template.replace("${INTENT}",fabIntent);
        }else{
            String emptyFAB =codeGenerator.provideTemplateForName("templates/intentTemplates/FABIntentEmpty");
            emptyFAB = emptyFAB.replace("${GET_VIEW}","view.");
            template = template.replace("${INTENT}",emptyFAB);
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
            buttonClickintent = buttonClickintent.concat(((ButtonClickWithResultIntent)i).getIntentCode()+"\n");
            setViews = setViews.concat(((ButtonClickWithResultIntent)i).getButtonId()+" = (Button) view.findViewById(R.id."
                    +((ButtonClickWithResultIntent)i).getButtonId()+"_button);\n");

        }
        if(!results.equals("")){
            template = template.replace("${ON_RESULT}","\n"+codeGenerator.provideTemplateForName("templates/activityTemplates/ButtonClickOnResult"));
            template = template.replace("${VISIBILITY}","public");

            template = template.replace("${RESULTS}",results);
        }else{
            template = template.replace("${ON_RESULT}","");
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
    public String createXMLCode() throws IOException {
        String template = layoutTemplate;
        String buttons="";

        if((super.getOutgoingIntentsForType(IntentType.buttonClick).size() +
                super.getOutgoingIntentsForType(IntentType.forResult).size()+
                super.getIngoingIntentsForType(IntentType.forResult).size())>1){
            template = template.replace("${BUTTONS}",codeGenerator.provideTemplateForName("templates/activityTemplates/LinearLayout"));
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

        }else if(super.getIngoingIntentsForType(IntentType.forResult).size() == 1){
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
        String manifest = codeGenerator.provideTemplateForName("templates/activityTemplates/ManifestActivity");
        String attributes ="";
        manifest = manifest.replace("${ACTIVITY}",super.getName());

        attributes = attributes.concat(" android:label=\""+super.getName()+"\"");
        if (IsInitialActivity.getInstance().isInitialActivity(this)){
            manifest = manifest.replace("${INTENT_FILTER}","\n"+codeGenerator.provideTemplateForName("templates/activityTemplates/IntentFilterLauncher")+"\n");
        }else {
            manifest = manifest.replace("${INTENT_FILTER}","");
        }

        //up navigation
        if(super.getIngoingIntentsForType(IntentType.itemClick).size()>0){
            AdapterViewItemClick i = (AdapterViewItemClick)super.getIngoingIntentsForType(IntentType.itemClick).get(0);
            if(i.getBelongingLink().getSource().isFragment()){
                attributes = attributes.concat("\nandroid:parentActivityName=\"."+getContainerActivity(i.getBelongingLink().getSource()).getName()+"\"");
            }else{
                attributes = attributes.concat("\nandroid:parentActivityName=\"."+i.getBelongingLink().getSource().getName()+"\"");
            }
        } else if(super.getIngoingIntentsForType(IntentType.cardClick).size()>0){
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
    public java.util.List<MenuItem> getMenuItems(RootLayout root, DragContainer container, DraggableActivity target){
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
            if(target.getIngoingIntentsForType(IntentType.forResult).size() == 0 &&
                    (target.getType()==DragControllerType.emptyActivity ||
                            target.getType()==DragControllerType.basicActivity )){
                MenuItem item3 = new MenuItem("Button Click With Result");
                item3.setOnAction(new EventHandler<ActionEvent>() {

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
                items.add(item3);
            }
        }

        return items;
    };

}
