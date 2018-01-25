package template.sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import template.managers.AttributeInspectorManager;
import template.managers.StructureTreeManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by utente on 16/12/2017.
 */
public class Link extends AnchorPane {

    @FXML
    private CubicCurve link;
    @FXML
    private Pane root_pane;

    private DraggableActivity source = null;
    private DraggableActivity target = null;

    private Arrow arrow;
    double[] arrowShape = new double[] { 0,0,6,12,-6,12 };

    public List<Intent> getIntentsList() {
        return intentsList;
    }

    private List<Intent> intentsList = new ArrayList<>();

    private final DoubleProperty mControlOffsetX = new SimpleDoubleProperty();
    private final DoubleProperty mControlOffsetY = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX2 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY2 = new SimpleDoubleProperty();
    private final DoubleProperty mLinkAnchorXSource = new SimpleDoubleProperty();
    private final DoubleProperty mLinkAnchorYSource = new SimpleDoubleProperty();
    private final DoubleProperty mLinkAnchorXTarget = new SimpleDoubleProperty();
    private final DoubleProperty mLinkAnchorYTarget = new SimpleDoubleProperty();
    private final DoubleProperty mCurveOrientationX = new SimpleDoubleProperty();
    private final DoubleProperty mCurveOrientationY = new SimpleDoubleProperty();


    public Link() {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("Link.fxml")
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

        arrow = new Arrow(link,1f,arrowShape);
        arrow.setVisible(false);
        root_pane.getChildren().add(arrow);

        //mControlOffsetX.set(100.0);
        //mControlOffsetY.set(0.0);


        mControlOffsetX.bind(mCurveOrientationX.multiply(50.0));
        mControlOffsetY.bind(mCurveOrientationY.multiply(50.0));

        //*********************************************
        /*Circle control1 = new Circle(50,50,10);
        Circle control2 = new Circle(50,50,10);
        control1.setFill(Color.BLACK);
        control2.setFill(Color.DARKGREEN);
        control1.centerXProperty().bind(link.controlX1Property());
        control1.centerYProperty().bind(link.controlY1Property());
        control2.centerXProperty().bind(link.controlX2Property());
        control2.centerYProperty().bind(link.controlY2Property());
        root_pane.getChildren().add(control1);
        root_pane.getChildren().add(control2);*/
        //*********************************************


        mControlDirectionX1.bind(new When(
                link.startXProperty().greaterThan(link.endXProperty()))
                .then(-1.0).otherwise(1.0));

        mControlDirectionX2.bind(new When (
                link.startXProperty().greaterThan(link.endXProperty()))
                .then(1.0).otherwise(-1.0));

        mControlDirectionY1.bind(new When(
                link.startYProperty().greaterThan(link.endYProperty()))
                .then(-1.0).otherwise(1.0));

        mControlDirectionY2.bind(new When (
                link.startYProperty().greaterThan(link.endYProperty()))
                .then(1.0).otherwise(-1.0));

        link.controlX1Property().bind(
                Bindings.add(
                        link.startXProperty(),
                        mControlOffsetX.multiply(mControlDirectionX1)
                )
        );

        link.controlX2Property().bind(
                Bindings.add(
                        link.endXProperty(),
                        mControlOffsetX.multiply(mControlDirectionX2)
                )
        );

        link.controlY1Property().bind(
                Bindings.add(
                        link.startYProperty(),
                        mControlOffsetY.multiply(mControlDirectionY1)
                )
        );

        link.controlY2Property().bind(
                Bindings.add(
                        link.endYProperty(),
                        mControlOffsetY.multiply(mControlDirectionY2)
                )
        );

    }

    public void setStart(Point2D startPoint) {

        link.setStartX(startPoint.getX());
        link.setStartY(startPoint.getY());
    }

    public void setEnd(Point2D endPoint) {

        link.setEndX(endPoint.getX());
        link.setEndY(endPoint.getY());
    }

