package template.sample;

import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * Created by utente on 22/01/2018.
 */
public class SelectedItem {
    private static SelectedItem instance = null;
    private Node selectedItem = null;

    public static SelectedItem getInstance() {

        if(instance == null) {
            instance = new SelectedItem();
        }
        return instance;
    }

    private SelectedItem() {
    }

    public Node getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Node item) {
        if (this.selectedItem != null){
            deselect();
        }
        this.selectedItem = item;
        if (selectedItem instanceof DraggableActivity){
            ((DraggableActivity) selectedItem).select();
        }else if (selectedItem instanceof Link){
            ((Link) selectedItem).select();
        }else if (selectedItem instanceof Intent){
            ((Intent) selectedItem).select();
        }
    }

    public void deselect(){
        if (selectedItem instanceof DraggableActivity){
            ((DraggableActivity) selectedItem).deselect();
        }else if (selectedItem instanceof Link){
            ((Link) selectedItem).deselect();
        }else if (selectedItem instanceof Intent){
            ((Intent) selectedItem).deselect();
        }
        this.selectedItem = null;
    }

}
