package de.his.core.tools.cs.sys.quality.eclipsemacker.gui;

import java.io.File;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Diese Klasse erstellt eine "Property Page" fuer jedes
 * Java Projekt im Workspace um Plugin bezogene Einstellungen
 * vorzunehmen.
 *
 * @author Bender
 */
public class Property extends PropertyPage {
    private static final String WEBAPPS = "webapps";

    /*
     * Default Values
     */
    private static final String RULES_DIR = ".settings/macker";

    private static final String FILTER_CLASSPATH = "de/his/appclient/\tde/his/appserver/\tde/his/core/";

    private static final String FILTER_SOURCE = "src/java/\tsrc/generated\tsrc/patches";

    private static final boolean INC_BUILD = true;

    private static final boolean FULL_BUILD = false;

    private static final boolean CHECK_CONTENT = false;

    private static final boolean DEFAULT_WARNING = false;

    private static final boolean DEFAULT_ERROR = false;

    private static final boolean DEFAULT = true;

    private static final boolean USE_CLASSPTAH_FILTER = true;

    private static final boolean USE_SOURCE_FILTER = true;

    private static final boolean USE_HIS_SETTINGS = true;

    /*
     * Gui Komponenten
     */
    private Text rulesDir;

    private Text rulesProject;

    private Button incBuild;

    private Button fullBuild;

    private Button checkContent;

    private Composite radioGroup;

    private Button warning;

    private Button error;

    private Button defaultM;

    private Button useFilter;

    private Button useSourceFilter;

    private Button buttonCheck;

    Button hisSettings;

    List list;

    List listSource;

    private Button buttonRemoveSource;

    private Button buttonAddSource;

    Text sourcePath;

    private Button buttonRemoveClass;

    private Button buttonAddClass;

    Text classPath;

    /**
     * Constructor.
     */
    public Property() {
        super();
    }




    /**
     * GUI der Propertypage.
     * @param parent
     */

