package template.sample;

/**
 * Created by utente on 08/02/2018.
 */
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
        initialActivity = null;
    }
}
