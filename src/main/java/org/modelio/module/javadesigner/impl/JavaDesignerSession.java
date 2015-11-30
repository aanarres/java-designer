package org.modelio.module.javadesigner.impl;

import java.io.File;
import java.util.Map;

import org.modelio.api.model.change.IModelChangeHandler;
import org.modelio.api.model.change.IModelChangeListener;
import org.modelio.api.model.change.IStatusChangeHandler;
import org.modelio.api.module.DefaultModuleSession;
import org.modelio.api.module.IModuleUserConfiguration;
import org.modelio.api.module.ModuleException;
import org.modelio.module.javadesigner.api.ISessionWithHandler;
import org.modelio.module.javadesigner.api.JavaDesignerParameters;
import org.modelio.module.javadesigner.custom.CustomFileException;
import org.modelio.module.javadesigner.custom.JavaTypeManager;
import org.modelio.module.javadesigner.editor.EditorManager;
import org.modelio.module.javadesigner.reverse.newwizard.ImageManager;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.vbasic.version.Version;

public class JavaDesignerSession extends DefaultModuleSession implements ISessionWithHandler {
    protected IModelChangeHandler modelChangeHandler = null;

    protected IStatusChangeHandler statusChangeHandler = null;

    protected IModelChangeListener modelChangeListener = null;

    public JavaDesignerSession(JavaDesignerModule module) {
        super (module);
    }

    public static boolean install(String objingPath, String mdaplugsPath) throws ModuleException {
        return DefaultModuleSession.install (objingPath, mdaplugsPath);
    }

