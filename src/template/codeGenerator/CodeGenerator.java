package template.codeGenerator;

import com.google.common.io.Resources;
import javafx.scene.control.Alert;
import template.activities.BottomNavigationActivity;
import template.managers.ProjectHandler;
import template.appInterface.DraggableActivity;
import template.utils.DragControllerType;
import template.utils.IconsFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CodeGenerator {
    //private String javaOverwritedPath = ProjectHandler.getInstance().getProjectPath()+"/app/src/main/java/overwrited";
    //private String layoutOverwritedPath = ProjectHandler.getInstance().getProjectPath()+"/app/src/main/res/overwrited";
    private String overwritedPath = ProjectHandler.getInstance().getProjectPath()+"/app/src/main/overwrited";

    public CodeGenerator() {
    }

    private void copyJavaClass(String filename ,File sourceFile){
        File f = new File (overwritedPath);
        if (!f.exists()){
            //create a folder that will contains overwrited java files
            f.mkdirs();
        }
        f= new File(overwritedPath+"/"+filename+".java");
        f.delete();
        //create a copy of the file
        sourceFile.renameTo(new File(overwritedPath+"/"+filename+".java"));
    }
    private void copyLayoutClass(String filename, File sourceFile){
        File f = new File (overwritedPath);
        if (!f.exists()){
            //create a folder that will contains overwrited xml files
            f.mkdirs();
        }
        f= new File(overwritedPath+"/"+filename+".xml");
        f.delete();
        //create a copy of the file
        sourceFile.renameTo(new File(overwritedPath+"/"+filename+".xml"));
    }

    public void generateCode(DraggableActivity activity) throws IOException {
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        //create java file

        File f = new File(projectHandler.getProjectPath()+"/app/src/main/java/"+projectHandler.getPackagePath()+"/"+activity.getName()+".java");
        //File f = new File("C:/Users/utente/Desktop/"+activity.getName()+".java");
        copyJavaClass(activity.getName(),f);
        f.getParentFile().mkdirs();
        f.createNewFile();
        //write code in java file
        BufferedWriter writer = new BufferedWriter(
                new FileWriter( projectHandler.getProjectPath()+"/app/src/main/java/"+projectHandler.getPackagePath()+"/"+activity.getName()+".java"));
        /*BufferedWriter writer = new BufferedWriter(
                new FileWriter( "C:/Users/utente/Desktop/"+activity.getName()+".java"));*/
        if(activity.isFragment()){
            writer.write(activity.createFragmentCode());
        }else{
            writer.write(activity.createJavaCode());
        }
        //writer.write(activity.createJavaCode());
        if ( writer != null)
            writer.close( );

        List<String> wellFormedOut =wellFormedJavaCode(projectHandler.getProjectPath()+"/app/src/main/java/"+projectHandler.getPackagePath()+"/"+activity.getName()+".java");
        writer = new BufferedWriter(
                new FileWriter( projectHandler.getProjectPath()+"/app/src/main/java/"+projectHandler.getPackagePath()+"/"+activity.getName()+".java"));
        for(String line: wellFormedOut){
            writer.write(line);
            writer.newLine();
        }
        if ( writer != null)
            writer.close( );

        //create xml file
        f = new File(projectHandler.getProjectPath()+"/app/src/main/res/layout/"
                +activity.generateLayoutName(activity.getName())+".xml");
        //f = new File("C:/Users/utente/Desktop/"+activity.getName()+".xml");
        copyLayoutClass(activity.generateLayoutName(activity.getName()),f);
        f.getParentFile().mkdirs();
        f.createNewFile();
        //write code in xml file
        writer = new BufferedWriter(
                new FileWriter( projectHandler.getProjectPath()+"/app/src/main/res/layout/"
                        //+generateLayoutName(activity.getName())+".xml"));
                        +activity.generateLayoutName(activity.getName())+".xml"));
        /*writer = new BufferedWriter(
                new FileWriter( "C:/Users/utente/Desktop/"+activity.getName()+".xml"));*/
        writer.write(activity.createXMLCode());
        if ( writer != null)
            writer.close( );

        wellFormedOut =null;
        wellFormedOut = wellFormedXMLCode(projectHandler.getProjectPath()+"/app/src/main/res/layout/"
                +activity.generateLayoutName(activity.getName())+".xml");
        writer = new BufferedWriter(
                new FileWriter( projectHandler.getProjectPath()+"/app/src/main/res/layout/"
                        +activity.generateLayoutName(activity.getName())+".xml"));

        for(String line: wellFormedOut){
            writer.write(line);
            writer.newLine();
        }
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
            if(!a.isFragment()){
                activities = activities.concat(a.getManifest()+"\n\t\t");
            }
        }
        if(searchActivityByType(activitiesList, DragControllerType.loginActivity)){
            template = template.replace("${PERMISSIONS}","<!-- To auto-complete the email text field in the login form with the user's emails -->\n\t"
                    +"<uses-permission android:name=\"android.permission.GET_ACCOUNTS\" />\n"
                    +"<uses-permission android:name=\"android.permission.READ_PROFILE\" />\n"
                    +"<uses-permission android:name=\"android.permission.READ_CONTACTS\" />");
        }else{
            template = template.replace("${PERMISSIONS}","");
        }

        template = template.replace("${PACKAGE}",ProjectHandler.getInstance().getPackage());
        template = template.replace("${ACTIVITIES}",activities);

        ProjectHandler projectHandler = ProjectHandler.getInstance();
        //create manifest file
        File f = new File(projectHandler.getManifestPath());
        //copyLayoutClass("AndroidManifest",f);
        f.getParentFile().mkdirs();
        f.createNewFile();
        //write code in java file
        BufferedWriter writer = new BufferedWriter(
                new FileWriter( projectHandler.getManifestPath()));
        writer.write(template);
        if ( writer != null)
            writer.close( );

        List<String> wellFormedOut =wellFormedXMLCode(projectHandler.getManifestPath());
        writer = new BufferedWriter(
                new FileWriter( projectHandler.getManifestPath()));
        for(String line: wellFormedOut){
            writer.write(line);
            writer.newLine();
        }
        if ( writer != null)
            writer.close( );



    }

    public void generateStylesFile() throws IOException {
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        File f= new File(projectHandler.getProjectPath()+"/app/src/main/res/values/styles.xml");
        f.getParentFile().mkdirs();
        f.createNewFile();

        BufferedWriter writer = new BufferedWriter(
                new FileWriter( projectHandler.getProjectPath()+"/app/src/main/res/values/styles.xml"));
        writer.write(provideTemplateForName("templates/stylesTemplate"));
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
    private boolean searchActivityByType(List<DraggableActivity> activitiesList,DragControllerType type){
        boolean found = false;
        for(DraggableActivity a : activitiesList){
            if (a.getType() == type){
                found = true;
                return found;
            }
        }
        return found;
    }

    private List<String>wellFormedJavaCode(String path) throws IOException {
        FileReader in = new FileReader(path);
        BufferedReader br = new BufferedReader(in);
        List<String> out = new ArrayList<String>();
        int open = 0;
        String s = "";
        boolean closeBrace = false;
        boolean openBrace =false;
        while ((s = br.readLine()) != null) {
            s=s.trim();
            Scanner scanner = new Scanner(s);

            while(scanner.hasNext()){
                String x =scanner.next();
                int z = x.length();
                for(int y = 0; y < z; y++){
                    if((x.charAt(y))=='{'){
                        openBrace = true;

                    }else if((x.charAt(y))=='}'){
                        closeBrace = true;
                    }
                }
            }
            if(closeBrace && !openBrace){
                open--;
                closeBrace = false;
            }
            for (int i = 0;i<open;i++){
                s = "\t"+s;
            }
            out.add(s);
            if (openBrace && !closeBrace){
                open++;
                openBrace = false;
            }
            if (openBrace&& closeBrace){
                openBrace = false;
                closeBrace = false;
            }
        }
        return out;

    }

    private List<String>wellFormedXMLCode(String path) throws IOException {
        FileReader in = new FileReader(path);
        BufferedReader br = new BufferedReader(in);
        List<String> out = new ArrayList<String>();
        int open = 0;
        String s = "";
        boolean closeTag = false;
        boolean openTag =false;
        boolean emptyCloseTag = false;
        while ((s = br.readLine()) != null) {
            s=s.trim();
            Scanner scanner = new Scanner(s);

            while(scanner.hasNext()){
                String x =scanner.next();
                int z = x.length();
                for(int y = 0; y < z; y++){
                    if((x.charAt(y))=='<' && (x.charAt(y+1))!='/' && (x.charAt(y+1))!='?'){
                        openTag = true;

                    }else if((x.charAt(y))=='<' && (x.charAt(y+1))=='/'){
                        closeTag = true;
                    }else if ((x.charAt(y))=='/' && (x.charAt(y+1))=='>'){
                        emptyCloseTag = true;
                    }
                }
            }
            if(closeTag && !openTag){
                open--;
                closeTag = false;
            }
            for (int i = 0;i<open;i++){
                s = "\t"+s;
            }
            out.add(s);
            if (openTag && !closeTag){
                open++;
                openTag = false;
            }
            if(emptyCloseTag){
                open--;
                emptyCloseTag = false;
            }
            if (openTag && closeTag){
                openTag = false;
                closeTag = false;
            }
        }
        return out;
    }

    public void generateNavigationMenu(List<BottomNavigationActivity> activities) throws IOException {
        String template = provideTemplateForName("templates/NavigationMenu");
        int n = 0;
        String num ="";
        List<String> icons = new ArrayList<String>();
        for(BottomNavigationActivity a: activities){
            //generate navigation.xml file containing menu with items of bottom navigation avtivity
            if(n!=0){
                num = Integer.toString(n);
            }
            String items="";
            items = items.concat(((BottomNavigationActivity)a).createNavigationMenu());
            template = template.replace("${ITEMS}",items);

            ProjectHandler projectHandler = ProjectHandler.getInstance();
            File f = new File(projectHandler.getProjectPath()+"/app/src/main/res/menu/navigation"+num+".xml");
            copyLayoutClass("navigation"+num,f);
            f.getParentFile().mkdirs();
            f.createNewFile();
            //write code in xml file
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter( projectHandler.getProjectPath()+"/app/src/main/res/menu/navigation"+num+".xml"));
            writer.write(template);
            if ( writer != null)
                writer.close( );

            List<String> wellFormedOut =null;
            wellFormedOut = wellFormedXMLCode(projectHandler.getProjectPath()+"/app/src/main/res/menu/navigation"+num+".xml");
            writer = new BufferedWriter(
                    new FileWriter( projectHandler.getProjectPath()+"/app/src/main/res/menu/navigation"+num+".xml"));

            for(String line: wellFormedOut){
                writer.write(line);
                writer.newLine();
            }
            if ( writer != null)
                writer.close( );
            n++;
            for(String icon : a.getIcons()){
                if(!icons.contains(icon)){
                    icons.add(icon);
                }
            }

        }
        generateIcons(icons);

    }

    private void generateIcons(List<String> icons) throws IOException {
        for(String icon:icons){
            ProjectHandler projectHandler = ProjectHandler.getInstance();
            File f = new File(projectHandler.getProjectPath()+"/app/src/main/res/drawable/"+icon+".xml");
            f.getParentFile().mkdirs();
            f.createNewFile();
            //write code in xml file
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter( projectHandler.getProjectPath()+"/app/src/main/res/drawable/"+icon+".xml"));
            writer.write(getIconFile(icon));
            if ( writer != null)
                writer.close( );
        }
    }
    private String getIconFile(String icon) throws IOException {
        String template = provideTemplateForName("templates/DrawableIcon");
        String path ="";
        switch(icon){
            case "ic_android_black_24dp":
                path = IconsFile.ANDROID;
                break;
            case "ic_build_black_24dp":
                path = IconsFile.BUILD;
                break;
            case "ic_dashboard_black_24dp":
                path = IconsFile.DASHBOARD;
                break;
            case "ic_edit_black_24dp":
                path = IconsFile.EDIT;
                break;
            case "ic_home_black_24dp":
                path = IconsFile.HOME;
                break;
            case "ic_notifications_black_24dp":
                path = IconsFile.NOTIFICATIONS;
                break;
            case "ic_person_black_24dp":
                path = IconsFile.PERSON;
                break;
            case "ic_share_black_24dp":
                path = IconsFile.SHARE;
                break;
            default:
                path = null;
                break;
        }
        template = template.replace("${PATH}","\""+path+"\"");
        return template;
    }

    public void generateJavaFile(String path,String content,String fileName) throws IOException {
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        //create java file

        File f = new File(path);
        copyJavaClass(fileName,f);
        f.getParentFile().mkdirs();
        f.createNewFile();
        //write code in java file
        BufferedWriter writer = new BufferedWriter(
                new FileWriter( path));
        /*BufferedWriter writer = new BufferedWriter(
                new FileWriter( "C:/Users/utente/Desktop/"+activity.getName()+".java"));*/

        writer.write(content);
        //writer.write(activity.createJavaCode());
        if ( writer != null)
            writer.close( );

        List<String> wellFormedOut =wellFormedJavaCode(path);
        writer = new BufferedWriter(
                new FileWriter( path));
        for(String line: wellFormedOut){
            writer.write(line);
            writer.newLine();
        }
        if ( writer != null)
            writer.close( );
    }

    public void generateXMLFile(String path,String content,String fileName) throws IOException {
        ProjectHandler projectHandler = ProjectHandler.getInstance();
        //create java file

        File f = new File(path);
        copyLayoutClass(fileName,f);
        f.getParentFile().mkdirs();
        f.createNewFile();
        //write code in java file
        BufferedWriter writer = new BufferedWriter(
                new FileWriter( path));
        /*BufferedWriter writer = new BufferedWriter(
                new FileWriter( "C:/Users/utente/Desktop/"+activity.getName()+".java"));*/

        writer.write(content);
        //writer.write(activity.createJavaCode());
        if ( writer != null)
            writer.close( );

        List<String> wellFormedOut =wellFormedXMLCode(path);
        writer = new BufferedWriter(
                new FileWriter( path));
        for(String line: wellFormedOut){
            writer.write(line);
            writer.newLine();
        }
        if ( writer != null)
            writer.close( );
    }

}

