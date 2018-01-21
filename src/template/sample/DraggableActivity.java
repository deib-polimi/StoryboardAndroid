package template.sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static template.sample.DragType.link;

/**
 * Created by utente on 10/12/2017.
 */
public class DraggableActivity extends AnchorPane{

    private static final DataFormat customFormat = new DataFormat("s","t");

    @FXML
    public Label title_label;
    @FXML AnchorPane root_pane;

    private DragControllerType mType = null;

    private EventHandler mContextDragOver;
    private EventHandler  mContextDragDropped;

    private Point2D mDragOffset = new Point2D(0.0, 0.0);

    private final DraggableActivity self;

    //Link
    @FXML AnchorPane left_link_handle;
    @FXML AnchorPane right_link_handle;
    @FXML AnchorPane body_pane;

    private EventHandler <MouseEvent> mLinkHandleDragDetected;
    private EventHandler <DragEvent> mLinkHandleDragDropped;
    private EventHandler <DragEvent> mContextLinkDragOver;
    private EventHandler <DragEvent> mContextLinkDragDropped;

    private Link mDragLink = null;
    private AnchorPane graph_pane = null;

    private List<Link> anchoredLinks = new ArrayList<Link>();

    ContextMenu contextMenu = new ContextMenu();


    public DraggableActivity(){

        self = this;

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
        //provide a universally unique identifier for this object
        setId(UUID.randomUUID().toString());
    }

    @FXML
    private void initialize() {

        buildNodeDragHandlers();
        buildLinkDragHandlers();

        left_link_handle.setOnDragDetected(mLinkHandleDragDetected);
        right_link_handle.setOnDragDetected(mLinkHandleDragDetected);

        left_link_handle.setOnDragDropped(mLinkHandleDragDropped);
        right_link_handle.setOnDragDropped(mLinkHandleDragDropped);
        body_pane.setOnDragDropped(mLinkHandleDragDropped);


        mDragLink = new Link();
        mDragLink.setVisible(false);

        parentProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable,
                                Object oldValue, Object newValue) {
                graph_pane = (AnchorPane) getParent();
            }
        });

        //buildContextMenu();
    }

    public DragControllerType getType() { return mType;}

    public void setType(DragControllerType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("dragicon");
        getStyleClass().add("icon-red");
        //getStyleClass().add("node-overlay");
        title_label.setText(mType.toString());
        /*switch (mType) {

            case gridView:

                break;

            case listView:

                break;

            case plus:

                break;

            case emptyActivity:

                break;

            case loginActivity:

                break;

            case tabBar:

                break;

            default:
                break;
        }*/

    }

    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
                (int) (localCoords.getX() - mDragOffset.getX()),
                (int) (localCoords.getY() - mDragOffset.getY())
        );
    }

    //delete activity (and all anchored links) from the graph
    public void delete (){

        //delete all anchored links
        int size = anchoredLinks.size();
        for (int i = 0; i<size; i++){
            Link link = anchoredLinks.get(0);
            anchoredLinks.remove(0);
            link.delete();
        }
        //delete activity from graph
        AnchorPane parent  = (AnchorPane) self.getParent();
        parent.getChildren().remove(self);
    }

    public void buildNodeDragHandlers() {

        //drag detection for node dragging
        title_label.setOnDragDetected ( new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver (mContextDragOver);
                getParent().setOnDragDropped (mContextDragDropped);

                getStyleClass().add("node-selected");

                //store the coordinates of the mouse click relative to the upper left corner of the activity
                mDragOffset = new Point2D(event.getX(), event.getY());

                relocateToPoint (new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();
                container.addData ("type", mType.toString());
                content.put(DragContainer.DragNode, container);

                startDragAndDrop (TransferMode.ANY).setContent(content);

                event.consume();
            }
        });

        mContextDragOver = new EventHandler <DragEvent> () {

            //dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);
                relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));
                int size = anchoredLinks.size();
                if (size>0){
                    for(int i = 0; i<size; i++){
                        anchoredLinks.get(i).updateArrow();
                        anchoredLinks.get(i).updateIntents();
                    }

                }

                event.consume();
            }
        };

        //dragdrop for node dragging
        mContextDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getStyleClass().remove("node-selected");

                event.setDropCompleted(true);

                event.consume();

            }
        };
    }

    private void buildLinkDragHandlers() {

        mLinkHandleDragDetected = new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                //clear the objects which provide the context for drag operation
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver(mContextLinkDragOver);
                getParent().setOnDragDropped(mLinkHandleDragDropped);

                //Set up user-draggable link
                graph_pane.getChildren().add(0,mDragLink); //index 0 ensures the CubicCurve is rendered first,
                                                                // preventing it from being drawn over existing DraggableActivity in the scene

                mDragLink.setVisible(false);

                //This point represents the (X,Y) coordinates of the center of the node in the graph AnchorPane’s coordinate space.
                //This will serve as the starting point for our CubicCurve
                Point2D p = new Point2D(
                        getLayoutX() + (getWidth() / 2.0),
                        getLayoutY() + (getHeight() / 2.0)
                );

                mDragLink.setStart(p);

                //Drag content code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer ();

                AnchorPane link_handle = (AnchorPane) event.getSource();
                //reference to the root AnchorPane of the DraggableActivity
                DraggableActivity parent = (DraggableActivity) link_handle.getParent().getParent().getParent();

                container.addData("source", getId());
                content.put(DragContainer.AddLink, container);

                parent.startDragAndDrop (TransferMode.ANY).setContent(content);

                event.consume();
            }
        };

        mLinkHandleDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //get the drag data.  If it's null, abort.
                //This isn't the drag event we're looking for.
                ByteBuffer byteBuffer = (ByteBuffer)event.getDragboard().getContent(DragContainer.AddLink);
                DragContainer container = deserialize(byteBuffer);

                if (container == null)
                    return;

                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                mDragLink.setVisible(false);
                graph_pane.getChildren().remove(0);

                AnchorPane link_handle = (AnchorPane) event.getSource();
                DraggableActivity parent = (DraggableActivity) link_handle.getParent().getParent().getParent();

                ClipboardContent content = new ClipboardContent();

                container.addData("target", getId());
                container.addData("drop_coords", new Point2D(event.getSceneX(), event.getSceneY()));

                content.put(DragContainer.AddLink, container);

                event.getDragboard().setContent(content);
                //contextMenu.show(link_handle, event.getScreenX(), event.getScreenY());

                event.setDropCompleted(true);

                event.consume();

            }
        };

        mContextLinkDragOver = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);

                //Relocate user-draggable link
                if (!mDragLink.isVisible())
                    mDragLink.setVisible(true);

                //end of our link always tracks with the mouse cursor
                mDragLink.setEnd(new Point2D(event.getX(), event.getY()));

                event.consume();

            }
        };

        mContextLinkDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                mDragLink.setVisible(false);
                graph_pane.getChildren().remove(0);

                event.setDropCompleted(true);
                event.consume();
            }
        };
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

    public void addAnchoredLink(Link link){
        this.anchoredLinks.add(link);
    }
    public List<Link>getAnchoredLinks (){return anchoredLinks;}

    public void deleteAnchoredLink(Link link){
        boolean found = false;
        int i = 0;
        while (found == false && i<anchoredLinks.size()){
            if (anchoredLinks.get(i).getId().equals(link.getId())){
                anchoredLinks.remove(i);
                found = true;
            }
            i++;
        }
    }


}
