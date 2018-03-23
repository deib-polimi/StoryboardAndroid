package template.appInterface;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import template.intent.Intent;
import template.managers.StructureTreeManager;
import template.utils.DragControllerType;
import template.utils.IntentType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DraggableActivity extends AnchorPane{

    @FXML
    private Label title_label;
    @FXML
    private AnchorPane root_pane;
    @FXML
    private AnchorPane drag_pane;
    @FXML
    private GridPane header_bar;
    @FXML
    private ImageView home;

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

    private boolean isFragment = false;

    public DraggableActivity(){

        self = this;

        //provide a universally unique identifier for this object
        setId(UUID.randomUUID().toString());


    }

    public void init() {

        buildNodeDragHandlers();
        buildLinkDragHandlers();

        left_link_handle.setOnDragDetected(mLinkHandleDragDetected);
        right_link_handle.setOnDragDetected(mLinkHandleDragDetected);

        left_link_handle.setOnDragDropped(mLinkHandleDragDropped);
        right_link_handle.setOnDragDropped(mLinkHandleDragDropped);
        drag_pane.setOnDragDropped(mLinkHandleDragDropped);


        mDragLink = new Link();
        mDragLink.setVisible(false);

        parentProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable,
                                Object oldValue, Object newValue) {
                graph_pane = (AnchorPane) getParent();
            }
        });
        //if it is the first activity set it as initial activity
        if(StructureTreeManager.getInstance().getRootItem().getChildren().size()==0){
            IsInitialActivity.getInstance().setInitialActivity(this);
        }

    }

    public DragControllerType getType() { return mType;}

    public void setType(DragControllerType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("dragicon");
        getStyleClass().add("node-bg-white");
        header_bar.getStyleClass().add("node-overlay-teal");

        switch (mType) {

            case gridView:
                body_pane.getStyleClass().add("activity-gridview");
                break;

            case listView:
                body_pane.getStyleClass().add("activity-listview");
                break;

            case cardView:
                body_pane.getStyleClass().add("activity-cardview");
                break;

            case emptyActivity:

                break;

            case basicActivity:
                body_pane.getStyleClass().add("activity-basic");
                break;

            case tabbedActivity:
                body_pane.getStyleClass().add("activity-tabbed");
                break;

            case loginActivity:
                body_pane.getStyleClass().add("activity-login");
                break;

            case bottomNavigationActivity:
                body_pane.getStyleClass().add("activity-bottomnav");
                break;




            default:
                break;
        }

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
        //if it is initial activity deselect initial activity
        if(IsInitialActivity.getInstance().getInitialActivity() == this){
            IsInitialActivity.getInstance().deselectInitialActivity();
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

                header_bar.getStyleClass().add("node-selected");

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

                header_bar.getStyleClass().remove("node-selected");

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

    public String getName() {
        return title_label.getText();
    }

    public void setName(String name) {
        title_label.setText(name);
    }
    public void updateName(String name){
        setName(name);
        StructureTreeManager treeManager = StructureTreeManager.getInstance();
        treeManager.updateActivityName(name,getId());
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

    public void mouseClickOnActivityBody (MouseEvent mouseEvent){

        //doppio click
        if (mouseEvent.getClickCount() == 2){
            //load attribute inspector
            loadInspector();

            //highligh and store selected item
            SelectedItem selectedItem = SelectedItem.getInstance();
            selectedItem.setSelectedItem(this);
            //select tree item
            StructureTreeManager treeManager = StructureTreeManager.getInstance();
            TreeItem<TreeItemParameter> item = treeManager.searchTreeItemById(getId(),treeManager.getRootItem());
            treeManager.selectTreeItem(item);
        }
    }

    public void loadInspector(){}

    public void select(){
        int depth = 30;

        DropShadow borderGlow= new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.BLUE);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);
        //borderGlow.setBlurType(BlurType.ONE_PASS_BOX);
        borderGlow.setSpread(0.5);

        this.setEffect(borderGlow);

    }

    public void deselect(){
        this.setEffect(null);
    }

    public  List<Intent> getIntents(){
        List<Intent> intentList = new ArrayList<Intent>();
        for(Link l : anchoredLinks){
            for(Intent i: l.getIntentsList()){
                intentList.add(i);
            }
        }
        return intentList;
    }

    public List<Link> getOutgoingLinks(){
        List<Link> outgoingLinks = new ArrayList<Link>();
        for(Link l: getAnchoredLinks()){
            if(l.getSource() == this){
                outgoingLinks.add(l);
            }
        }
        return outgoingLinks;
    }
    public List<Link> getIngoingLinks(){
        List<Link> ingoingLinks = new ArrayList<Link>();
        for(Link l: getAnchoredLinks()){
            if(l.getTarget() == this){
                ingoingLinks.add(l);
            }
        }
        return ingoingLinks;
    }

    public List<Intent> getOutgoingIntents(){
        List<Intent> intentList = new ArrayList<Intent>();
        for(Link l: getOutgoingLinks()){
            for(Intent i: l.getIntentsList()){
                intentList.add(i);
            }
        }
        return intentList;
    }

    public List<Intent> getIngoingIntents(){
        List<Intent> intentList = new ArrayList<Intent>();
        for(Link l: getIngoingLinks()){
            for(Intent i: l.getIntentsList()){
                intentList.add(i);
            }
        }
        return intentList;
    }

    public List<Intent> getOutgoingIntentsForType(IntentType type){
        List<Intent> intentList = new ArrayList<Intent>();
        for(Link l: getOutgoingLinks()){
            for(Intent i: l.getIntentsList()){
                if(i.getType() == type){
                    intentList.add(i);
                }

            }
        }
        return intentList;
    }
    public List<Intent> getIngoingIntentsForType(IntentType type){
        List<Intent> intentList = new ArrayList<Intent>();
        for(Link l: getIngoingLinks()){
            for(Intent i: l.getIntentsList()){
                if(i.getType() == type){
                    intentList.add(i);
                }

            }
        }
        return intentList;
    }

    public boolean isFragment(){
        return isFragment;
    }
    public void setFragment(boolean isFragment){
        this.isFragment = isFragment;
        if(isFragment){
            //if fragment was initial activity, select its container application as new initial activity
            if(IsInitialActivity.getInstance().isInitialActivity(this)){
                isInitialActivity(false);
                IsInitialActivity.getInstance().setInitialActivity(getContainerActivity(this));
            }


        }

    }

    public void isInitialActivity(boolean isInitial){}
    public String createJavaCode() throws IOException {return null;}
    public String createXMLCode() throws IOException {return null;}
    public String getManifest() throws IOException {return null;}
    public List<MenuItem> getMenuItems(RootLayout root, DragContainer container, DraggableActivity target){return null;};
    public String createFragmentCode() throws IOException {return null;}
    public String generateLayoutName(String objectName){return null;}
    public void loadInspectorListeners(){};
    public void setFragmentInspector(boolean isFragment){};
    public DraggableActivity getContainerActivity(DraggableActivity fragment){
        for (Intent i : fragment.getIngoingIntents()){
            if(i.getType() == IntentType.tabIntent || i.getType() == IntentType.bottomNavigIntent){
                return i.getBelongingLink().getSource();
            }
        }
        return null;
    }

    public void setInitialIcon(boolean isInitial){
        if(isInitial){
            home.setImage(new Image(getClass().getResource("/icons/ic_home_white.png").toString()));
        }else{
            home.setImage(null);
        }
    }



}
