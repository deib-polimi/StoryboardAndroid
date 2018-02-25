package template.sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import template.attributeInspector.IntentAttributes;
import template.managers.StructureTreeManager;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by utente on 24/02/2018.
 */
public class Intent extends Pane {
    @FXML
    private Circle circle;
    @FXML
    private Pane root_pane;

    private float t;
    private CubicCurve curve;
    private double radius;
    private Image img;
    private IntentType type;
    private Link belongingLink;
    private String name;

    //private final DoubleProperty arrowDirection = new SimpleDoubleProperty();
    //private Polygon arrow;
    //private boolean showArrow = false;


    public Intent(CubicCurve curve, float t, double radius, IntentType type) {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("Intent.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        circle.setRadius(radius);
        this.curve = curve;
        this.t = t;
        this.type = type;
        this.img = setImage(type);
        circle.setFill(new ImagePattern(img));
        circle.setStroke(Color.BLACK);
        update();
        setId(UUID.randomUUID().toString());

        this.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2){
                    Intent i = (Intent) mouseEvent.getSource();
                    //update attribute inspector
                    loadAttributeInspector();
                    //highlight and store selected item
                    SelectedItem selectedItem = SelectedItem.getInstance();
                    selectedItem.setSelectedItem(i);
                    //select tree item
                    StructureTreeManager treeManager = StructureTreeManager.getInstance();
                    TreeItem<TreeItemParameter> item = treeManager.searchTreeItemById(getId(),treeManager.getRootItem());
                    treeManager.selectTreeItem(item);
                }
            }
        });
    }

    @FXML
    private void initialize() {


    }

    /*public void showArrow(boolean showArrow){
        this.showArrow = showArrow;
        arrow.setVisible(showArrow);
    }

    public void setArrow(){

        double[] arrowBIG = new double[] { 0,-3,5,3,-5,3 };
        arrow = new Polygon(arrowBIG);
        //arrow.setRotate(-90);
        root_pane.getChildren().add(arrow);
        arrowDirection.bind(new When((belongingLink.getSource().layoutXProperty()).lessThan(belongingLink.getTarget().layoutXProperty()))
                .then(1.0).otherwise(-1.0));
        arrow.translateXProperty().bind(Bindings.add(arrow.layoutXProperty(),arrowDirection.multiply(15)));
        arrow.rotateProperty().bind(arrowDirection.multiply(90));
        arrow.setVisible(showArrow);

    }*/

    private Image setImage(IntentType type){
        Image image=null;

        switch (type) {

            case buttonClick:
                image= new Image(getClass().getResource("/img/android.jpg").toString());
                break;

            case fabClick:
                image= new Image(getClass().getResource("/img/gridview.png").toString());
                break;

            case loginClick:
                image= new Image(getClass().getResource("/img/listview.png").toString());
                break;

            case tabIntent:
                image= new Image(getClass().getResource("/img/listview.png").toString());
                break;

            case bottomNavigIntent:
                image= new Image(getClass().getResource("/img/listview.png").toString());
                break;

            case itemClick:
                image= new Image(getClass().getResource("/img/listview.png").toString());
                break;

            case cardClick:
                image= new Image(getClass().getResource("/img/listview.png").toString());
                break;

            case forResult:
                image= new Image(getClass().getResource("/img/listview.png").toString());
                break;

        }
        return image;
    }

    public void update() {
        double size = Math.max(curve.getBoundsInLocal().getWidth(), curve.getBoundsInLocal().getHeight());
        double scale = size / 4d;

        Point2D ori = eval(curve, t);

        //setTranslateX(ori.getX());
        setLayoutX(ori.getX());
        //setTranslateY(ori.getY());
        setLayoutY(ori.getY());
    }
    private Point2D eval(CubicCurve c, float t){
        Point2D p=new Point2D(Math.pow(1-t,3)*c.getStartX()+
                3*t*Math.pow(1-t,2)*c.getControlX1()+
                3*(1-t)*t*t*c.getControlX2()+
                Math.pow(t, 3)*c.getEndX(),
                Math.pow(1-t,3)*c.getStartY()+
                        3*t*Math.pow(1-t, 2)*c.getControlY1()+
                        3*(1-t)*t*t*c.getControlY2()+
                        Math.pow(t, 3)*c.getEndY());
        return p;
    }

    public void changePosition (float tNew){
        /*if(showArrow){
            this.t = 1-tNew;
        }else{*/
            this.t = tNew;
        //}
        update();
    }

    public void setName(String name) {
        this.name = name;
    }
    public void updateName(String name){
        setName(name);
        StructureTreeManager treeManager = StructureTreeManager.getInstance();
        treeManager.updateIntentName(name,getId());
    }
    public String getName() {
        return name;
    }


    public Link getBelongingLink() {
        return belongingLink;
    }

    public void setBelongingLink(Link belongingLink) {
        this.belongingLink = belongingLink;
    }

    public IntentType getType() {
        return type;
    }

    public void delete (){
        if(type ==IntentType.tabIntent){
            belongingLink.getTarget().setFragment(false);
            //((TabbedActivity)belongingLink.getSource()).removeTab((TabIntent) this);
        }else if (type ==IntentType.bottomNavigIntent){
            belongingLink.getTarget().setFragment(false);
            //((BottomNavigationActivity)belongingLink.getSource()).removeTab((BottomNavigationIntent) this);
        }
        //remove intent from graph
        AnchorPane parent  = (AnchorPane) this.getParent();
        parent.getChildren().remove(this);

    }

    public void select(){
        int depth = 20;

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

    public void loadAttributeInspector(){}
    public IntentAttributes getIntentInspector (){return null;}

    public String convertExtraType(String boxValue){
        switch(boxValue){
            case "String" :
                return "String";
            case "Boolean" :
                return "boolean";
            case "Integer":
                return "int";
            case "Float":
                return "float";
            case "Double":
                return "double";
            default :
                return null;
        }
    }
    public String getExtraValue(String boxValue){
        switch(boxValue){
            case "String" :
                return "\"Message\"";
            case "Boolean" :
                return "true";
            case "Integer":
                return "1";
            case "Float":
                return "1.0f";
            case "Double":
                return "1.0";
            default :
                return null;
        }
    }

    public String getExtraType( ){return null;}
    public String getExtraReceiver(int nID){return null;}

}

