package template.managers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kotlin.reflect.jvm.internal.impl.util.ModuleVisibilityHelper;
import template.attributeInspector.EmptyActivityAttributes;
import template.sample.DraggableActivity;
import template.sample.Intent;
import template.sample.IsInitialActivity;
import template.sample.Link;

public class AttributeInspectorManager {

    private TabPane attributeInspector;

    private static AttributeInspectorManager instance = null;

    public static AttributeInspectorManager getInstance() {

        if(instance == null) {
            instance = new AttributeInspectorManager();
        }
        return instance;
    }

    private AttributeInspectorManager() {
    }

    public TabPane getAttributeInspector() {
        return attributeInspector;
    }

    public void setAttributeInspector(TabPane attributeInspector) {

        this.attributeInspector = attributeInspector;
    }



    public void loadActivityInspector (Node activityInspector,DraggableActivity activity){
        //clean attribute inspector
        cleanAttributeInspector();
        //create tab
        Tab activityTab = new Tab("Activity");
        Tab intentTab = new Tab ("Intent");
        //populate tabs
        activityTab.setContent(activityInspector);
        if(activity.getIntents().size()>0){
            VBox intentInspectorsList = new VBox();
            for(Intent i : activity.getOutgoingIntents()){
                //create inspector section for each intent of the activity
                intentInspectorsList.getChildren().add(i.getIntentInspector());
                intentInspectorsList.getChildren().add(new Separator());

            }
            intentTab.setContent(intentInspectorsList);
        }
        //load tabs to attribute inspector
        attributeInspector.getTabs().add(activityTab);
        attributeInspector.getTabs().add(intentTab);

    }
    public void loadIntentInspector (Node intentInspector){
        //clean attribute inspector
        cleanAttributeInspector();
        //create tabs
        Tab intentTab = new Tab ("Intents");
        //populate tabs
        intentTab.setContent(intentInspector);
        //load tabs to attribute inspector
        attributeInspector.getTabs().add(intentTab);

    }

    public void loadLinkInspector(Link link){
        //clean attribute inspector
        cleanAttributeInspector();
        //create tabs
        Tab linkTab = new Tab ("Intents");
        //populate tabs
        VBox intentInspectorsList = new VBox();
        intentInspectorsList.getChildren().add(new Label(link.getSource().getName()+" --> "+link.getTarget().getName()));
        for(Intent i : link.getIntentsList()){
            intentInspectorsList.getChildren().add(i.getIntentInspector());
            intentInspectorsList.getChildren().add(new Separator());
        }
        linkTab.setContent(intentInspectorsList);
        //load tabs to attribute inspector
        attributeInspector.getTabs().add(linkTab);

    }

    public void replaceInspectorAfterDelete(){
        if (IsInitialActivity.getInstance().getInitialActivity()!=null){
            //load inictial activity's attribute inspector
            IsInitialActivity.getInstance().getInitialActivity().loadInspector();
        }
        else if(StructureTreeManager.getInstance().getRootItem().getChildren().size()>0){
            StructureTreeManager treeManager = StructureTreeManager.getInstance();
            //retrieve id of the first activity in the tree
            String id =treeManager.getRootItem().getChildren().get(0).getValue().getId();
            //retrieve the activity and load its attribute inspector
            DraggableActivity activity = (DraggableActivity) treeManager.searchById(id);
            activity.loadInspector();
        }else{
            // if there aren't activities in the tree clean the attribute inspector
            cleanAttributeInspector();
        }
    }
    private void cleanAttributeInspector(){
        ObservableList<Tab> tabs = attributeInspector.getTabs();
        attributeInspector.getTabs().removeAll(tabs);
    }


}
