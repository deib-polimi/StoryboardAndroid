package template.sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import template.ProjectHandler;
import template.attributeInspector.EmptyActivityAttributes;
import template.attributeInspector.TabbedActivityAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by utente on 14/02/2018.
 */
public class TabbedActivity extends DraggableActivity {
    private String classTemplate;
    private String layoutTemplate;

    private TabbedActivityAttributes activityInspector= null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private List<TabIntent> tabs = new ArrayList<TabIntent>();

    public TabbedActivity() throws IOException {
        super();
        //get activity and layout templates in String
        classTemplate = codeGenerator.provideTemplateForName("templates/TabbedActivity");
        layoutTemplate = codeGenerator.provideTemplateForName("templates/TabbedActivityLayout");
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
        activityInspector = new TabbedActivityAttributes();
        super.setName("NewTabbedActivity");
        loadInspector();
    }

    public List<TabIntent> getTabs() {
        return tabs;
    }

    public void setTabs(List<TabIntent> tabs) {
        this.tabs = tabs;
    }
    /*public void updateTabs(){
        List<TabIntent> updatedTabs = new ArrayList<TabIntent>();
        //get current tabs
        for (Intent i : super.getOutgoingIntentsForType(IntentType.tabIntent)){
            updatedTabs.add((TabIntent) i);
        }
        //delete removed tabs
        for(TabIntent i:tabs){
            if(!updatedTabs.contains(i)){
                tabs.remove(i);
            }
        }
        //add new tabs
        for(TabIntent i:updatedTabs){
            if(!tabs.contains(i)){
                tabs.add(i);
            }
        }


    }*/
    public void removeTab(TabIntent tab){
        tabs.remove(tab);
    }

    public void addTab(TabIntent tab){
        tabs.add(tab);
    }
    public int getOrder(TabIntent tab){
        int order = tabs.indexOf(tab);
        return order;
    }
    public void changeOrder(int oldValue, int newValue){
        TabIntent tab = tabs.get(newValue);
        TabIntent changingTab = tabs.get(oldValue);
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
        String imports = "";
        String tabs = "";
        String buttonClickintent = "";
        String fabIntent = "";
        //create code of the button click intents outgoing from the activity

        if(super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){
            imports = imports.concat(Imports.INTENT+"\n");
            imports = imports.concat(Imports.FAB+"\n");
            imports = imports.concat(Imports.VIEW+"\n");
            Intent intent = super.getOutgoingIntentsForType(IntentType.fabClick).get(0);
            fabIntent = fabIntent.concat(((FABIntent)intent).getIntentCode());
            template = template.replace("${FAB}",fabIntent);
        }else{
            template = template.replace("${FAB}","");
        }

        for(Intent i : super.getOutgoingIntentsForType(IntentType.tabIntent)){
            tabs = tabs.concat(((TabIntent)i).getIntentCode()+"\n");
        }

        template = template.replace("${IMPORTS}",imports);
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${ACTIVITY_LAYOUT}",generateLayoutName(super.getName()));
        template = template.replace("${PACKAGE}", ProjectHandler.getInstance().getPackage());
        template = template.replace("${TABS}",tabs);
        template = template.replace("${N_TABS}",Integer.toString(super.getOutgoingIntentsForType(IntentType.tabIntent).size()));

        return template;
    }
    @Override
    public String createXMLCode() throws IOException {
        String template = layoutTemplate;
        String tabItems="";
        for(Intent i : super.getOutgoingIntentsForType(IntentType.tabIntent)){
            tabItems = tabItems.concat(((TabIntent)i).getIntentLayoutCode()+"\n");
        }
        template = template.replace("${ACTIVITY_NAME}",super.getName());
        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        template = template.replace("${TABS}",tabItems);
        if (super.getOutgoingIntentsForType(IntentType.fabClick).size()>0){
            template = template.replace("${FAB}",codeGenerator.provideTemplateForName("templates/FABIntentLayout"));
        }else{
            template = template.replace("${FAB}","");
        }
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

        manifest = manifest.replace("${ATTRIBUTES}"," android:label=\""+this.getName()+"\"\n"
                +"android:theme=\"@style/AppTheme.NoActionBar\"");
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
        return "activity"+out;
    }

    @Override
    public java.util.List<MenuItem> getMenuItems(RootLayout root, DragContainer container,DraggableActivity target){
        List<MenuItem> items = new ArrayList<MenuItem>();
        if(target.getIngoingLinks().size()==0 && target.getType()!=DragControllerType.tabbedActivity
                && target.getType()!=DragControllerType.loginActivity
                && target.getType()!=DragControllerType.bottomNavigationActivity){
            //if target isn't already a tab or it isn't target of other intents (i.e. it couldn't be a fragment)
            //and target isn't a tabbed activity or login activity or bottom navigation activity

            MenuItem item1 = new MenuItem("Tab");
            item1.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    IntentType intentType = IntentType.tabIntent;
                    try {
                        root.createLink(container,intentType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            items.add(item1);
        }

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
        return items;
    };

}