    public void bindEnds (DraggableActivity source, DraggableActivity target,Intent intent) {

        //source a sx del target
        if (target.getLayoutX()>source.getLayoutX()+source.getWidth()){
            link.startXProperty().bind(
                    Bindings.add(source.layoutXProperty(),source.getWidth()));

            link.startYProperty().bind(
                    Bindings.add(source.layoutYProperty(), (source.getHeight() / 2.0)));

            link.endXProperty().bind(
                    target.layoutXProperty());

            link.endYProperty().bind(
                    Bindings.add(target.layoutYProperty(), (target.getHeight() / 2.0)));
        }
        //source a dx del target
        else if(source.getLayoutX()>target.getLayoutX()+target.getWidth()){
            link.startXProperty().bind(source.layoutXProperty());

            link.startYProperty().bind(
                    Bindings.add(source.layoutYProperty(), (source.getHeight() / 2.0)));

            link.endXProperty().bind(
                    Bindings.add(target.layoutXProperty(),target.getWidth()));

            link.endYProperty().bind(
                    Bindings.add(target.layoutYProperty(), (target.getHeight() / 2.0)));
        }
        //source sopra al target
        else if(source.getLayoutY()+source.getHeight()<target.getLayoutY()){
            link.startXProperty().bind(Bindings.add(source.layoutXProperty(), (source.getWidth() / 2.0)));
            link.startYProperty().bind(Bindings.add(source.layoutYProperty(), source.getHeight()));

            link.endXProperty().bind(Bindings.add(target.layoutXProperty(), (target.getWidth() / 2.0)));
            link.endYProperty().bind(target.layoutYProperty());

        }
        //source sotto al target
        else if(source.getLayoutY()>target.getLayoutY()+target.getHeight()){
            link.startXProperty().bind(Bindings.add(source.layoutXProperty(), (source.getWidth() / 2.0)));
            link.startYProperty().bind(source.layoutYProperty());

            link.endXProperty().bind(Bindings.add(target.layoutXProperty(), (target.getWidth() / 2.0)));
            link.endYProperty().bind(Bindings.add(target.layoutYProperty(), target.getHeight()));
        }

        double H = source.getHeight()*1.8;
        double W = source.getWidth()*1.8;
        double w = W-source.getWidth();


        //binding property to change link position wrt activities when you move an activity
        /*mLinkAnchorXSource.bind(new When((target.layoutXProperty().greaterThan(source.layoutXProperty().add(w)))
                .and(target.layoutXProperty().lessThan(source.layoutXProperty().add(W)))
                .and((source.layoutYProperty().greaterThan(target.layoutYProperty().add(H))).or(target.layoutYProperty().greaterThan(source.layoutYProperty().add(H)))))
                .then(0.5)
                .otherwise(new When(target.layoutXProperty().greaterThan(source.layoutXProperty().add(source.getWidth()))).then(1)
                        .otherwise(new When(source.layoutXProperty().greaterThan(target.layoutXProperty().add(target.getWidth()))).then(0).otherwise(0.5))));*/

        mLinkAnchorXSource.bind(new When(target.layoutXProperty().greaterThan(source.layoutXProperty().add(source.getWidth()))).then(1)
                .otherwise(new When(source.layoutXProperty().greaterThan(target.layoutXProperty().add(target.getWidth()))).then(0).otherwise(0.5)));

        mLinkAnchorYSource.bind(new When(target.layoutYProperty().greaterThan(source.layoutYProperty().add(H))).then(1)
                .otherwise(new When(source.layoutYProperty().greaterThan(target.layoutYProperty().add(H))).then(0).otherwise(0.5)));

        /*mLinkAnchorXTarget.bind(new When((source.layoutXProperty().greaterThan(target.layoutXProperty().add(w)))
                .and(source.layoutXProperty().lessThan(target.layoutXProperty().add(W)))
                .and((target.layoutYProperty().greaterThan(source.layoutYProperty().add(H))).or(source.layoutYProperty().greaterThan(target.layoutYProperty().add(H)))))
                .then(0.5)
                .otherwise(new When(target.layoutXProperty().greaterThan(source.layoutXProperty().add(source.getWidth()))).then(0)
                        .otherwise(new When(source.layoutXProperty().greaterThan(target.layoutXProperty().add(target.getWidth()))).then(1).otherwise(0.5))));*/

        mLinkAnchorXTarget.bind(new When(target.layoutXProperty().greaterThan(source.layoutXProperty().add(source.getWidth()))).then(0)
                .otherwise(new When(source.layoutXProperty().greaterThan(target.layoutXProperty().add(target.getWidth()))).then(1).otherwise(0.5)));

        mLinkAnchorYTarget.bind(new When(target.layoutYProperty().greaterThan(source.layoutYProperty().add(H))).then(0)
                .otherwise(new When(source.layoutYProperty().greaterThan(target.layoutYProperty().add(H))).then(1).otherwise(0.5)));

        link.startXProperty().bind(source.layoutXProperty().add(Bindings.multiply(mLinkAnchorXSource,source.getWidth())));
        link.startYProperty().bind(source.layoutYProperty().add(Bindings.multiply(mLinkAnchorYSource,source.getHeight())));
        link.endXProperty().bind(target.layoutXProperty().add(Bindings.multiply(mLinkAnchorXTarget,target.getWidth())));
        link.endYProperty().bind(target.layoutYProperty().add(Bindings.multiply(mLinkAnchorYTarget,target.getHeight())));

        mCurveOrientationX.bind(new When((target.layoutXProperty().greaterThan(source.layoutXProperty().add(source.getWidth())))
                .or(source.layoutXProperty().greaterThan(target.layoutXProperty().add(target.getWidth())))).then(1).otherwise(0));
        mCurveOrientationY.bind(new When((target.layoutXProperty().greaterThan(source.layoutXProperty().add(source.getWidth())))
                .or(source.layoutXProperty().greaterThan(target.layoutXProperty().add(target.getWidth())))).then(0).otherwise(1));

        bindIntent(intent);
        updateArrow();
        arrow.setVisible(true);

    }

