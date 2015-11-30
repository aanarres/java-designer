package org.modelio.module.javadesigner.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelio.api.module.IModuleAPIConfiguration;
import org.modelio.api.module.IModuleUserConfiguration;
import org.modelio.metamodel.factory.ElementNotUniqueException;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.statik.Artifact;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.metamodel.uml.statik.Classifier;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.javadesigner.ant.AntExecutor;
import org.modelio.module.javadesigner.ant.AntGenerator;
import org.modelio.module.javadesigner.api.CustomException;
import org.modelio.module.javadesigner.api.IJavaDesignerPeerModule;
import org.modelio.module.javadesigner.api.JavaDesignerStereotypes;
import org.modelio.module.javadesigner.automation.AccessorManager;
import org.modelio.module.javadesigner.dialog.InfoDialogManager;
import org.modelio.module.javadesigner.dialog.JConsoleWithDialog;
import org.modelio.module.javadesigner.editor.EditorManager;
import org.modelio.module.javadesigner.generator.Generator;
import org.modelio.module.javadesigner.javadoc.JavadocManager;
import org.modelio.module.javadesigner.report.ReportManager;
import org.modelio.module.javadesigner.report.ReportModel;
import org.modelio.module.javadesigner.reverse.ReverseMode;
import org.modelio.module.javadesigner.reverse.Reversor;
import org.modelio.module.javadesigner.reverse.ui.ReverseException;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.vbasic.version.Version;
import org.modelio.vcore.smkernel.mapi.MObject;

public class JavaDesignerPeerModule implements IJavaDesignerPeerModule {
    protected JavaDesignerModule module;
    protected IModuleAPIConfiguration peerConfiguration;


    public JavaDesignerPeerModule(JavaDesignerModule module, IModuleAPIConfiguration peerConfiguration) {
        this.module = module;
        this.peerConfiguration = peerConfiguration;
    }

	@Override
	public IModuleAPIConfiguration getConfiguration() {
		return this.peerConfiguration;
	}
	
    @Override
    public String getDescription() {
        return this.module.getDescription ();
    }

    @Override
    public String getName() {
        return this.module.getName ();
    }

    @Override
    public Version getVersion() {
        return this.module.getVersion ();
    }

    @Override
    public void generate(NameSpace element, boolean withGUI) {
        HashSet<NameSpace> elements = new HashSet<> ();
        elements.add (element);
        this.generate (elements, withGUI);
    }

    @Override
    public void generate(Collection<NameSpace> elements, boolean withGUI) {
        try {
            JavaDesignerUtils.initCurrentGenRoot (elements);
        } catch (InterruptedException e) {
            return;
        }
        
        ReportModel report = ReportManager.getNewReport ();
        
        Generator generator = new Generator (JavaDesignerUtils.getAllComponentsToTreat (elements, this.module), this.module);
        generator.generate (report);
        
        if (withGUI) {
            ReportManager.showGenerationReport (report);
        } else {
            ReportManager.printGenerationReport (report);
        }
        
        JavaDesignerUtils.setProjectGenRoot (null);
    }

    @Override
    public void generateAntFile(Artifact artifact, boolean withGUI) {
        AntGenerator antGenerator;
        if (withGUI) {
            antGenerator = new AntGenerator(this.module, new JConsoleWithDialog (InfoDialogManager.getExecuteAntTargetDialog ()));
        } else {
            antGenerator = new AntGenerator(this.module);
        }
        
        antGenerator.generateBuildXmlFile (artifact);
    }

    @Override
    public String executeTarget(Artifact artifact, String target, boolean withGUI) {
        AntExecutor antGenerator;
        if (withGUI) {
            antGenerator = new AntExecutor(this.module, new JConsoleWithDialog (InfoDialogManager.getExecuteAntTargetDialog ()));
        } else {
            antGenerator = new AntExecutor(this.module);
        }
        antGenerator.executeTarget(artifact, target);
        
        return "";
    }

