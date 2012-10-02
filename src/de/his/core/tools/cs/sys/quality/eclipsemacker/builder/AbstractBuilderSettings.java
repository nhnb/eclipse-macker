package de.his.core.tools.cs.sys.quality.eclipsemacker.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.innig.macker.rule.RulesException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import de.his.core.tools.cs.sys.quality.eclipsemacker.custommacker.CustomMacker;


/**
 * 
 * @author keunecke
 */
public abstract class AbstractBuilderSettings {

    /**
     * default folder for macker rule files
     */
    protected static final String SETTINGS_MACKER = ".settings/macker";
    /** projekt settings (HIS1)*/
    private static final String PROPERTIES_FILE = "/../webapps/qisserver/WEB-INF/internal/macker/rules/macker_properties.txt";
    private boolean warnung = false;
    private boolean error = false;
    private boolean defaultM = false;
    private String filterClassContent = "";
    private String filterSourceContent = "";
    private boolean useClassFilter = false;
    private boolean useSourceFilter = false;
    private boolean checkContent = false;
    private boolean runOnFullBuild = false;
    private boolean runOnIncBuild = false;
    private String ruleDir = "";

    /**
     * Project containing the rules
     */
    protected String ruleProject = "";
    private ArrayList<File> ruleFiles = new ArrayList<File>();
    private ArrayList<String> classpathFolders = new ArrayList<String>();
    private ArrayList<String> sourceFolders = new ArrayList<String>();
    /** Das Aktuelle Java Projekt*/
    protected IProject project;
    private IJavaProject jProject;

    /**
     * Create a new AbstractBuilderSettings instance
     */
    public AbstractBuilderSettings() {
        super();
    }

    /**
     * HIS Spezifische settings laden
     */
    protected LinkedHashMap<String, String> loadDispatcherProp() {
    	String s = "";
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    	String separatorDispProp = "=";
    
    	try {
    
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(this.getProject().getLocation().toString() + PROPERTIES_FILE)));
    
    		while((s = in.readLine()) != null) {
    
    			if (!s.startsWith("#")) {
    				StringTokenizer st = new StringTokenizer(s, separatorDispProp);
    				map.put(st.nextToken(), st.nextToken());
    			}
    	      }
    		 in.close();
    
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (NoSuchElementException e) {
    			e.printStackTrace();
    		}
    
