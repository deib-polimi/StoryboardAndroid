package template.sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

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

    private DragIcon mDragOverIcon = null;

    private EventHandler mIconDragOverRoot=null;
    private EventHandler mIconDragDropped=null;
    private EventHandler mIconDragOverGraphPane=null;

    TreeItem<TreeItemParameter> rootItem = null;

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
                    final TreeItem<TreeItemParameter> selectedItem = structure_tree.getSelectionModel().getSelectedItem();

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

        rootItem = new TreeItem<TreeItemParameter>(new TreeItemParameter("Nome Progetto","ROOT"));
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

                        DraggableActivity activity = new DraggableActivity();

                        activity.setType(DragControllerType.valueOf(container.getValue("type")));
                        graph_pane.getChildren().add(activity);

                        //Relocate the new DragIcon to center on the mouse cursor position
                        Point2D cursorPoint = container.getValue("scene_coords");
                        activity.relocateToPoint(
                                new Point2D(cursorPoint.getX()-19 , cursorPoint.getY()-19)
                        );

                        TreeItem<TreeItemParameter> item = new TreeItem<TreeItemParameter>(new TreeItemParameter(activity.getType().toString(),activity.getId()));
                        rootItem.getChildren().add(item);
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
                        buildContextMenu(contextMenu,container);
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
        TreeItem<TreeItemParameter> item = structure_tree.getSelectionModel().getSelectedItem();
        //System.out.println(item.getValue());

        //doppio click
        //if (mouseEvent.getClickCount() == 2){}
    }

    //delete selected tree item (and children) from tree and graph
    public void deleteTreeItem(){
        TreeItem<TreeItemParameter> item = structure_tree.getSelectionModel().getSelectedItem();

        if (item == null || item.getValue().getId().equals("ROOT")) {
            return;
        } else {
            //when item is an intent: if there are other intents on the same link delete the item
            //else remove the entire link
            if ((searchById(item.getValue().getId()) instanceof Intent) && (item.getParent().getChildren().size()==1)){
                deleteSubTree(item.getParent());
                deleteNodeFromGraph(item.getParent().getValue().getId());

            }else{
                deleteSubTree(item);
                deleteNodeFromGraph(item.getValue().getId());
            }

        }
        System.out.println(graph_pane.getChildren());


    }

    //delete item and its children from tree
    private void deleteSubTree(TreeItem<TreeItemParameter> root){
        Node n = searchById(root.getValue().getId());
        if (n instanceof DraggableActivity){
            //if activity is target of any link, search that link in the tree and delete it
            //this because in the tree links are children only of the source not the target
            for(Link l : ((DraggableActivity) n).getAnchoredLinks()){
                if (l.getTarget().getId().equals(root.getValue().getId())){
                    TreeItem<TreeItemParameter> itemLink = searchTreeItemById(l.getId(),rootItem);
                    boolean removed = itemLink.getParent().getChildren().remove(itemLink);
                }
            }
        }
        boolean removed = root.getParent().getChildren().remove(root);
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

    private void createLink(DragContainer container,IntentType intentType){
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
                        Intent intent = new Intent(l.getCurve(),0f,20,intentType);
                        l.addIntent(intent);
                        graph_pane.getChildren().add(intent);
                        addIntentToTree(l);
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

                    Intent intent = new Intent(link.getCurve(),0.5f,20,intentType);
                    link.bindEnds(source, target,intent);
                    graph_pane.getChildren().add(intent);

                    //activity tiene traccia dei link ad esa collegati. Questo serve per aggiornare
                    //la posizione di arrow e icone intents quando sposto activity nel grafo
                    target.addAnchoredLink(link);
                    source.addAnchoredLink(link);
                    addLinkToTree(link,source,target);

                }
            }

        }
    }

    public void buildContextMenu(ContextMenu contextMenu,DragContainer container){
        MenuItem item1 = new MenuItem("Button Click");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                IntentType intentType = IntentType.buttonClick;
                createLink(container,intentType);
            }
        });
        MenuItem item2 = new MenuItem("Implicit Intent");
        item2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                IntentType intentType = IntentType.implicit;
                createLink(container,intentType);
            }
        });
        MenuItem item3 = new MenuItem("Embed");
        item3.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                IntentType intentType = IntentType.embed;
                createLink(container,intentType);
            }
        });

        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(item1, item2,item3);
    }

    private void addLinkToTree (Link link, DraggableActivity source,DraggableActivity target){
       // boolean found = false;
        //int i = 0;
        TreeItem<TreeItemParameter> parentActivity = searchTreeItemById(source.getId(),rootItem);
        //add link to tree (to source activity node)
        TreeItem<TreeItemParameter> item = new TreeItem<TreeItemParameter>(new TreeItemParameter("Link to "+target.getType(),link.getId()));
        parentActivity.getChildren().add(item);
        int intentListSize = link.getIntentsList().size();
        //add intent to parent link
        Intent intent = link.getIntentsList().get(intentListSize-1); //ultimo intent aggiunto si trova in coda alla lista
        TreeItem<TreeItemParameter> itemIntent = new TreeItem<TreeItemParameter>(new TreeItemParameter(intent.getType().toString(),intent.getId()));
        item.getChildren().add(itemIntent);
    }

    private void addIntentToTree(Link link){
        TreeItem<TreeItemParameter> parentLink = searchTreeItemById(link.getId(),rootItem);
        int intentListSize = link.getIntentsList().size();
        //add intent to parent link
        Intent intent = link.getIntentsList().get(intentListSize-1); //ultimo intent aggiunto si trova in coda alla lista
        TreeItem<TreeItemParameter> itemIntent = new TreeItem<TreeItemParameter>(new TreeItemParameter(intent.getType().toString(),intent.getId()));
        parentLink.getChildren().add(itemIntent);
    }

    private TreeItem<TreeItemParameter> searchTreeItemById(String id,TreeItem<TreeItemParameter> searchRoot ){
        TreeItem<TreeItemParameter> foundItem = null;
        if (searchRoot != null){

            if (searchRoot.getValue().getId().equals(id)){
                return searchRoot;
            }else{
                for(TreeItem<TreeItemParameter> i : searchRoot.getChildren()){
                    foundItem = searchTreeItemById(id,i);
                    if (foundItem!=null)
                        break;
                }
            }
        }


        return foundItem;
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



}
