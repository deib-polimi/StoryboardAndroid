package template.sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import template.ProjectHandler;
import template.attributeInspector.CardViewAttributes;
import template.attributeInspector.CardViewFragmentAttributes;
import template.attributeInspector.GridViewAttributes;
import template.attributeInspector.GridViewFragmentAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by utente on 21/02/2018.
 */
public class CardViewActivity extends DraggableActivity {

    private String classTemplate;
    private String layoutTemplate;
    private String adapterTemplate;
    private String modelTemplate;
    private String classFragment;
    private String modelLayout;
    private String itemClickListenerTemplate;
    private CardViewAttributes activityInspector= null;
    private CardViewFragmentAttributes fragmentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String layout;
    private int columns;

    public CardViewActivity() throws IOException {
        super();
        //get activity and layout templates in String
        classTemplate = codeGenerator.provideTemplateForName("templates/CardViewActivity");
        classFragment = codeGenerator.provideTemplateForName("templates/CardViewFragment");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/CardViewLayout");
        adapterTemplate = codeGenerator.provideTemplateForName("templates/CardViewAdapter");
        modelTemplate = codeGenerator.provideTemplateForName("templates/CustomAdapterModel");
        modelLayout = codeGenerator.provideTemplateForName("templates/CardViewItemLayout");
        itemClickListenerTemplate = codeGenerator.provideTemplateForName("templates/CardViewClickListener");

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
        activityInspector = new CardViewAttributes();
        fragmentInspector = new CardViewFragmentAttributes();
        super.setName("NewCardView");
        layout = "List";
        columns = 2;
        loadInspector();
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    @Override
    public String createJavaCode() throws IOException {
        String template = classTemplate;
        String imports = "";
        String cardClick = "";
        String extraId="";

        if (layout.equals("List")){
            template = template.replace("${COLUMNS}","");
            template = template.replace("${TYPE}","Linear");
        }else if(layout.equals("Grid")){
            template = template.replace("${COLUMNS}",","+Integer.toString(columns));
            template = template.replace("${TYPE}","Grid");
        }

        //check if there is a fab intent
        String fabIntent="";
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){

            Intent intent = super.getOutgoingIntentsForType(IntentType.fabClick).get(0);
            if(!intent.getExtraType().equals("None")){
                extraId = extraId.concat(((FABIntent)intent).getExtraIdDeclaration()+"\n");
            }
            fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode()+"\n");
            imports = imports.concat(Imports.FAB);

        }
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0 ||
                super.getOutgoingIntentsForType(IntentType.cardClick).size()>0){
            imports = imports.concat(Imports.VIEW+"\n");
        }
        template = template.replace("${FAB}",fabIntent);

        if(super.getOutgoingIntentsForType(IntentType.cardClick).size()>0){

            Intent intent = super.getOutgoingIntentsForType(IntentType.cardClick).get(0);
            if(!intent.getExtraType().equals("None")){
                extraId = extraId.concat(((CardViewItemClick)intent).getExtraIdDeclaration()+"\n");
            }
            cardClick = cardClick.concat(((CardViewItemClick)intent).getIntentCode());
            generateClickListener();
            imports = imports.concat(Imports.CLICK_LISTENER+"\n");
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

        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0 ||
                super.getOutgoingIntentsForType(IntentType.cardClick).size()>0 ||
                !receivers.equals("")){
            imports = imports.concat(Imports.INTENT);
        }
        template = template.replace("${IMPORTS}",imports+"\n");
        template = template.replace("${ON_CLICK}","\n"+cardClick+"\n");
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());

        template = template.replace("${INTENT_EXTRA_ID}","\n"+extraId);
        if (!receivers.equals("")){
            template = template.replace("${INTENT_RECEIVER}","Intent intent = getIntent();\n"+receivers);
        }else{
            template = template.replace("${INTENT_RECEIVER}",receivers);
        }

        generateAdapter();
        generateModel();

        return template;
    }

    @Override
    public String createFragmentCode() throws IOException {
        String template = classFragment;
        String imports = "";
        String cardClick = "";
        String extraId="";

        if (layout.equals("List")){
            template = template.replace("${COLUMNS}","");
            template = template.replace("${TYPE}","Linear");
        }else if(layout.equals("Grid")){
            template = template.replace("${COLUMNS}",","+Integer.toString(columns));
            template = template.replace("${TYPE}","Grid");
        }

        //check if there is a fab intent
        String fabIntent="";
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){

            Intent intent = super.getOutgoingIntentsForType(IntentType.fabClick).get(0);
            if(!intent.getExtraType().equals("None")){
                extraId = extraId.concat(((FABIntent)intent).getExtraIdDeclaration()+"\n");
            }
            fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode()+"\n");
            imports = imports.concat(Imports.FAB);

        }
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0 ||
                super.getOutgoingIntentsForType(IntentType.cardClick).size()>0){
            imports = imports.concat(Imports.VIEW+"\n");
        }
        template = template.replace("${FAB}",fabIntent);

        if(super.getOutgoingIntentsForType(IntentType.cardClick).size()>0){

            Intent intent = super.getOutgoingIntentsForType(IntentType.cardClick).get(0);
            if(!intent.getExtraType().equals("None")){
                extraId = extraId.concat(((CardViewItemClick)intent).getExtraIdDeclaration()+"\n");
            }
            cardClick = cardClick.concat(((CardViewItemClick)intent).getIntentCode());
            generateClickListener();
            imports = imports.concat(Imports.CLICK_LISTENER+"\n");
        }
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0 ||
                super.getOutgoingIntentsForType(IntentType.cardClick).size()>0){
            imports = imports.concat(Imports.INTENT);
        }
        template = template.replace("${IMPORTS}",imports+"\n");
        template = template.replace("${ON_CLICK}","\n"+cardClick+"\n");
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());
        template = template.replace("${INTENT_EXTRA_ID}","\n"+extraId);

        generateAdapter();
        generateModel();

        return template;
    }

    public void generateAdapter() throws IOException {
        String template = adapterTemplate;
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ITEM_LAYOUT}",generateLayoutName(super.getName())+"_card");
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        codeGenerator.generateJavaFile(projectHandler.getProjectPath()+"/app/src/main/java/"
                +projectHandler.getPackagePath()+"/adapter/"+super.getName()+"Adapter.java",template);
    }

    public void generateModel() throws IOException {
        String template = modelTemplate;
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        codeGenerator.generateJavaFile(projectHandler.getProjectPath()+"/app/src/main/java/"
                +projectHandler.getPackagePath()+"/adapter/"+super.getName()+"Model.java",template);
    }

    public void generateClickListener() throws IOException {
        String template = itemClickListenerTemplate;
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        codeGenerator.generateJavaFile(projectHandler.getProjectPath()+"/app/src/main/java/"
                +projectHandler.getPackagePath()+"/adapter/"+super.getName()+"ClickListener.java",template);
    }

    public String createXMLCode() throws IOException {
        String template = layoutTemplate;
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){
            template = template.replace("${FAB}","\n"+codeGenerator.provideTemplateForName("templates/FABIntentLayout")+"\n");
        }else{
            template = template.replace("${FAB}","");
        }

        generateCardLayout();

        return template;
    }

    public void generateCardLayout() throws IOException {
        String template = modelLayout;
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        codeGenerator.generateXMLFile(projectHandler.getProjectPath()+"/app/src/main/res/layout/"
                +generateLayoutName(super.getName())+"_card.xml",template);
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
    public java.util.List<MenuItem> getMenuItems(RootLayout root, DragContainer container, DraggableActivity target){
        List<MenuItem> items = new ArrayList<MenuItem>();
        if(!target.isFragment()){

            if(super.getOutgoingIntentsForType(IntentType.fabClick).size() ==0){
                MenuItem item1 = new MenuItem("FAB Intent");
                item1.setOnAction(new EventHandler<ActionEvent>() {

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
                items.add(item1);
            }
            if(super.getOutgoingIntentsForType(IntentType.cardClick).size() ==0 &&
                    target.getIngoingIntentsForType(IntentType.itemClick).size() ==0 &&
                    target.getIngoingIntentsForType(IntentType.cardClick).size() ==0){
                MenuItem item2 = new MenuItem("Card Click");
                item2.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        IntentType intentType = IntentType.cardClick;
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
