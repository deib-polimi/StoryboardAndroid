package template;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.scene.control.Alert;
import org.apache.velocity.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import template.managers.StructureTreeManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.actionSystem.AnAction.getEventProject;

/**
 * Created by utente on 04/02/2018.
 */
public class ProjectHandler {
    private Project project;
    private VirtualFile selectedFile;
    private String mPackage;
    private AnActionEvent event;
    private static ProjectHandler instance = null;
    private String projectPath;
    private String manifestPath;
    private String projectName;

    public ProjectHandler() {
    }

    public static ProjectHandler getInstance() {

        if(instance == null) {
            instance = new ProjectHandler();
        }
        return instance;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public VirtualFile getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(VirtualFile selectedFile) {
        this.selectedFile = selectedFile;
    }

    public String getPackagePath() {

        String packagePath = mPackage.replace(".","/");
        return packagePath;
    }
    public String getPackage(){
        return mPackage;
    }

    public void setPackage(String mPackage) {
        this.mPackage = mPackage;
    }

    public AnActionEvent getEvent() {
        return event;
    }

    public void setEvent(AnActionEvent event) {
        this.event = event;
        this.project = getEventProject(event);
        this.projectPath = getEventProject(event).getBasePath();
        this.selectedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        this.mPackage = getPackageName(this.project,event);
        this.projectName = getEventProject(event).getName();
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    private String getPackageName(Project project, AnActionEvent event) {
        try {
            for (String path : possibleManifestPaths()) {
                VirtualFile file = getManifestFromPath(project, path);
                if (file != null && file.exists()) {
                    manifestPath = path;
                    return extractPackageFromManifestStream(file.getInputStream());
                }
            }
            for (String path : sourceRootPaths(project, event)) {
                VirtualFile file = getManifestFromPath(project, path);
                if (file != null && file.exists()) {
                    manifestPath = path;
                    return extractPackageFromManifestStream(file.getInputStream());
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private ArrayList<String> possibleManifestPaths() {
        return Lists.newArrayList("", "app/", "app/src/main/", "src/main/", "res/");
    }
    private VirtualFile getManifestFromPath(Project project, String path) {
        VirtualFile folder = project.getBaseDir().findFileByRelativePath(path);
        if (folder != null) {
            return folder.findChild("AndroidManifest.xml");
        }
        return null;
    }
    private List<String> sourceRootPaths(Project project, AnActionEvent event) {
        return getSourceRootPathList(project, event);
    }
    private String extractPackageFromManifestStream(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        Element manifest = (Element)doc.getElementsByTagName("manifest").item(0);
        return manifest.getAttribute("package");
    }

    private List<String> getSourceRootPathList(Project project, AnActionEvent event) {
        List<String> sourceRoots = Lists.newArrayList();
        String projectPath = StringUtils.normalizePath(project.getBasePath());
        for (VirtualFile virtualFile : getModuleRootManager(event).getSourceRoots(false)) {
            sourceRoots.add(StringUtils.normalizePath(virtualFile.getPath()).replace(projectPath, ""));
        }
        return sourceRoots;
    }

    private ModuleRootManager getModuleRootManager(AnActionEvent event) {
        return ModuleRootManager.getInstance(event.getData(LangDataKeys.MODULE));
    }

    public String getManifestPath() {
        return projectPath+"/"+manifestPath+"AndroidManifest.xml";
    }
}
