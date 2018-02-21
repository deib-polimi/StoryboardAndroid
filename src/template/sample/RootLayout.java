package template.sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.CubicCurve;
import template.ProjectHandler;
import template.managers.AttributeInspectorManager;
import template.managers.StructureTreeManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
    private TreeView<TreeItemParameter> structure_tree;
    @FXML
    private Button generate_button;
    @FXML
    private TabPane attribute_inspector;

    private DragIcon mDragOverIcon = null;

    private EventHandler mIconDragOverRoot=null;
    private EventHandler mIconDragDropped=null;
    private EventHandler mIconDragOverGraphPane=null;

    private TreeItem<TreeItemParameter> rootItem = null;

    private StructureTreeManager treeManager = null;

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
           /*if (c<2){
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
            }*/
            DragIcon icn = new DragIcon();
            addDragDetection(icn);
            icn.setType(DragControllerType.values()[i]);
            controllers_grid.add(icn,0,i);
            controllers_grid.add(new Label(icn.getType().toString()),1,i);
            //c++;

            structure_tree.setOnKeyPressed( new EventHandler<KeyEvent>()
            {
                @Override
                public void handle( final KeyEvent keyEvent )
                {
                    final TreeItem<TreeItemParameter> selectedItem = structure_tree.getSelectionModel().getSelectedItem();

                    if ( selectedItem != null )
                    //if(item!=null)
                    {
                        if ( keyEvent.getCode().equals( KeyCode.DELETE ) )
                        {
                            deleteItem();
                            deselectAll();
                        }

                    }
                }
            } );

        }
        //initialize attribute inspector
        AttributeInspectorManager inspectorManager = AttributeInspectorManager.getInstance();
        inspectorManager.setAttributeInspector(attribute_inspector);

        buildDragHandlers();

        rootItem = new TreeItem<TreeItemParameter>(new TreeItemParameter(ProjectHandler.getInstance().getProjectName(),"ROOT"));
        rootItem.setExpanded(true);  //root tree item looks expanded when the application starts
        structure_tree.setRoot(rootItem);
        treeManager = StructureTreeManager.getInstance();
        treeManager.setStructureTree(structure_tree);
        treeManager.setRootItem(rootItem);
        treeManager.setGraph(graph_pane);
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

                //DragContainer container = (DragContainer)event.getDragboard().getContent(DragContainer.AddNode);
                DragContainer container = null;
                try{
                    container = (DragContainer)event.getDragboard().getContent(DragContainer.AddNode);
                }catch (Exception e){
                    System.out.println("Drag icona non completato");
                }

                if (container!=null){
                    //check if drop is inside the limits of graph pane
                    if (container.getValue("scene_coords") != null){

                        //DraggableActivity activity = new DraggableActivity();
                        DraggableActivity activity = null;
                        try {
                            activity = createActivityByType(DragControllerType.valueOf(container.getValue("type")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        activity.setType(DragControllerType.valueOf(container.getValue("type")));
                        deselectAll();

                        graph_pane.getChildren().add(activity);

                        //Relocate the new DragIcon to center on the mouse cursor position
                        Point2D cursorPoint = container.getValue("scene_coords");
                        activity.relocateToPoint(
                                new Point2D(cursorPoint.getX()-19 , cursorPoint.getY()-19)
                        );

                        TreeItem<TreeItemParameter> item = new TreeItem<TreeItemParameter>(new TreeItemParameter(activity.getName(),activity.getId()));
                        rootItem.getChildren().add(item);
                        activity.loadInspectorListeners();

                    }
                }

                //AddLink drag operation
                try{
                    container =(DragContainer) event.getDragboard().getContent(DragContainer.AddLink);
                }catch (Exception e){
                    System.out.println("Link non completato");
                }


                if (container != null) {
                    String sourceId = container.getValue("source");
                    String targetId = container.getValue("target");

                    if (sourceId != null && targetId != null && !sourceId.equals(targetId)) {
                        Point2D point = container.getValue("drop_coords");
                        ContextMenu contextMenu = new ContextMenu();
                        buildContextMenu(contextMenu,container,(DraggableActivity)searchById(sourceId),(DraggableActivity)searchById(targetId));
                        contextMenu.show(graph_pane, point.getX(), point.getY());
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
        //retrieve selected tree item
       TreeItem<TreeItemParameter> item = structure_tree.getSelectionModel().getSelectedItem();
       if (item!=null){
           //retrieve corresponding item on the graph
           Node selectedNode = searchById(item.getValue().getId());
           //highlight (and store) the selected item
           SelectedItem selectedItem = SelectedItem.getInstance();
           selectedItem.setSelectedItem(selectedNode);
       }
       else {
           deselectAll();
       }
    }

    //delete selected tree item (and children) from tree and graph
    public void deleteItem(){
        TreeItem<TreeItemParameter> item = structure_tree.getSelectionModel().getSelectedItem();

        if (item == null || item.getValue().getId().equals("ROOT")) {
            return;
        } else {
            //when item is an intent: if there are other intents on the same link delete the item
            //else remove the entire link
            if ((searchById(item.getValue().getId()) instanceof Intent) && (item.getParent().getChildren().size()==1)){
                treeManager.deleteSubTree(item.getParent());
                deleteNodeFromGraph(item.getParent().getValue().getId());

            }else{
                treeManager.deleteSubTree(item);
                deleteNodeFromGraph(item.getValue().getId());
            }

        }
        AttributeInspectorManager.getInstance().replaceInspectorAfterDelete();

    }



    //delete node (activity, link, intent) from graph
    private void deleteNodeFromGraph(String id){
        Node n = searchById(id);
        if (n instanceof Intent){
            //delete intent from its associated link and from graph
            Link belongingLink = ((Intent) n).getBelongingLink();
            belongingLink.deleteIntent((Intent)n);
        }else if (n instanceof DraggableActivity){
            //delete activity from graph
            ((DraggableActivity) n).delete();
        }else if (n instanceof Link){
            //delete link from the graph
            ((Link) n).delete();
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

    private Link linkAlreadyExist(DraggableActivity source,DraggableActivity target){
        for(Link l : source.getAnchoredLinks()){
            if (l.getTarget().equals(target)){
                return l;
            }
        }
        return null;
    }

    public void createLink(DragContainer container,IntentType intentType) throws IOException {
        //bind the ends of our link to the nodes whose id's are stored in the drag container
        String sourceId = container.getValue("source");
        String targetId = container.getValue("target");

        if (sourceId != null && targetId != null && !sourceId.equals(targetId)) {

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


            if (source != null && target != null){
                Link l = linkAlreadyExist(source,target);
                //se link esiste già aggiungo intent
                if (l!= null){
                    //controllo che il limite massimo di intent per link (5) non sia stato raggiunto
                    if(l.getIntentsList().size()<5){
                        //Intent intent = new Intent(l.getCurve(),0f,20,intentType);
                        Intent intent = createIntentByType(l.getCurve(),0f,20,intentType);
                        l.addIntent(intent);
                        graph_pane.getChildren().add(intent);
                        treeManager.addIntentToTree(l);
                        intent.loadAttributeInspector();
                        deselectAll();
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("OVERFLOW ERROR");
                        alert.setHeaderText("Limit number reached");
                        alert.setContentText("You reached the maximum number of intents! Remember that you can insert maximum 5 intents per link.");
                        alert.showAndWait();
                    }


                }
                //altrimenti creo nuovo link
                else{
                    Link link = new Link();
                    graph_pane.getChildren().add(0,link);
                    link.setSource(source);
                    link.setTarget(target);

                    //Intent intent = new Intent(link.getCurve(),0.5f,20,intentType);
                    Intent intent = createIntentByType(link.getCurve(),0.5f,20,intentType);
                    link.bindEnds(source, target,intent);
                    graph_pane.getChildren().add(intent);

                    //activity tiene traccia dei link ad esa collegati. Questo serve per aggiornare
                    //la posizione di arrow e icone intents quando sposto activity nel grafo
                    target.addAnchoredLink(link);
                    source.addAnchoredLink(link);
                    if(intentType == IntentType.tabIntent){
                        target.setFragment(true);
                        ((TabbedActivity)source).addTab((TabIntent)intent);
                    }else if (intentType == IntentType.bottomNavigIntent){
                        target.setFragment(true);
                        ((BottomNavigationActivity)source).addTab((BottomNavigationIntent) intent);
                    }
                    treeManager.addLinkToTree(link,source,target);
                    intent.loadAttributeInspector();
                    deselectAll();

                }
            }

        }
    }

    public void buildContextMenu(ContextMenu contextMenu,DragContainer container,DraggableActivity source,DraggableActivity target){

        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(source.getMenuItems(this,container,target));
    }

    private Node searchById (String id){
        Node node = null;
        for (Node n : graph_pane.getChildren()){
            if (n.getId().equals(id)){
                node = n;
                return node;
            }
        }
        return node;
    }

    public void clearSelections(MouseEvent mouseEvent){
        Object clickedItem =mouseEvent.getTarget();
        if (mouseEvent.getClickCount() == 2){
            if (clickedItem == graph_pane){
                deselectAll();
            }

        }
    }
    public void deselectAll(){
        //clean attribute inspector
        //deselect item
        SelectedItem selectedItem = SelectedItem.getInstance();
        selectedItem.deselect();
        //deselect tree item
        StructureTreeManager treeManager = StructureTreeManager.getInstance();
        treeManager.deselectAll();
    }


    //CODE GENERATION (button click)
    public void generateCode() throws IOException {
        CodeGenerator codeGenerator = new CodeGenerator();
        List<DraggableActivity> activities = StructureTreeManager.getInstance().getActivitiesFromTree();
        List<BottomNavigationActivity> bottomNavigationActivities = new ArrayList<BottomNavigationActivity>();
        //generate java file and xml file for all the activities
        for(DraggableActivity a : activities){
            codeGenerator.generateCode(a);
            if (a.getType() == DragControllerType.bottomNavigationActivity){
                bottomNavigationActivities.add((BottomNavigationActivity) a);
            }
        }
        //register activities in Android Manifest
        codeGenerator.generateManifest(activities);
        //generate navigation menu containing the declarations of the items
        //of all bottom navigation activities
        if(bottomNavigationActivities.size()>0){
            codeGenerator.generateNavigationMenu(bottomNavigationActivities);
        }

    }

    private Intent createIntentByType(CubicCurve curve,float t, double radius, IntentType type) throws IOException {
        Intent intent = null;
        switch (type) {

            case buttonClick:
                intent = new ButtonClickIntent(curve,t,radius,type);
                break;

            case fabClick:
                intent = new FABIntent(curve,t,radius,type);
                break;

            case loginClick:
                intent = new LoginIntent(curve,t,radius,type);
                break;

            case tabIntent:
                intent = new TabIntent(curve,t,radius,type);
                break;

            case bottomNavigIntent:
                intent = new BottomNavigationIntent(curve,t,radius,type);
                break;

            case itemClick:
                intent = new AdapterViewItemClick(curve,t,radius,type);
                break;

            case cardClick:
                intent = new CardViewItemClick(curve,t,radius,type);
                break;

            default:
                break;
        }
        return intent;
    }

    private DraggableActivity createActivityByType(DragControllerType type) throws IOException {
        DraggableActivity activity = null;
        switch (type) {

            case emptyActivity:
                activity = new EmptyActivity();
                break;

            case basicActivity:
                activity = new BasicActivity();
                break;

            case loginActivity:
                activity = new LoginActivity();
                break;

            case tabbedActivity:
                activity = new TabbedActivity();
                break;

            case bottomNavigationActivity:
                activity = new BottomNavigationActivity();
                break;

            case listView:
                activity = new ListViewActivity();
                break;

            case gridView:
                activity = new GridViewActivity();
                break;

            case cardView:
                activity = new CardViewActivity();
                break;

            default:
                break;
        }
        return activity;
    }
}
