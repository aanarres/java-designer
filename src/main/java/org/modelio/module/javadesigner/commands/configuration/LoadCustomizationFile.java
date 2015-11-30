package org.modelio.module.javadesigner.commands.configuration;

import java.io.File;
import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.IModuleUserConfiguration;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.javadesigner.api.JavaDesignerParameters;
import org.modelio.module.javadesigner.custom.CustomFileException;
import org.modelio.module.javadesigner.custom.JavaTypeManager;
import org.modelio.module.javadesigner.impl.JavaDesignerModule;
import org.modelio.vcore.smkernel.mapi.MObject;

public class LoadCustomizationFile extends DefaultModuleCommandHandler {

    /**
     * This command is available on the model root only.
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        if (!super.accept(selectedElements, module)) {
            return false;
        }
        if (selectedElements.size () == 1) {
        	MObject selectedElement = selectedElements.get (0);
        	return (selectedElement instanceof Package) && ((Package) selectedElement).getOwner() == null;
        }
        return false;
    }

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        // Loading custom file
        IModuleUserConfiguration configuration = module.getConfiguration ();
        String customFilePath = configuration.getParameterValue (JavaDesignerParameters.CUSTOMIZATIONFILE);
        
        // Get the schema file
        File schemaFile = new File (module.getConfiguration ().getModuleResourcesPath () +
                File.separator + "res" + //$NON-NLS-1$
                File.separator + "custom" + //$NON-NLS-1$
                File.separator + "customFile.xsd"); //$NON-NLS-1$
        
        File customFile = new File (customFilePath);
        
        // Try a relative file if the given one doesn't exist
        if (!customFile.exists()) {
            customFile = new File (module.getConfiguration ().getModuleResourcesPath () +
                    File.separator + customFilePath); //$NON-NLS-1$
        }
        
        try {
            JavaTypeManager.getInstance ().loadCustomizationFile (customFile, schemaFile);
        } catch (CustomFileException e) {
            JavaDesignerModule.logService.error("Unable to load configuration file " + customFile);
        }
    }

    @Override
    public boolean isActiveFor(List<MObject> selectedElements, IModule module) {
        if (!super.isActiveFor(selectedElements, module)) {
            return false;
        }
        return true;
    }

}
