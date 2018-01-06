package template.sample;

import com.sun.jna.platform.win32.WinDef;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by utente on 24/11/2017.
 */
public class RootLayout extends AnchorPane{

    @FXML
    private GridPane controllers_grid;

    @FXML
    private AnchorPane graph_pane;
    @FXML
    private SplitPane base_pane;
    @FXML
    private TreeView<String> structure_tree;

    private DragIcon mDragOverIcon = null;

    private EventHandler mIconDragOverRoot=null;
    private EventHandler mIconDragDropped=null;
    private EventHandler mIconDragOverGraphPane=null;

    TreeItem<String> rootItem = null;

    public RootLayout(){

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("RootLayout.fxml")
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
        mDragOverIcon = new DragIcon();

        mDragOverIcon.setVisible(false);
        mDragOverIcon.setOpacity(0.65);
        getChildren().add(mDragOverIcon);

        //inserisco icone nel controllers pane
        int length = DragControllerType.values().length;
        int c = 0;
        int r = 0;
        DragIcon[] icons = new DragIcon[length];
        for (int i = 0; i <length; i++) {
           if (c<2){
                DragIcon icn = new DragIcon();
                addDragDetection(icn);
                icn.setType(DragControllerType.values()[i]);
                controllers_grid.add(icn,c,r);
            }
            else{
                c=0;
                r++;
                DragIcon icn = new DragIcon();
                addDragDetection(icn);
                icn.setType(DragControllerType.values()[i]);
                controllers_grid.add(icn,c,r);
            }
            c++;

            structure_tree.setOnKeyPressed( new EventHandler<KeyEvent>()
            {
                @Override
                public void handle( final KeyEvent keyEvent )
                {
                    final TreeItem<String> selectedItem = structure_tree.getSelectionModel().getSelectedItem();

                    if ( selectedItem != null )
                    {
                        if ( keyEvent.getCode().equals( KeyCode.DELETE ) )
                        {
                            deleteTreeItem();
                        }

                        //... other keyevents
                    }
                }
            } );
        }

        buildDragHandlers();

        rootItem = new TreeItem<>("Nome Progetto");
        rootItem.setExpanded(true);  //root tree item looks expanded when the application starts
        structure_tree.setRoot(rootItem);

    }

    private void buildDragHandlers() {

        //drag over transition to move icon form controllers pane to graph pane
        mIconDragOverRoot = new EventHandler <DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                Point2D p = graph_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

                if (!graph_pane.boundsInLocalProperty().get().contains(p)) {
                    mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                    return;
                }

                event.consume();
            }
        };

        mIconDragOverGraphPane = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);

                mDragOverIcon.relocateToPoint(
                        new Point2D(event.getSceneX(), event.getSceneY())

            );

                event.consume();
            }
        };

        mIconDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                //Aggiungo activity al resource tree

                ByteBuffer byteBuffer = (ByteBuffer) event.getDragboard().getContent(DragContainer.AddNode);
                DragContainer container = deserialize(byteBuffer);
                String type = container.getValue("type");
                container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                content.put(DragContainer.AddNode, container);
                event.getDragboard().setContent(content);

                TreeItem<String> item = new TreeItem<String>(type);
                rootItem.getChildren().add(item);

                TreeItem<String> leaf = new TreeItem<String>("Intent");
                item.getChildren().add(leaf);


                event.setDropCompleted(true);
            }
        };

        this.setOnDragDone (new EventHandler <DragEvent> (){

            @Override
            public void handle (DragEvent event) {

                graph_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverGraphPane);
                graph_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
                base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);

                mDragOverIcon.setVisible(false);

                DragContainer container = (DragContainer)event.getDragboard().getContent(DragContainer.AddNode);

                if (container!=null){
                    //check if drop is inside the limits of graph pane
                    if (container.getValue("scene_coords") != null){

                        DraggableActivity activity = new DraggableActivity();

                        activity.setType(DragControllerType.valueOf(container.getValue("type")));
                        graph_pane.getChildren().add(activity);

                        //Relocate the new DragIcon to center on the mouse cursor position
                        Point2D cursorPoint = container.getValue("scene_coords");
                        activity.relocateToPoint(
                                new Point2D(cursorPoint.getX()-19 , cursorPoint.getY()-19)
                        );
                    }
                }

                //AddLink drag operation
                try{
                    container =(DragContainer) event.getDragboard().getContent(DragContainer.AddLink);
                }catch (Exception e){
                    System.out.println("Link non completato");
                }


                if (container != null) {

                    //bind the ends of our link to the nodes whose id's are stored in the drag container
                    String sourceId = container.getValue("source");
                    String targetId = container.getValue("target");

                    if (sourceId != null && targetId != null) {

                        //System.out.println(container.getData());
                        Link link = new Link();

                        //add our link at the top of the rendering order so it's rendered first
                        graph_pane.getChildren().add(0,link);

                        DraggableActivity source = null;
                        DraggableActivity target = null;

                        for (Node n: graph_pane.getChildren()) {

                            if (n.getId() == null)
                                continue;

                            if (n.getId().equals(sourceId))
                                source = (DraggableActivity) n;

                            if (n.getId().equals(targetId))
                                target = (DraggableActivity) n;

                        }

                        if (source != null && target != null)
                            link.bindEnds(source, target);
                    }

                }
                if (container != null) {
                    System.out.println(container.getData());
                }

                event.consume();
            }
        });
    };

    private void addDragDetection(DragIcon dragIcon) {

        dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                // set the other drag event handles on their respective objects
                base_pane.setOnDragOver(mIconDragOverRoot);
                graph_pane.setOnDragOver(mIconDragOverGraphPane);
                graph_pane.setOnDragDropped(mIconDragDropped);

                // get a reference to the clicked DragIcon object
                DragIcon icn = (DragIcon) event.getSource();

                //begin drag ops
                mDragOverIcon.setType(icn.getType());
                mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", mDragOverIcon.getType().toString());
                content.put(DragContainer.AddNode, container);

                mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
                mDragOverIcon.setVisible(true);
                mDragOverIcon.setMouseTransparent(true);
                event.consume();
            }
        });
    }

    public void treeItemMouseClick (MouseEvent mouseEvent){
        TreeItem<String>item = structure_tree.getSelectionModel().getSelectedItem();
        System.out.println(item.getValue());

        //doppio click
        //if (mouseEvent.getClickCount() == 2){}
    }

    public void deleteTreeItem(){
        TreeItem<String> item = structure_tree.getSelectionModel().getSelectedItem();

        if (item == null) {
            return;
        }

        TreeItem<String> parent = item.getParent();

        if (parent != null) {
            parent.getChildren().remove(item);
            ObservableList<Node> activityList = graph_pane.getChildren();
            int length = activityList.size();
            for (int i=0; i<length; i++){
                DraggableActivity activity = (DraggableActivity) activityList.get(i);
                if (item.getValue().equals(activity.getType().toString())){
                    activity.delete();
                }
            }
        }
    }

    public static DragContainer deserialize(ByteBuffer buffer) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(buffer.array());
            ObjectInputStream ois = new ObjectInputStream(is);
            DragContainer obj = (DragContainer) ois.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