    @Override
    public void updateModel(Collection<NameSpace> elements, boolean withGUI) {
        try {
            JavaDesignerUtils.initCurrentGenRoot (elements);
        } catch (InterruptedException e) {
            return;
        }
        
        ReportModel report = ReportManager.getNewReport ();
        
        Reversor reversor = new Reversor (this.module, report);
        Set<NameSpace> elementsToUpdate = new HashSet<> ();
        
        for (NameSpace element : elements) {
            NameSpace producingParent = JavaDesignerUtils.getNearestNameSpace (element);
            if (producingParent != null) {
                elementsToUpdate.add (producingParent);
            }
        }
        
        try {
            reversor.update (JavaDesignerUtils.getAllComponentsToTreat (elementsToUpdate, this.module), ReverseMode.Retrieve, EditorManager.getInstance ());
        } catch (ReverseException e) {
            // The Reverse was canceled
        }
        
        if (withGUI) {
            ReportManager.showGenerationReport (report);
        } else {
            ReportManager.printGenerationReport (report);
        }
        
        JavaDesignerUtils.setProjectGenRoot (null);
    }

    @Override
    public File getFilename(NameSpace element) {
        return JavaDesignerUtils.getFilename (element, this.module);
    }

    @Override
    public String executeTarget(Artifact artifact, String target) {
        return executeTarget (artifact, target, true);
    }

    @Override
    public void generate(NameSpace element) {
        generate (element, true);
    }

    @Override
    public void generate(Collection<NameSpace> elements) {
        generate (elements, true);
    }

    @Override
    public void generateAntFile(Artifact artifact) {
        generateAntFile (artifact, true);
    }

    @Override
    public void updateModel(Collection<NameSpace> elements) {
        updateModel (elements, true);
    }

    @Override
    public boolean deleteAccessors(Classifier theClassifier) {
        IModuleUserConfiguration javaConfig = this.module.getConfiguration ();
        
        AccessorManager accessorManager = new AccessorManager (this.module.getModelingSession());
        accessorManager.init (javaConfig);
        accessorManager.deleteAccessors(theClassifier);
        return true;
    }

    @Override
    public boolean updateAccessors(Attribute theAttribute, boolean createNewAccessors) throws CustomException, ExtensionNotFoundException, ElementNotUniqueException {
        if (!theAttribute.isStereotyped(IJavaDesignerPeerModule.MODULE_NAME, JavaDesignerStereotypes.JAVAATTRIBUTEPROPERTY)) {
            IModuleUserConfiguration javaConfig = this.module.getConfiguration ();
        
            AccessorManager accessorManager = new AccessorManager (this.module.getModelingSession());
            accessorManager.init (javaConfig);
            accessorManager.updateAccessors (theAttribute, createNewAccessors);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateAccessors(AssociationEnd theAssociationEnd, boolean createNewAccessors) throws CustomException, ExtensionNotFoundException, ElementNotUniqueException {
        if (!theAssociationEnd.isStereotyped(IJavaDesignerPeerModule.MODULE_NAME, JavaDesignerStereotypes.JAVAASSOCIATIONENDPROPERTY)) {
            IModuleUserConfiguration javaConfig = this.module.getConfiguration ();
        
            AccessorManager accessorManager = new AccessorManager (this.module.getModelingSession());
            accessorManager.init (javaConfig);
            accessorManager.updateAccessors (theAssociationEnd, createNewAccessors);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void generateJavaDoc(Package element, boolean withGUI) {
        JavadocManager javadocManager;
        if (withGUI) {
            javadocManager = new JavadocManager(this.module, new JConsoleWithDialog (InfoDialogManager.getJavaDocDialog ()));
        } else {
            javadocManager = new JavadocManager(this.module);
        }

        List<MObject> selectedElements = new ArrayList<>();
        selectedElements.add(element);
        javadocManager.generateDoc(selectedElements);
    }
}