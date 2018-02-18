package template.sample;

import javafx.scene.shape.CubicCurve;
import template.attributeInspector.BottomNavigationIntentAttributes;
import template.attributeInspector.TabIntentAttributes;
import template.managers.AttributeInspectorManager;

import java.io.IOException;

/**
 * Created by utente on 17/02/2018.
 */
public class BottomNavigationIntent extends Intent {
    private BottomNavigationIntentAttributes intentInspector = null;
    private CodeGenerator codeGenerator = new CodeGenerator();
    private String classTemplate;
    private String navigationItemTemplate;
    private String icon;

    public BottomNavigationIntent(CubicCurve curve, float t, double radius, IntentType type) throws IOException {
        super(curve, t, radius, type);

        this.intentInspector = new BottomNavigationIntentAttributes();
        super.setName("item");
        icon = "Android";
        classTemplate = codeGenerator.provideTemplateForName("templates/BottomNavigationItemSelection");
        navigationItemTemplate = codeGenerator.provideTemplateForName("templates/NavigationItem");
        intentInspector.createListeners(this);
    }

    public int getOrder() {
        BottomNavigationActivity container = (BottomNavigationActivity) super.getBelongingLink().getSource();
        return container.getOrder(this);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconId(){
        switch(icon){
            case "Android":
                return "ic_android_black_24dp";
            case "Build":
                return "ic_build_black_24dp";
            case "Dashboard":
                return "ic_dashboard_black_24dp";
            case "Edit":
                return "ic_edit_black_24dp";
            case "Home":
                return "ic_home_black_24dp";
            case "Notifications":
                return "ic_notifications_black_24dp";
            case "Person":
                return "ic_person_black_24dp";
            case "Share":
                return "ic_share_black_24dp";
            default:
                return null;
        }
    }

    public BottomNavigationIntentAttributes getIntentInspector (){
        intentInspector.fillValues(this);
        return intentInspector;
    }

    @Override
    public void loadAttributeInspector(){
        AttributeInspectorManager inspectorManager = AttributeInspectorManager.getInstance();
        intentInspector.fillValues(this);
        inspectorManager.loadIntentInspector(intentInspector,this);
    }
    public String getIntentCode(){
        String template = classTemplate;
        template = template.replace("${ITEM_ID}","item"+Integer.toString(getOrder()+1));
        template = template.replace("${FRAGMENT}",super.getBelongingLink().getTarget().getName());
        return template;
    }
    public String getNavigationMenuCode(){
        String template = navigationItemTemplate;
        template = template.replace("${ITEM_ID}","item"+Integer.toString(getOrder()+1));
        template = template.replace("${ICON}",getIconId());
        template = template.replace("${TITLE}",super.getName());
        return template;
    }
    public void fillValues(BottomNavigationIntent intent){
        intentInspector.fillValues(this);
    }
}