    @Override
    public boolean select() throws ModuleException {
        String jdkPath = JavaDesignerUtils.getJDKPath ();
        String customFile = "res" + //$NON-NLS-1$
                File.separator + "custom" + //$NON-NLS-1$
                File.separator + "javaCustomizationFile.xml"; //$NON-NLS-1$

        IModuleUserConfiguration configuration = this.module.getConfiguration ();
        configuration.setParameterValue (JavaDesignerParameters.ACCESSIBLECLASSES, jdkPath +
                File.separator +
                "jre" + File.separator + "lib" + File.separator + "rt.jar"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        configuration.setParameterValue (JavaDesignerParameters.ACCESSORSGENERATION, JavaDesignerParameters.AccessorsGenerationMode.Smart.toString ());
        configuration.setParameterValue (JavaDesignerParameters.AUTOGENERATE, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.AUTOMATICALLYOPENJAVADOC, "true"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.AUTOREVERSE, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.COMPILATIONOPTIONS, ""); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.COMPILATIONPATH, "$(Project)/bin"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.CUSTOMIZATIONFILE, customFile);
        configuration.setParameterValue (JavaDesignerParameters.DESCRIPTIONASJAVADOC, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.DIAGRAMCREATIONONREVERSE, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.ECLIPSEPROJECT, ""); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.ERRORONFIRSTWARNING, "true"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.GENERATEFINALPARAMETERS, "true"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.GENERATEINVARIANTS, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.GENERATEPREPOSTCONDITIONS, "false"); //$NON-NLS-1$
        if (System.getProperty ("os.name").startsWith ("Windows")) {
            configuration.setParameterValue (JavaDesignerParameters.EXTERNALEDITORCOMMANDLINE, "notepad.exe"); //$NON-NLS-1$
        }
        configuration.setParameterValue (JavaDesignerParameters.FRIENDLYACCESSORVISIBILITY, "Public"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.FRIENDLYMODIFIERVISIBILITY, "Public"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.FULLNAMEGENERATION, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.GENDOCPATH, "$(Project)/doc"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.GENERATEJAVADOC, "true"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.GENERATIONMODE, JavaDesignerParameters.GenerationMode.RoundTrip.toString ());
        configuration.setParameterValue (JavaDesignerParameters.GENERATIONPATH, "$(Project)/src"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.INTERFACEIMPLEMENTATION, JavaDesignerParameters.InterfaceImplementationMode.Ask.toString ());
        configuration.setParameterValue (JavaDesignerParameters.INVARIANTSNAME, "invariant"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.JAVACOMPATIBILITY, JavaDesignerParameters.CompatibilityLevel.Java8.toString ());
        configuration.setParameterValue (JavaDesignerParameters.JARFILEPATH, "$(Project)/bin"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.JAVADOCOPTIONS, "-private"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.JAVAHGENERATIONPATH, "$(Project)/src"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.JDKPATH, jdkPath);
        configuration.setParameterValue (JavaDesignerParameters.LOCKGENERATEDFILES, "true"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.PACKAGEJARINRAMC, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.PACKAGESRCINRAMC, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.PRIVATEACCESSORVISIBILITY, JavaDesignerParameters.AccessorVisibility.Public.toString ());
        configuration.setParameterValue (JavaDesignerParameters.PRIVATEMODIFIERVISIBILITY, JavaDesignerParameters.AccessorVisibility.Public.toString ());
        configuration.setParameterValue (JavaDesignerParameters.PROTECTEDACCESSORVISIBILITY, JavaDesignerParameters.AccessorVisibility.Public.toString ());
        configuration.setParameterValue (JavaDesignerParameters.PROTECTEDMODIFIERVISIBILITY, JavaDesignerParameters.AccessorVisibility.Public.toString ());
        configuration.setParameterValue (JavaDesignerParameters.PUBLICACCESSORVISIBILITY, "Public"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.PUBLICMODIFIERVISIBILITY, "Public"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.READONLYELEMENTNOTGENERATED, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.RETRIEVEDEFAULTBEHAVIOUR, JavaDesignerParameters.RetrieveMode.Ask.toString ());
        configuration.setParameterValue (JavaDesignerParameters.RUNPARAMETERS, ""); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.SOURCEFILESPATH, ""); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.USEEXTERNALEDITION, "false"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.USEJAVAH, "true"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.ENCODING, "UTF-8"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.GENERATEDEFAULTRETURN, "true"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.GENERATEJAVADOC_MARKERS, "true"); //$NON-NLS-1$
        configuration.setParameterValue (JavaDesignerParameters.COMPONENTSUBPATH, "src"); //$NON-NLS-1$
        return super.select ();
    }

    @Override
    public boolean start() throws ModuleException {
        // Remove the metamodelVersion
        Version version = this.module.getVersion ();
        String completeVersion = version.toString ();

        // Display the copyright
        JavaDesignerModule.logService.info ("Modelio/" + this.module.getName () + " " + completeVersion + " - Copyright 2008-2015 Modeliosoft"); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$

        /*
         * Notifications
         */
        this.statusChangeHandler = new StatusChangeHandler (this.module);
        this.module.getModelingSession ().addStatusHandler (this.statusChangeHandler);

        this.modelChangeHandler = new ModelChangeHandler (this.module);
        this.module.getModelingSession ().addModelHandler (this.modelChangeHandler);

        // TODO add file manager to handle deleted/moved files
        // this.modelChangeListener = new ModelChangeListener (new JavaFileManager((Module) this.module));
        this.modelChangeListener = new ModelChangeListener (null);
        this.module.getModelingSession ().addModelListener (this.modelChangeListener);

        // Set the main module parameters if they are empty
        IModuleUserConfiguration configuration = this.module.getConfiguration ();

        String jdkPath = null;

        if (configuration.getParameterValue (JavaDesignerParameters.ACCESSIBLECLASSES).isEmpty ()) {
            // find the JDK path
            jdkPath = JavaDesignerUtils.getJDKPath ();
            configuration.setParameterValue (JavaDesignerParameters.ACCESSIBLECLASSES, jdkPath +
                    File.separator +
                    "jre" + File.separator + "lib" + File.separator + "rt.jar"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        if (configuration.getParameterValue (JavaDesignerParameters.ACCESSORSGENERATION).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.ACCESSORSGENERATION, JavaDesignerParameters.AccessorsGenerationMode.Smart.toString ());
        }

        if (configuration.getParameterValue (JavaDesignerParameters.COMPILATIONPATH).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.COMPILATIONPATH, "$(Project)/bin"); //$NON-NLS-1$
        }

        if (configuration.getParameterValue (JavaDesignerParameters.JAVACOMPATIBILITY).isEmpty()) {
            configuration.setParameterValue (JavaDesignerParameters.JAVACOMPATIBILITY, JavaDesignerParameters.CompatibilityLevel.Java7.toString ());
        }

        if (configuration.getParameterValue (JavaDesignerParameters.GENDOCPATH).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.GENDOCPATH, "$(Project)/doc"); //$NON-NLS-1$
        }

        if (configuration.getParameterValue (JavaDesignerParameters.GENERATIONMODE).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.GENERATIONMODE, JavaDesignerParameters.GenerationMode.RoundTrip.toString ());
        }

        if (configuration.getParameterValue (JavaDesignerParameters.GENERATIONPATH).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.GENERATIONPATH, "$(Project)/src"); //$NON-NLS-1$
        }

        if (configuration.getParameterValue (JavaDesignerParameters.INTERFACEIMPLEMENTATION).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.INTERFACEIMPLEMENTATION, JavaDesignerParameters.InterfaceImplementationMode.Ask.toString ());
        }

        if (configuration.getParameterValue (JavaDesignerParameters.JARFILEPATH).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.JARFILEPATH, "$(Project)/bin"); //$NON-NLS-1$
        }

        if (configuration.getParameterValue (JavaDesignerParameters.JAVAHGENERATIONPATH).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.JAVAHGENERATIONPATH, "$(Project)/src"); //$NON-NLS-1$
        }

        if (configuration.getParameterValue (JavaDesignerParameters.JDKPATH).isEmpty ()) {
            // find the JDK path if necessary
            if (jdkPath == null) {
                jdkPath = JavaDesignerUtils.getJDKPath ();
            }

            configuration.setParameterValue (JavaDesignerParameters.JDKPATH, jdkPath);
        }

        if (configuration.getParameterValue (JavaDesignerParameters.RETRIEVEDEFAULTBEHAVIOUR).isEmpty ()) {
            configuration.setParameterValue (JavaDesignerParameters.RETRIEVEDEFAULTBEHAVIOUR, JavaDesignerParameters.RetrieveMode.Ask.toString ());
        }

        String customFilePath = configuration.getParameterValue (JavaDesignerParameters.CUSTOMIZATIONFILE);
        // Set a default value if the parameter is empty
        if (customFilePath.isEmpty ()) {
            customFilePath = "res" + //$NON-NLS-1$
                    File.separator + "custom" + //$NON-NLS-1$
                    File.separator + "javaCustomizationFile.xml"; //$NON-NLS-1$
            configuration.setParameterValue (JavaDesignerParameters.CUSTOMIZATIONFILE, customFilePath);
        }

        String encoding = configuration.getParameterValue (JavaDesignerParameters.ENCODING); //$NON-NLS-1$
        if (encoding == null || encoding.equals ("") || encoding.contains ("_")) {
            configuration.setParameterValue (JavaDesignerParameters.ENCODING, "UTF-8"); //$NON-NLS-1$
        }

        // Load the customization file
        File customFile = new File (customFilePath);

        if (!customFile.exists()) {
            customFile = new File (this.module.getConfiguration ().getModuleResourcesPath () + File.separator + customFilePath);
        }

        // Get the schema file
        File schemaFile = new File (this.module.getConfiguration ().getModuleResourcesPath () +
                File.separator + "res" + //$NON-NLS-1$
                File.separator + "custom" + //$NON-NLS-1$
                File.separator + "customFile.xsd"); //$NON-NLS-1$
        try {
            JavaTypeManager.getInstance ().loadCustomizationFile (customFile, schemaFile);
        } catch (CustomFileException e) {
            JavaDesignerModule.logService.error(e);
        }

        // Init image cache
        String modulePath = this.module.getConfiguration().getModuleResourcesPath().toAbsolutePath().toString();
        ImageManager.setModulePath(modulePath);

        return super.start ();
    }

    @Override
    public void stop() throws ModuleException {
        this.module.getModelingSession ().removeStatusHandler (this.statusChangeHandler);
        this.module.getModelingSession ().removeModelHandler (this.modelChangeHandler);
        this.module.getModelingSession ().removeModelListener (this.modelChangeListener);
        EditorManager.getInstance ().closeEditors ();

        super.stop ();
    }

    @Override
    public void unselect() throws ModuleException {
        super.unselect ();
    }

    @Override
    public void upgrade(Version oldVersion, Map<String, String> oldParameters) throws ModuleException {
        try {
                super.upgrade (oldVersion, oldParameters);
        } catch (Exception e) {
            JavaDesignerModule.logService.error(e);
        }
    }

    @Override
    public IModelChangeHandler getModelChangeHandler() {
        return this.modelChangeHandler;
    }

}
