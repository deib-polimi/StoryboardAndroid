package template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sun.javafx.application.PlatformImpl;
import javafx.stage.Stage;
import template.managers.ProjectHandler;
import template.appInterface.Main;

public class PluginAction extends AnAction {
    private Stage stage;
    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public PluginAction() {
        // Set the menu item name.
        super("Plugin");
        // Set the menu item name, description and icon.
        //super("Text _Boxes","Item description", IconLoader.getIcon("/icons/erDiagram.png"));
    }

    public void actionPerformed(AnActionEvent event) {

        /*Project project = event.getData(PlatformDataKeys.PROJECT);
        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        Messages.showMessageDialog(project, "Ciao, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());*/
        //Application.launch(Main.class, (java.lang.String[])null);
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        projectHandler.setEvent(event);
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        });


    }

    private void initialize() {
        try {
            Main main = new Main();
            if (stage == null) {
                stage = new Stage();
                main.start(stage);
            }else{
                stage.toFront();
            }
            //main.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
