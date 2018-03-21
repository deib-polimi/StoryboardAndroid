package template.managers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import template.intent.ButtonClickWithResultIntent;
import template.appInterface.*;
import template.intent.Intent;
import template.utils.DragControllerType;
import template.utils.IntentType;

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
        if(activity.isFragment()){
            //if activity is fragment, populate attribute inspector with a VBOX containing
            //the fragment inspector and the inspector of the relative tab intent
            VBox fragmentInspectorList = new VBox();
            fragmentInspectorList.getChildren().add(activityInspector);
            for (Intent i : activity.getIngoingIntents()){
                if (i.getType()== IntentType.tabIntent || i.getType()==IntentType.bottomNavigIntent){
                    fragmentInspectorList.getChildren().add(i.getIntentInspector());
                }
            }
            activityTab.setContent(fragmentInspectorList);

        }else{
            activityTab.setContent(activityInspector);
        }

        if(activity.getIntents().size()>0){
            if(activity.getType()!= DragControllerType.tabbedActivity && activity.getType()!= DragControllerType.bottomNavigationActivity){
                VBox intentInspectorsList = new VBox();
                for(Intent i : activity.getOutgoingIntents()) {
                    //create inspector section for each intent of the activity
                    intentInspectorsList.getChildren().add(i.getIntentInspector());
                    intentInspectorsList.getChildren().add(new Separator());
                }
                if(activity.getIngoingIntentsForType(IntentType.forResult).size()==1){
                    ButtonClickWithResultIntent i =(ButtonClickWithResultIntent) activity.getIngoingIntentsForType(IntentType.forResult).get(0);
                    intentInspectorsList.getChildren().add(i.getResultInspector());
                    intentInspectorsList.getChildren().add(new Separator());
                }
                intentTab.setContent(intentInspectorsList);

            }else{
                VBox tabsInspectorsList = new VBox();
                for(Intent i : activity.getOutgoingIntents()){
                    //create inspector section for each tab of the activity
                    tabsInspectorsList.getChildren().add(new Label("Content: "+i.getBelongingLink().getTarget().getName()));
                    tabsInspectorsList.getChildren().add(i.getIntentInspector());
                    tabsInspectorsList.getChildren().add(new Separator());

                }
                intentTab.setContent(tabsInspectorsList);
            }

        }
        //load tabs to attribute inspector
        attributeInspector.getTabs().add(activityTab);
        attributeInspector.getTabs().add(intentTab);


    }
    public void loadIntentInspector (Node intentInspector,Intent intent){
        //clean attribute inspector
        cleanAttributeInspector();
        //create tabs
        Tab intentTab = new Tab ("intents");
        //populate tabs
        if(intent.getType() ==IntentType.tabIntent || intent.getType() ==IntentType.bottomNavigIntent){
            VBox tabIntentList = new VBox();
            tabIntentList.getChildren().add(new Label("Content: "+intent.getBelongingLink().getTarget().getName()));
            tabIntentList.getChildren().add(intentInspector);
            intentTab.setContent(tabIntentList);
        }else if (intent.getType() == IntentType.forResult){
            VBox list = new VBox();
            list.getChildren().add(intentInspector);
            list.getChildren().add(new Separator());
            list.getChildren().add(((ButtonClickWithResultIntent)intent).getResultInspector());
            intentTab.setContent(list);
        }
        else{
            intentTab.setContent(intentInspector);
        }

        //load tabs to attribute inspector
        attributeInspector.getTabs().add(intentTab);

    }

    public void loadLinkInspector(Link link){
        //clean attribute inspector
        cleanAttributeInspector();
        //create tabs
        Tab linkTab = new Tab ("intents");
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
