package template.sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import template.ProjectHandler;
import template.attributeInspector.BottomNavigationActivityAttributes;
import template.attributeInspector.TabbedActivityAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by utente on 17/02/2018.
 */
public class BottomNavigationActivity extends DraggableActivity {
    private String classTemplate;
    private String layoutTemplate;

    private BottomNavigationActivityAttributes activityInspector= null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private List<BottomNavigationIntent> tabs = new ArrayList<BottomNavigationIntent>();

    public BottomNavigationActivity() throws IOException {
        super();
        //get activity and layout templates in String
        classTemplate = codeGenerator.provideTemplateForName("templates/BottomNavigationActivity");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/BottomNavigationLayout");
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
        activityInspector = new BottomNavigationActivityAttributes();
        super.setName("NewBottomNavigationActivity");
        loadInspector();
    }

    public List<BottomNavigationIntent> getTabs() {
        return tabs;
    }

    public void setTabs(List<BottomNavigationIntent> tabs) {
        this.tabs = tabs;
    }
    /*public void updateTabs(){
        List<BottomNavigationIntent> updatedTabs = new ArrayList<BottomNavigationIntent>();
        //get current tabs
        for (Intent i : super.getOutgoingIntentsForType(IntentType.bottomNavigIntent)){
            updatedTabs.add((BottomNavigationIntent) i);
        }
        //delete removed tabs
        for(BottomNavigationIntent i:tabs){
            if(!updatedTabs.contains(i)){
                tabs.remove(i);
            }
        }
        //add new tabs
        for(BottomNavigationIntent i:updatedTabs){
            if(!tabs.contains(i)){
                tabs.add(i);
            }
        }
    }*/
    public void removeTab(BottomNavigationIntent tab){
        tabs.remove(tab);
    }

    public void addTab(BottomNavigationIntent tab){
        tabs.add(tab);
    }
    public int getOrder(BottomNavigationIntent tab){
        int order = tabs.indexOf(tab);
        return order;
    }
    public void changeOrder(int oldValue, int newValue){
        BottomNavigationIntent tab = tabs.get(newValue);
        BottomNavigationIntent changingTab = tabs.get(oldValue);
        tabs.remove(tab);
        tabs.remove(changingTab);
        if(newValue<oldValue){
            tabs.add(newValue,changingTab);
            tabs.add(oldValue,tab);
        }else{
            tabs.add(oldValue,tab);
            tabs.add(newValue,changingTab);
        }
        tab.fillValues(tab);

    }

    @Override
    public String createJavaCode() throws IOException {
        String template = classTemplate;
        String tabs = "";
        String imports ="";

        for(Intent i : super.getOutgoingIntentsForType(IntentType.bottomNavigIntent)){
            tabs = tabs.concat(((BottomNavigationIntent)i).getIntentCode()+"\n");
            //if it is the first tab
            if(((BottomNavigationIntent)i).getOrder()==0){
                template = template.replace("${FRAGMENT}",i.getBelongingLink().getTarget().getName());
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
        template = template.replace("${ITEMS}",tabs);

        if (!receivers.equals("")){
            if(super.getOutgoingIntentsForType(IntentType.fabClick).size()==0){
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
    public String createXMLCode() throws IOException {
        String template = layoutTemplate;
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        return template;
    }
    public String createNavigationMenu() throws IOException {
        String items ="";
        for(Intent i : super.getOutgoingIntentsForType(IntentType.bottomNavigIntent)) {
            items = items.concat(((BottomNavigationIntent) i).getNavigationMenuCode() + "\n");
        }
        return items;
    }

    public List<String> getIcons(){
        //return a list of all the icons used by bottom navigation activity
        List<String> icons = new ArrayList<String>();
        for(Intent i : super.getOutgoingIntentsForType(IntentType.bottomNavigIntent)) {
            String icon = ((BottomNavigationIntent) i).getIconId();
            if(!icons.contains(icon)){
                icons.add(icon);
            }
        }

        return icons;
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
        String manifest = codeGenerator.provideTemplateForName("templates/ManifestActivity");
        manifest = manifest.replace("${ACTIVITY}",super.getName());

        attributes = attributes.concat(" android:label=\""+super.getName()+"\"");
        if (IsInitialActivity.getInstance().isInitialActivity(this)){
            manifest = manifest.replace("${INTENT_FILTER}","\n"+codeGenerator.provideTemplateForName("templates/IntentFilterLauncher")+"\n");
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
        if(target.getIngoingLinks().size()==0 && target.getType()!=DragControllerType.tabbedActivity
                && target.getType()!=DragControllerType.loginActivity
                && target.getType()!=DragControllerType.bottomNavigationActivity){
            //if target isn't already a tab or it isn't target of other intents (i.e. it couldn't be a fragment)
            //and target isn't a tabbed activity or login activity or bottom navigation activity

            MenuItem item1 = new MenuItem("BottomTab");
            item1.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    IntentType intentType = IntentType.bottomNavigIntent;
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