    	return map;
    }

    /**
     * Die im angegebenen "Macker Rules Dir." befindlichen Dateien (*.xml)
     * werden instanziiert und in einer Liste gespeichert.
     * 
     * Die Macker Rules Dir ist als relativ zum Projekt zu verstehen, wobei sie mit einem Slash beginnen muss. Mit .. kann
     * man aber auch auf andere Projekte verweisen.
     *
     */
    public void setRulesFromDirectory() {
        ArrayList<File> r = new ArrayList<File>();
        String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
        File dir = new File(workspace + "/" + this.ruleProject + "/" + getRulesDir());
        if (dir.exists() && dir.isDirectory()) {
            addRuleFiles(r, dir);
        }
    
    }

    private void addRuleFiles(ArrayList<File> r, File dir) {
        File[] fileList = dir.listFiles();
        for (File f : fileList) {
            if (f.getName().endsWith(".xml")) {
                r.add(f);
            }
        }
        setRulesFull(r);
    }

    /**
     * Die instanziierten Macker Regelen werden einem CustomMacker
     * Objekt uebergeben.
     * @param cm CustomMacker Objekt.
     */
    public void addRulesToMacker(CustomMacker cm) {
    	for (File f : getRulesFull()) {
    		try {
    			cm.addRulesFile(f);
    		} catch (RulesException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }

    /**
     * Getter um Projekt Einstellungen aus der Property Page zu laden.
     * @param qn QualifiedName Objekt.
     * @return String Value.
     */
    protected String getPersistentProperty(QualifiedName qn) {
    	try {
    		return getProject().getPersistentProperty(qn);
    	} catch (CoreException e) {
    		return "";
    	}
    }

    /**
     * @return the project
     */
    public IProject getProject() {
    	return project;
    }

    /**
     * @return the filterSourceContent
     */
    public String getFilterSourceContent() {
    	return filterSourceContent;
    }

    /**
     * @param filterSourceContent the filterSourceContent to set
     */
    public void setFilterSourceContent(String filterSourceContent) {
    	this.filterSourceContent = filterSourceContent;
    }

    /**
     * @return the useSourceFilter
     */
    public boolean isUseSourceFilter() {
    	return useSourceFilter;
    }

    /**
     * @param useSourceFilter the useSourceFilter to set
     */
    public void setUseSourceFilter(boolean useSourceFilter) {
    	this.useSourceFilter = useSourceFilter;
    }

    /**
     * Laden der Sourcefilter Vorgaben.
     * @return the sources, Liste mit definierten Source Verzeichnissen
     * welche spaeter ueberprueft werden sollen.
     */
    public ArrayList<String> getSources() {
    	if (this.sourceFolders.size() == 0) {
    		//setFilterSourceContent
    		String list = this.getFilterSourceContent();
    		StringTokenizer st = new StringTokenizer(list, "\t");
    
    		while (st.hasMoreTokens()) {
    			sourceFolders.add(st.nextToken());
    		}
    	}
    
    	return sourceFolders;
    }

    /**
     * @param sources the sources to set
     */
    public void setSources(ArrayList<String> sources) {
    	this.sourceFolders = sources;
    }

    /**
     * @return the jProject
     */
    public IJavaProject getjProject() {
    	return jProject;
    }

    /**
     * @param jProject the jProject to set
     */
    public void setjProject(IJavaProject jProject) {
    	this.jProject = jProject;
    }

    /**
     * @return the rulesDir
     */
    public String getRulesDir() {
    	return ruleDir;
    }

    /**
     * @param rulesDir the rulesDir to set
     */
    public void setRulesDir(String rulesDir) {
    	this.ruleDir = rulesDir;
    }

    /**
     * @param project the project to set
     */
    public void setProject(IProject project) {
    	this.project = project;
    }

    /**
     * @return the warnung
     */
    public boolean isWarnung() {
    	return warnung;
    }

    /**
     * @param warnung the warnung to set
     */
    public void setWarnung(boolean warnung) {
    	this.warnung = warnung;
    }

    /**
     * @return the error
     */
    public boolean isError() {
    	return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(boolean error) {
    	this.error = error;
    }

    /**
     * @return the defaultM
     */
    public boolean isDefaultM() {
    	return defaultM;
    }

    /**
     * @param defaultM the defaultM to set
     */
    public void setDefaultM(boolean defaultM) {
    	this.defaultM = defaultM;
    }

    /**
     * @return the filterContent
     */
    public String getFilterContent() {
    	return filterClassContent;
    }

    /**
     * @param filterContent the filterContent to set
     */
    public void setFilterContent(String filterContent) {
    	this.filterClassContent = filterContent;
    }

    /**
     * @return the useFilter
     */
    public boolean isUseFilter() {
    	return useClassFilter;
    }

    /**
     * @param useFilter the useFilter to set
     */
    public void setUseFilter(boolean useFilter) {
    	this.useClassFilter = useFilter;
    }

    /**
     * @return the checkContent
     */
    public boolean isCheckContent() {
    	return checkContent;
    }

    /**
     * @param checkContent the checkContent to set
     */
    public void setCheckContent(boolean checkContent) {
    	this.checkContent = checkContent;
    }

    /**
     * @return the runOnFullBuild
     */
    public boolean isRunOnFullBuild() {
    	return runOnFullBuild;
    }

    /**
     * @param runOnFullBuild the runOnFullBuild to set
     */
    public void setRunOnFullBuild(boolean runOnFullBuild) {
    	this.runOnFullBuild = runOnFullBuild;
    }

    /**
     * @return the runOnIncBuild
     */
    public boolean isRunOnIncBuild() {
    	return runOnIncBuild;
    }

    /**
     * @param runOnIncBuild the runOnIncBuild to set
     */
    public void setRunOnIncBuild(boolean runOnIncBuild) {
    	this.runOnIncBuild = runOnIncBuild;
    }

    /**
     * @return the rulesFull
     */
    public ArrayList<File> getRulesFull() {
    	return ruleFiles;
    }

    /**
     * @param rulesFull the rulesFull to set
     */
    public void setRulesFull(ArrayList<File> rulesFull) {
    	this.ruleFiles = rulesFull;
    }

    /**
     * Ermitteln der vom Projekt verwendeten ClassPath Verzeichnisse.
     * @return the classpaths
     */
    public ArrayList<String> getClasspaths() {
    	if (this.classpathFolders.size() == 0) {
    		IJavaProject jp = getjProject();
    		try {
    			IClasspathEntry[] rawClasspath = jp.getRawClasspath();
    			for (int i = 0; i < rawClasspath.length; i++) {
    				if (!rawClasspath[i].getPath().toOSString().startsWith("org.eclipse")) {
    					classpathFolders.add(rawClasspath[i].getPath().toOSString());
    				}
    
    			}
    		} catch (JavaModelException e) {
    			e.printStackTrace();
    		}
    	}
    
    	return classpathFolders;
    }

    /**
     * @return all classpath elements
     */
    public ArrayList<File> getClasspathElements() {
    	ArrayList<File> jars = new ArrayList<File>();
    	IJavaProject jp = getjProject();
    	IClasspathEntry[] rawClasspath;
    	IPath location = jp.getProject().getLocation();
    	try {
    		rawClasspath = jp.getRawClasspath();
    		for (int i = 0; i < rawClasspath.length; i++) {
    			IPath iPath = rawClasspath[i].getPath();
    			iPath = iPath.removeFirstSegments(1); //this removes the webapps prefix
    			IPath fullPathToJar = location.append(iPath);
    			File file = fullPathToJar.toFile();
    			jars.add(file);
    		}
    		IPath outputLocation = jp.getOutputLocation().removeFirstSegments(1);
    		File outputFolder = location.append(outputLocation).toFile();
    		jars.add(outputFolder);
    	} catch (JavaModelException e) {
    		e.printStackTrace();
    	}
    	return jars;
    }

    /**
     * @param classpaths the classpaths to set
     */
    public void setClasspaths(ArrayList<String> classpaths) {
    	this.classpathFolders = classpaths;
    }

    /**
     * Initialize settings
     * @return the initialized settings object
     */
    public abstract AbstractBuilderSettings initSettings();

}