package template.appInterface;

public class TreeItemParameter {
    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    private String value;
    private String id;

    public TreeItemParameter(String value, String id) {
        this.value = value;
        this.id = id;
    }
    public String toString() { return value; }

    public void setValue(String value) {
        this.value = value;
    }
}
