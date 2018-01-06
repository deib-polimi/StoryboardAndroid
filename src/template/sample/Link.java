package template.sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by utente on 16/12/2017.
 */
public class Link extends AnchorPane {

    @FXML
    CubicCurve link;
    @FXML
    Circle link_type;

    Image img = new Image(getClass().getResource("/img/container.png").toString());

    private final DoubleProperty mControlOffsetX = new SimpleDoubleProperty();
    private final DoubleProperty mControlOffsetY = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX2 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY2 = new SimpleDoubleProperty();
    private final DoubleProperty middleValueX = new SimpleDoubleProperty();
    private final DoubleProperty middleValueY = new SimpleDoubleProperty();

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
        link_type.setVisible(false);

        mControlOffsetX.set(100.0);
        mControlOffsetY.set(50.0);

        mControlDirectionX1.bind(new When(
                link.startXProperty().greaterThan(link.endXProperty()))
                .then(-1.0).otherwise(1.0));

        mControlDirectionX2.bind(new When (
                link.startXProperty().greaterThan(link.endXProperty()))
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
        link_type.centerXProperty().bind(Bindings.add(link.startXProperty(),link.endXProperty()).divide(2));
        link_type.centerYProperty().bind(Bindings.add(link.startYProperty(),link.endYProperty()).divide(2));


    }

    public void setStart(Point2D startPoint) {

        link.setStartX(startPoint.getX());
        link.setStartY(startPoint.getY());
    }

    public void setEnd(Point2D endPoint) {

        link.setEndX(endPoint.getX());
        link.setEndY(endPoint.getY());
    }

    public void bindEnds (DraggableActivity source, DraggableActivity target) {
        link.startXProperty().bind(
                Bindings.add(source.layoutXProperty(), (source.getWidth() / 2.0)));

        link.startYProperty().bind(
                Bindings.add(source.layoutYProperty(), (source.getWidth() / 2.0)));

        link.endXProperty().bind(
                Bindings.add(target.layoutXProperty(), (target.getWidth() / 2.0)));

        link.endYProperty().bind(
                Bindings.add(target.layoutYProperty(), (target.getWidth() / 2.0)));
        bindIcon();
    }

    public void bindIcon (){

        link_type.setFill(new ImagePattern(img));
        link_type.setVisible(true);

    }
}
