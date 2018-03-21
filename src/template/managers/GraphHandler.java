package template.managers;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import template.intent.ButtonClickIntent;
import template.intent.ButtonClickWithResultIntent;
import template.appInterface.*;
import template.intent.Intent;
import template.utils.DragControllerType;
import template.utils.IntentType;

import java.util.ArrayList;
import java.util.List;

public class GraphHandler {
    private static GraphHandler instance = null;
    private AnchorPane graph_pane;

    public GraphHandler() {
    }

    public static GraphHandler getInstance() {

        if(instance == null) {
            instance = new GraphHandler();
        }
        return instance;
    }

    public static void setInstance(GraphHandler instance) {
        GraphHandler.instance = instance;
    }

    public AnchorPane getGraph_pane() {
        return graph_pane;
    }

    public void setGraph_pane(AnchorPane graph_pane) {
        this.graph_pane = graph_pane;
    }

    public String getActivityIndex(DragControllerType activityType, String baseName){
        int i = 0;
        boolean available = false;
        String index = "";
        List<DraggableActivity> activities= getActivitiesByType(activityType);
        while(available == false){
            available = true;
            for(DraggableActivity a: activities){
                if(a.getName().equals(baseName+index)){
                    available = false;
                    i++;
                    index = Integer.toString(i);
                }
            }
        }
        return index;
    }

    public String getButtonIndex(DraggableActivity activity){
        int i = 0;
        boolean available = false;
        String index = "";
        List<Intent> buttons= getActivityButtons(activity);
        while(available == false){
            available = true;
            for(Intent intent :buttons){
                if(intent instanceof ButtonClickIntent){
                    if(((ButtonClickIntent)intent).getButtonId().equals("button"+index)){
                        available = false;
                        i++;
                        index = Integer.toString(i);
                    }
                }
                if(intent instanceof ButtonClickWithResultIntent){
                    if(((ButtonClickWithResultIntent)intent).getButtonId().equals("button"+index)){
                        available = false;
                        i++;
                        index = Integer.toString(i);
                    }
                }

            }
        }
        return index;
    }

    public List<DraggableActivity> getActivitiesByType(DragControllerType activityType){
        List<DraggableActivity> activities= new ArrayList<DraggableActivity>();
        for(Node n: graph_pane.getChildren()){
            if (n instanceof DraggableActivity){
                if (((DraggableActivity)n).getType()==activityType){
                    activities.add((DraggableActivity)n);
                }
            }
        }
        return activities;
    }
    public List<DraggableActivity> getActivities(){
        List<DraggableActivity> activities= new ArrayList<DraggableActivity>();
        for(Node n: graph_pane.getChildren()){
            if (n instanceof DraggableActivity){
                activities.add((DraggableActivity)n);
            }
        }
        return activities;
    }


    public List<Intent> getActivityButtons(DraggableActivity activity){
        List<Intent> intents = new ArrayList<Intent>();
        for(Intent i : activity.getOutgoingIntentsForType(IntentType.buttonClick)){
            intents.add(i);
        }
        for(Intent i : activity.getOutgoingIntentsForType(IntentType.forResult)){
            intents.add(i);
        }
        return intents;
    }

    public boolean checkForIdCollisions(){
        List<DraggableActivity> activities = getActivities();
        List<String> activitiesId = new ArrayList<String>();
        for(DraggableActivity a : activities){
            activitiesId.add(a.getName());
        }
        //check for duplicated activity names
        for(int i = 0; i<activitiesId.size();i++){
            if(activitiesId.indexOf(activitiesId.get(i))!= i){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Duplicate activity name");
                alert.setContentText("There are activities with the same name: "+activitiesId.get(i));
                alert.showAndWait();
                return false;
            }
        }

        //check for duplicated button id in empty and basic activities
        activities.clear();
        activities = getActivitiesByType(DragControllerType.emptyActivity);
        activities.addAll(getActivitiesByType(DragControllerType.basicActivity));
        for (DraggableActivity a :activities){
            List<Intent> buttons = getActivityButtons(a);
            List<String> buttonsId = new ArrayList<String>();
            for (Intent i : buttons){
                if (i instanceof ButtonClickIntent){
                    buttonsId.add(((ButtonClickIntent)i).getButtonId());
                }else if(i instanceof ButtonClickWithResultIntent){
                    buttonsId.add(((ButtonClickWithResultIntent)i).getButtonId());
                }
            }
            for(int i = 0; i<buttonsId.size();i++){
                if(buttonsId.indexOf(buttonsId.get(i))!= i){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("Duplicate button id");
                    alert.setContentText("In activity "+a.getName()+" there are buttons with the same id: "+buttonsId.get(i));
                    alert.showAndWait();
                    return false;
                }
            }
        }

        return true;
    }
}
