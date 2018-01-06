package template.sample;

import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*public class DragContainer {

    private String source;
    private String target;
    private DragType type;

    public DragContainer() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public DragType getType() {
        return type;
    }

    public void setType(DragType type) {
        this.type = type;
    }

    public void clean (){
        this.setSource("");
        this.setTarget("");
        this.setType(DragType.empty);
    }*/


public class DragContainer implements Serializable {

    private static final long serialVersionUID = -1458406119115196098L;

    public static final DataFormat AddNode = new DataFormat("application.DragIcon.add");
    public static final DataFormat DragNode = new DataFormat("application.DraggableNode.drag");
    public static final DataFormat AddLink = new DataFormat("application.NodeLink.add");

    private final List<Pair<String, Object> > mDataPairs = new ArrayList<Pair<String, Object> >();


    public DragContainer () {
    }

    public void addData (String key, Object value) {
        mDataPairs.add(new Pair<String, Object>(key, value));
    }

    public  <T> T getValue (String key) {

        for (Pair<String, Object> data: mDataPairs) {

            if (data.getKey().equals(key))
                return (T) data.getValue();

        }

        return null;
    }

    public List<Pair<String, Object>> getData () { return mDataPairs; }
}

