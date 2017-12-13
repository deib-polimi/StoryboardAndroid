package template.sample;

import com.sun.jna.platform.win32.WinDef;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

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
        }

        buildDragHandlers();

        rootItem = new TreeItem<>("Nome Progetto");
        rootItem.setExpanded(true);
        /*for (int i = 1; i < 6; i++) {
            TreeItem<String> item = new TreeItem<String>("Message" + i);
            rootItem.getChildren().add(item);
        }*/
        structure_tree.setRoot(rootItem);

    }

    private void buildDragHandlers() {

        final Point2D[] sceneCoord = {null};

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

                sceneCoord[0] =new Point2D(event.getSceneX(), event.getSceneY());

                //The DragContainer created in the DragDetected handler is retrieved from the event’s Dragboard.
                //DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode)

                //The container is updated with the scene coordinates of the mouse cursor
                //container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

                //ClipboardContent content = new ClipboardContent();
                //content.put(DragContainer.AddNode, container);

                //The event DragBoard’s content is replaced with the new ClipboardContent
                // object containing the updated DragContainer data
                //event.getDragboard().setContent(content);

                //Aggiungo activity al resource tree
                Dragboard db = event.getDragboard();
                String type = db.getString();
                TreeItem<String> item = new TreeItem<String>(type);
                rootItem.getChildren().add(item);
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

                //DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
                Dragboard db = event.getDragboard();
                String type = db.getString();

                //if (container != null) {
                if (type!=null){
                    //check if drop is inside the limits of graph pane
                    //if (container.getValue("scene_coords") != null) {
                    if (sceneCoord[0] != null){

                        DraggableActivity activity = new DraggableActivity();

                        //droppedIcon.setType(DragControllerType.valueOf(container.getValue("type")));
                        activity.setType(DragControllerType.valueOf(type));
                        graph_pane.getChildren().add(activity);

                        //Point2D cursorPoint = container.getValue("scene_coords");

                        //Relocate the new DragIcon to center on the mouse cursor position
                        /*droppedIcon.relocateToPoint(
                                new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                        );*/
                        activity.relocateToPoint(
                                new Point2D(sceneCoord[0].getX()-19 , sceneCoord[0].getY()-19)
                        );
                    }
                }
                event.consume();
                sceneCoord[0]=null;
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
                /*DragContainer container = new DragContainer();

                container.addData ("type", mDragOverIcon.getType().toString());
                content.put(DragContainer.AddNode, container);  //DataFormat, object*/
                content.putString(mDragOverIcon.getType().toString());

                mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
                mDragOverIcon.setVisible(true);
                mDragOverIcon.setMouseTransparent(true);
                event.consume();
            }
        });
    }
}
