package template.sample;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.transform.Rotate;

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



    public Intent(CubicCurve curve, float t, double radius, IntentType type ) {

        super(radius);
        this.curve = curve;
        this.t = t;
        this.type = type;
        this.img = setImage(type);
        setFill(new ImagePattern(img));
        setStroke(Color.BLACK);
        update();
        setId(UUID.randomUUID().toString());
    }

    private Image setImage(IntentType type){
        Image image=null;

        switch (type) {

            case buttonClick:
                image= new Image(getClass().getResource("/img/container.png").toString());
                break;

            case implicit:
                image= new Image(getClass().getResource("/img/gridview.png").toString());
                break;

            case embed:
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
        //remove intent from graph
        AnchorPane parent  = (AnchorPane) this.getParent();
        parent.getChildren().remove(this);




    }


}