    public void bindIntent (Intent intent){

        intentsList.add(intent);
        intent.setBelongingLink(this);
        intent.update();

    }

    public void updateArrow (){

        arrow.update();
    }
    public void updateIntents(){
        for (Intent i : intentsList){
            i.update();
        }
    }

    //aggiunge intent al link
    public void addIntent(Intent intent){
        intentsList.add(intent);
        intent.setBelongingLink(this);
        intentsRepositioning();
        updateIntents();
    }

    //in seguito all'inserimento di un nuovo intent riposiziono le icone degli intent sul link
    public void intentsRepositioning(){
        float size = intentsList.size();
        float increment = 1/(size+1);
        float position = increment;
        for (Intent i : intentsList){
            i.changePosition(position);
            position+=increment;
        }
        /*if (size%2 ==0){//pari
            int i = 0;
            float distanceRate = 2;
            while ( i< size){
                intentsList.get(i).changePosition(0+0.2f*distanceRate);
                intentsList.get(i+1).changePosition(1-0.2f*distanceRate);
                i+=2;
                distanceRate/=2;
            }
        }else if(size%2 !=0){
            intentsList.get(0).changePosition(0.5f);
            int i = 1;
            float distanceRate = 2;
            while ( i< size){
                intentsList.get(i).changePosition(0+0.166f*distanceRate);
                intentsList.get(i+1).changePosition(1-0.166f*distanceRate);
                i+=2;
                distanceRate/=2;
            }
        }*/

    }

    public DraggableActivity getSource() {
        return source;
    }

    public void setSource(DraggableActivity source) {
        this.source = source;
    }

    public DraggableActivity getTarget() {
        return target;
    }

    public void setTarget(DraggableActivity target) {
        this.target = target;
    }

    //delete link and associated intents from graph
    public void delete (){
        //remove reference from source's/target's anchoredLinks list
        source.deleteAnchoredLink(this);
        target.deleteAnchoredLink(this);
        //delete all associated intents
        int size = intentsList.size();
        for (int i = 0; i<size; i++){
            Intent intent = intentsList.get(0);
            intentsList.remove(0);
            intent.delete();
        }
        //remove link from graph
        AnchorPane parent  = (AnchorPane) this.getParent();
        parent.getChildren().remove(this);
    }

    //delete an associated intent from graph
    public void deleteIntent(Intent intent){
        boolean found = false;
        int i = 0;
        while (found == false && i<intentsList.size()){
            if (intentsList.get(i).getId().equals(intent.getId())){
                //remove reference of intent in the intentsList
                intentsList.remove(i);
                intent.delete();
                found = true;
            }
            i++;
        }
    }

    public CubicCurve getCurve() {
        return link;
    }

    public void mouseClickOnLink (MouseEvent mouseEvent){

        //doppio click
        if (mouseEvent.getClickCount() == 2){
            //update attribute inspector
            AttributeInspectorManager inspectorManager = AttributeInspectorManager.getInstance();
            inspectorManager.setText("Link: "+this.getId());
            //highlight and store selected item
            SelectedItem selectedItem = SelectedItem.getInstance();
            selectedItem.setSelectedItem(this);
            //select tree item
            StructureTreeManager treeManager = StructureTreeManager.getInstance();
            TreeItem<TreeItemParameter> item = treeManager.searchTreeItemById(getId(),treeManager.getRootItem());
            treeManager.selectTreeItem(item);

        }
    }

    public void select(){
        int depth = 15;

        DropShadow borderGlow= new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.BLUE);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);
        //borderGlow.setBlurType(BlurType.THREE_PASS_BOX);
        borderGlow.setSpread(0.5);

        this.setEffect(borderGlow);
    }

    public void deselect(){
        this.setEffect(null);
    }
}
