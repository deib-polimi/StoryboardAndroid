package template.sample;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import template.attributeInspector.ButtonClickIntentAttributes;
import template.attributeInspector.FABIntentAttributes;
import template.attributeInspector.IntentAttributes;
import template.managers.AttributeInspectorManager;
import template.managers.StructureTreeManager;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by utente on 11/01/2018.
 */
public class Intent extends Circle{
    private float t;
    private CubicCurve curve;
    private double radius;
    private Image img;
    private IntentType type;
    private Link belongingLink;
    private String name;

    public Intent(CubicCurve curve, float t, double radius, IntentType type ) throws IOException {

        super(radius);
        this.curve = curve;
        this.t = t;
        this.type = type;
        this.img = setImage(type);
        setFill(new ImagePattern(img));
        setStroke(Color.BLACK);
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

    private Image setImage(IntentType type){
        Image image=null;

        switch (type) {

            case buttonClick:
                image= new Image(getClass().getResource("/img/container.png").toString());
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

        }
        return image;
    }

    public void update() {
        double size = Math.max(curve.getBoundsInLocal().getWidth(), curve.getBoundsInLocal().getHeight());
        double scale = size / 4d;

        Point2D ori = eval(curve, t);

        setTranslateX(ori.getX());
        setTranslateY(ori.getY());
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
        this.t = tNew;
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
            ((TabbedActivity)belongingLink.getSource()).removeTab((TabIntent) this);
        }else if (type ==IntentType.bottomNavigIntent){
            belongingLink.getTarget().setFragment(false);
            ((BottomNavigationActivity)belongingLink.getSource()).removeTab((BottomNavigationIntent) this);
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

}
