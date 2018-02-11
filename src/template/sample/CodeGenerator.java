package template.sample;

import com.google.common.io.Resources;
import template.ProjectHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

/**
 * Created by utente on 09/02/2018.
 */
public class CodeGenerator {

    public CodeGenerator() {
    }

    public void generateCode(DraggableActivity activity) throws IOException {
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        //create java file
        File f = new File(projectHandler.getProjectPath()+"/app/src/main/java/"+projectHandler.getPackagePath()+"/"+activity.getName()+".java");
        f.getParentFile().mkdirs();
        f.createNewFile();
        //write code in java file
        BufferedWriter writer = new BufferedWriter(
                new FileWriter( projectHandler.getProjectPath()+"/app/src/main/java/"+projectHandler.getPackagePath()+"/"+activity.getName()+".java"));
        writer.write(activity.createJavaCode());
        if ( writer != null)
            writer.close( );
        //create xml file
        f = new File(projectHandler.getProjectPath()+"/app/src/main/res/layout/activity"
                +generateLayoutName(activity.getName())+".xml");
        f.getParentFile().mkdirs();
        f.createNewFile();
        //write code in xml file
        writer = new BufferedWriter(
                new FileWriter( projectHandler.getProjectPath()+"/app/src/main/res/layout/activity"
                        +generateLayoutName(activity.getName())+".xml"));
        writer.write(activity.createXMLCode());
        if ( writer != null)
            writer.close( );
    }

    public String provideTemplateForName(String templateName) throws IOException {

        java.net.URL p = getClass().getResource(templateName);

        try {
            //convert templates from txt file to string
            return Resources.toString(p, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void generateManifest(List<DraggableActivity> activitiesList) throws IOException {
        String template = provideTemplateForName("templates/ManifestTemplate");
        String activities = "";

        for(DraggableActivity a : activitiesList){
            activities = activities.concat(a.getManifest()+"\n\t\t");
        }

        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        template = template.replace("${ACTIVITIES}",activities);

        ProjectHandler projectHandler = ProjectHandler.getInstance();
        //create manifest file
        File f = new File(projectHandler.getManifestPath());
        f.getParentFile().mkdirs();
        f.createNewFile();
        //write code in java file
        BufferedWriter writer = new BufferedWriter(
                new FileWriter( projectHandler.getManifestPath()));
        writer.write(template);
        if ( writer != null)
            writer.close( );
    }

    //generate layout name from object name. Ex: MyClass -> _my_class
    private String generateLayoutName(String activityName){
        Scanner in = new Scanner(activityName);
        String out = "";
        String x = in.next();
        int z = x.length();
        for(int y = 0; y < z; y++){
            if(Character.isUpperCase(x.charAt(y))){
                out = out+"_"+(Character.toLowerCase(x.charAt(y)));

            }else{
                out = out+x.charAt(y);
            }
        }
        return out;
    }

}
