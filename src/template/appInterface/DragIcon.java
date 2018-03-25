package template.appInterface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import template.utils.DragControllerType;

import java.io.IOException;

public class DragIcon extends AnchorPane {

    @FXML
    private ImageView icon_image;

    private DragControllerType mType;

    public DragIcon(){

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("DragIcon.fxml")
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
    }

    public DragControllerType getType() { return mType;}

    public void setType(DragControllerType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("dragicon");
        getStyleClass().add("icon-green");
        switch (mType) {

            case basicActivity:
                icon_image.setImage(new Image(getClass().getResource("/img/basicIcon2.jpg").toString()));
                break;

            case listView:
                icon_image.setImage(new Image(getClass().getResource("/img/listIcon.jpg").toString()));
                break;

            case gridView:
                icon_image.setImage(new Image(getClass().getResource("/img/gridIcon.jpg").toString()));
                break;

            case emptyActivity:
                icon_image.setImage(new Image(getClass().getResource("/img/emptyIcon.jpg").toString()));
                break;

            case loginActivity:
                icon_image.setImage(new Image(getClass().getResource("/img/loginIcon.jpg").toString()));
                break;

            case tabbedActivity:
                icon_image.setImage(new Image(getClass().getResource("/img/tabbedIcon.jpg").toString()));
                break;

            case bottomNavigationActivity:
                icon_image.setImage(new Image(getClass().getResource("/img/bottomIcon.jpg").toString()));
                break;

            case cardView:
                icon_image.setImage(new Image(getClass().getResource("/img/cardIcon.jpg").toString()));
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
                (int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
                (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
        );
    }
}
