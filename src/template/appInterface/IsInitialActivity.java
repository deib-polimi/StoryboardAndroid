package template.appInterface;

public class IsInitialActivity {
    private static IsInitialActivity instance = null;
    private DraggableActivity initialActivity=null;

    public static IsInitialActivity getInstance() {

        if(instance == null) {
            instance = new IsInitialActivity();
        }
        return instance;
    }

    public void setInitialActivity(DraggableActivity initialActivity) {

        this.initialActivity = initialActivity;
        initialActivity.setInitialIcon(true);
    }

    public DraggableActivity getInitialActivity() {
        return initialActivity;
    }

    public boolean isInitialActivity(DraggableActivity activity){
        if(activity==initialActivity){
            return true;
        }
        return false;
    }
    public void deselectInitialActivity(){

        initialActivity.setInitialIcon(false);
        initialActivity = null;

    }
}