    private void addSection(Composite parent) {
        //HIS Settings Checkbox
        addHisSettings(parent);

        addRulesDir(parent);
        Label labelCf = new Label(parent, SWT.NONE);
        labelCf.setText("Classpath Filter");
        addClasspathFilter(parent);

        Label label = new Label(parent, SWT.NONE);
        label.setText("Source Filter");
        addSourceFilter(parent);

        addCheckboxes(parent);

        // Lade aktuelle Einstellungen
        try {
            IResource resource = ((IJavaProject) getElement()).getResource();
            String rPath = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.RULES_PATH));
            String incB = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.RUN_ON_INCREMENTAL_BUILD));
            String fullB = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.RUN_ON_FULL_BUILD));
            String checkC = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.CHECK_CONTENT));
            String filter = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.CLASSPATH_FILTER));
            String useF = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.USE_CLASSPATH_FILTER));

            String filterS = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.SOURCE_FILTER));
            String useFS = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.USE_SOURCE_FILTER));

            String rW = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.WARNING));
            String rE = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.ERROR));
            String rD = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.DEFAULT));

            //HIS Settings
            String useHISSettings = resource.getPersistentProperty(new QualifiedName("", PreferenceConstants.USE_GLOBAL_SETTINGS));
            hisSettings.setSelection((useHISSettings != null) ? Boolean.parseBoolean(useHISSettings) : USE_HIS_SETTINGS);


            if (filter != null) {
                setListContent(filter, list);
            } else {
                setListContent(FILTER_CLASSPATH, list);
            }

            if (filterS != null) {
                setListContent(filterS, listSource);
            } else {
                setListContent(FILTER_SOURCE, listSource);
            }

            useSourceFilter.setSelection((useFS != null) ? Boolean.parseBoolean(useFS) : USE_SOURCE_FILTER);
            useFilter.setSelection((useF != null) ? Boolean.parseBoolean(useF) : USE_CLASSPTAH_FILTER);
            warning.setSelection((rW != null) ? Boolean.parseBoolean(rW) : DEFAULT_WARNING);
            error.setSelection((rE != null) ? Boolean.parseBoolean(rE) : DEFAULT_ERROR);
            defaultM.setSelection((rD != null) ? Boolean.parseBoolean(rD) : DEFAULT);

            checkContent.setSelection((checkC != null) ? Boolean.parseBoolean(checkC) : CHECK_CONTENT);
            fullBuild.setSelection((fullB != null) ? Boolean.parseBoolean(fullB) : FULL_BUILD);
            incBuild.setSelection((incB != null) ? Boolean.parseBoolean(incB) : INC_BUILD);
            rulesDir.setText((rPath != null) ? rPath : RULES_DIR);

            buttonCheck.setSelection(checkRulesDir(resource));

        } catch (CoreException e) {
            rulesDir.setText(RULES_DIR);
        }


        enablePropertys(!hisSettings.getSelection());
    }



    private void addSourceFilter(Composite parent) {
        Composite f = new Composite(parent, SWT.None);

        GridLayout rf = new GridLayout();
        rf.numColumns = 4;
        f.setLayout(rf);
        listSource = new List(f, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = 3;
        listSource.setToolTipText("Legt fest welche Source-Verzeichnisse in einem Projekt ueberprueft werden sollen");

        listSource.setLayoutData(gridData);
        Composite second = new Composite(f, SWT.None);

        GridLayout rz = new GridLayout();
        rz.numColumns = 3;
        second.setLayout(rz);

        buttonRemoveSource = new Button(f, SWT.PUSH);
        buttonRemoveSource.setText("Remove/");

        buttonAddSource = new Button(f, SWT.PUSH);
        buttonAddSource.setText("Add source folder");

        sourcePath = new Text(f, SWT.SINGLE | SWT.BORDER);
        GridData gdz = new GridData(GridData.FILL_HORIZONTAL);

        gdz.widthHint = convertWidthInCharsToPixels(46);
        sourcePath.setLayoutData(gdz);

        buttonAddSource.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                listSource.add(sourcePath.getText());
                getListContent(list);
            }
        });

        buttonRemoveSource.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (listSource.getSelectionIndex() > -1) {
                    listSource.remove(listSource.getSelectionIndex());
                }
            }
        });
    }



    private void addHisSettings(Composite parent) {
        Composite title = new Composite(parent, SWT.None);

        //		Label headerLabel = new Label(parent, SWT.NONE);
        //		headerLabel.setText("Macker Propertys");
        GridLayout tz = new GridLayout();
        tz.numColumns = 2;

        title.setLayout(tz);
        title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


        hisSettings = new Button(title, SWT.CHECK);
        hisSettings.setText("Use  global settings");
        hisSettings.setToolTipText("load settings via global preferences");


        hisSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                enablePropertys(!hisSettings.getSelection());
            }
        });
    }


    private void addRulesDir(Composite parent) {
        Composite first = new Composite(parent, SWT.None);
        GridLayout r = new GridLayout();
        r.numColumns = 2;
        first.setLayout(r);

        Label ruleLabel = new Label(first, SWT.NONE);
        ruleLabel.setText("Macker Rules Directory:");
        rulesDir = new Text(first, SWT.SINGLE | SWT.BORDER);
        rulesDir.setToolTipText("A path relative to the project's root");

        Label ruleProjectLabel = new Label(first, SWT.NONE);
        ruleProjectLabel.setText("Macker Rules Project:");
        rulesProject = new Text(first, SWT.SINGLE | SWT.BORDER);
        rulesProject.setToolTipText("Project containing macker rules");

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = convertWidthInCharsToPixels(55);
        rulesDir.setLayoutData(gd);

        buttonCheck = new Button(first, SWT.CHECK);
        buttonCheck.setEnabled(false);
        buttonCheck.setSelection(false);

    }


    void enablePropertys (boolean enabled) {
        rulesDir.setEnabled(enabled);
        rulesProject.setEnabled(enabled);
        incBuild.setEnabled(enabled);
        fullBuild.setEnabled(enabled);
        checkContent.setEnabled(enabled);
        radioGroup.setEnabled(enabled);
        useFilter.setEnabled(enabled);
        useSourceFilter.setEnabled(enabled);
        list.setEnabled(enabled);
        listSource.setEnabled(enabled);
        buttonRemoveSource.setEnabled(enabled);
        buttonAddSource.setEnabled(enabled);
        sourcePath.setEnabled(enabled);
        buttonRemoveClass.setEnabled(enabled);
        buttonAddClass.setEnabled(enabled);
        classPath.setEnabled(enabled);
        error.setEnabled(enabled);
        warning.setEnabled(enabled);
        defaultM.setEnabled(enabled);
    }


    private void addCheckboxes(Composite parent) {
        Composite check = new Composite(parent, SWT.None);
        check.setLayout(new GridLayout());
        check.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        useFilter = new Button(check, SWT.CHECK);
        useFilter.setText("Use Classpath Filter");
        useFilter.setToolTipText("Legt fest welche Pakete in einem Projekt ueberprueft werden sollen");
        useSourceFilter = new Button(check, SWT.CHECK);
        useSourceFilter.setText("Use Source Filter");
        useSourceFilter.setToolTipText("Legt fest welche Source-Verzeichnisse in einem Projekt ueberprueft werden sollen");
        incBuild = new Button(check, SWT.CHECK);
        incBuild.setText("Run on Incremental Build");
        incBuild.setToolTipText("Nach jedem Speichervorgang Pruefen");
        fullBuild = new Button(check, SWT.CHECK);
        fullBuild.setText("Run on Full Build");
        fullBuild.setToolTipText("Pruefen bei einem Neuaufbau des Projektes5");
        checkContent = new Button(check, SWT.CHECK);
        checkContent.setText("Check Content");
        checkContent.setToolTipText("Fehleranzeige innerhalb einer Klasse");
        Label events = new Label(parent, SWT.None);
        events.setText("Show Macker Events as");
        radioGroup = new Composite(parent, SWT.NONE);
        GridLayout g = new GridLayout();
        g.numColumns = 3;
        radioGroup.setLayout(g);
        radioGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        warning = createRadioButton(radioGroup, "Warning");
        warning.setToolTipText("Zeige alle Verstoesse als Warnungen an");
        error = createRadioButton(radioGroup, "Error");
        error.setToolTipText("Zeige alle Verstoesse als Kompilierungsfehler an");
        defaultM = createRadioButton(radioGroup, "Default");
        defaultM.setToolTipText("Vorgegebene Einstellung verwenden");
    }

    private void addClasspathFilter(Composite parent) {
        Composite f = new Composite(parent, SWT.None);

        GridLayout rf = new GridLayout();
        rf.numColumns = 4;
        f.setLayout(rf);
        list = new List(f, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = 3;
        list.setToolTipText("Legt fest welche Pakete in einem Projekt ueberprueft werden sollen.");
        list.setLayoutData(gridData);
        Composite second = new Composite(f, SWT.None);

        GridLayout rz = new GridLayout();
        rz.numColumns = 3;
        second.setLayout(rz);

        buttonRemoveClass = new Button(f, SWT.PUSH);
        buttonRemoveClass.setText("Remove/");

        buttonAddClass = new Button(f, SWT.PUSH);
        buttonAddClass.setText("Add classpath");

        classPath = new Text(f, SWT.SINGLE | SWT.BORDER);
        GridData gdz = new GridData(GridData.FILL_HORIZONTAL);

        gdz.widthHint = convertWidthInCharsToPixels(50);
        classPath.setLayoutData(gdz);

        buttonAddClass.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                list.add(classPath.getText());
                getListContent(list);
            }
        });

        buttonRemoveClass.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (list.getSelectionIndex() > -1) {
                    list.remove(list.getSelectionIndex());
                }
            }
        });
    }


    private Button createRadioButton(Composite parent, String label) {
        final Button button = new Button(parent, SWT.RADIO);
        button.setText(label);
        return button;
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        composite.setLayoutData(data);


        addSection(composite);

        return composite;
    }

    /**
     * Default Werte setzen
     */
    @Override
    protected void performDefaults() {
        super.performDefaults();
        //HIS Settings
        hisSettings.setSelection(USE_HIS_SETTINGS);
        rulesDir.setText(RULES_DIR);
        checkContent.setSelection(CHECK_CONTENT);
        fullBuild.setSelection(FULL_BUILD);
        incBuild.setSelection(INC_BUILD);
        list.setItems(new String[] { "de/his/appclient/", "de/his/appserver/", "de/his/core/" });
        listSource.setItems(new String[] { "src/java/", "src/generated", "src/patches" });
        warning.setSelection(DEFAULT_WARNING);
        defaultM.setSelection(DEFAULT);
        error.setSelection(DEFAULT_ERROR);
        useFilter.setSelection(USE_CLASSPTAH_FILTER);
    }

    /**
     * Projekt Einstellungen dauerhaft speichern.
     */
    @Override
    public boolean performOk() {
        // store the values
        try {
            IResource resource = ((IJavaProject) getElement()).getResource();
            //HIS Settings
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.USE_GLOBAL_SETTINGS), Boolean.toString(hisSettings.getSelection()).toString());
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.USE_SOURCE_FILTER), Boolean.toString(useSourceFilter.getSelection()).toString());
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.USE_CLASSPATH_FILTER), Boolean.toString(useFilter.getSelection()).toString());
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.DEFAULT), Boolean.toString(defaultM.getSelection()));
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.ERROR), Boolean.toString(error.getSelection()));
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.WARNING), Boolean.toString(warning.getSelection()));
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.CLASSPATH_FILTER), getListContent(list));
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.SOURCE_FILTER), getListContent(listSource));
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.RULES_PATH), rulesDir.getText());
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.RUN_ON_INCREMENTAL_BUILD), Boolean.toString(incBuild.getSelection()));
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.RUN_ON_FULL_BUILD), Boolean.toString(fullBuild.getSelection()));
            resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.CHECK_CONTENT), Boolean.toString(checkContent.getSelection()));
            //pruefen ob das angegeben (Macker-Rules)Verzeichnis gefunden wurde.
            buttonCheck.setSelection(checkRulesDir(resource));

        } catch (CoreException e) {
            return false;
        }
        return true;
    }

    /**
     * Prueft ob ein angegebener Pfad im Projekt vorhanden ist.
     * @param project das aktuell verwendete JavaProjekt.
     * @return true falls Verzeichnis im Projekt vorhanden.
     */
    private boolean checkRulesDir(IResource project) {
        boolean found = false;
        File dir = new File(project.getLocation().toString() + rulesDir.getText());
        if (dir.exists() && dir.isDirectory()) {
            found = true;
        }
        return found;
    }

	/**
	 * Setzt die Werte des Classpath Filters
	 * @param content
	 */
	private void setListContent(String content, List list) {
		StringTokenizer st = new StringTokenizer(content, "\t");
	     while (st.hasMoreTokens()) {
	       list.add(st.nextToken());
	     }
	}
	
    /**
     * Gibt die aktuellen Werte des Classpath filters als String zurueck.
     * @return Mit tab gejointer String der Einträge in der übergebenen Liste
     */
    String getListContent(List listContent) {
		String content = "";
        for (int i = 0; i < listContent.getItems().length; i++) {
            if (!listContent.getItem(i).equals("")) {
                content += listContent.getItem(i) + "\t";
			}
		}
		return content;
	}
	
    /**
     * Setzt beim ersten Start eines Projektes mit dem Plugin
     * die Defaultwerte.
     * @param resource
     * @return true if successful
     * @throws CoreException 
     */
    public boolean init(IResource resource) throws CoreException {
        //HIS Settings
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.USE_GLOBAL_SETTINGS), Boolean.toString(USE_HIS_SETTINGS));
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.USE_CLASSPATH_FILTER), Boolean.toString(USE_CLASSPTAH_FILTER));
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.USE_SOURCE_FILTER), Boolean.toString(USE_SOURCE_FILTER));
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.CLASSPATH_FILTER), FILTER_CLASSPATH);
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.SOURCE_FILTER), FILTER_SOURCE);
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.DEFAULT), Boolean.toString(DEFAULT));
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.ERROR), Boolean.toString(DEFAULT_ERROR).toString());
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.WARNING), Boolean.toString(DEFAULT_WARNING));
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.RULES_PATH), RULES_DIR);
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.RULES_PROJECT), WEBAPPS);
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.RUN_ON_INCREMENTAL_BUILD), Boolean.toString(INC_BUILD));
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.RUN_ON_FULL_BUILD), Boolean.toString(FULL_BUILD));
        resource.setPersistentProperty(new QualifiedName("", PreferenceConstants.CHECK_CONTENT), Boolean.toString(CHECK_CONTENT));
        return true;
    }

}
