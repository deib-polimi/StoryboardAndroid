package template.sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Created by utente on 10/12/2017.
 */
public class DraggableActivity extends AnchorPane{

    @FXML
    public Label title_label;
    @FXML AnchorPane root_pane;

    private DragControllerType mType = null;

    private EventHandler mContextDragOver;
    private EventHandler  mContextDragDropped;

    private Point2D mDragOffset = new Point2D(0.0, 0.0);

    private final DraggableActivity self;

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
    }

    @FXML
    private void initialize() {
        buildNodeDragHandlers();
    }

    public DragControllerType getType() { return mType;}

    public void setType(DragControllerType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("dragicon");
        getStyleClass().add("icon-red");
        getStyleClass().add("node-overlay");
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
                content.putString(mType.toString());
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
}
