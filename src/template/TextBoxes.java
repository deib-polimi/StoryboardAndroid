package template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.jetbrains.jps.cmdline.Launcher;
import template.sample.Main;
import javafx.*;
import template.sample.SampleController;
import com.sun.javafx.tk.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;


public class TextBoxes extends AnAction {
    private Stage stage;
    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public TextBoxes() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        //super("Text _Boxes","Item description", IconLoader.getIcon("/icons/erDiagram.png"));
    }

    public void actionPerformed(AnActionEvent event) {

        /*Project project = event.getData(PlatformDataKeys.PROJECT);
        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        Messages.showMessageDialog(project, "Ciao, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());*/
        Application.launch(Main.class, (java.lang.String[])null);


    }

}
