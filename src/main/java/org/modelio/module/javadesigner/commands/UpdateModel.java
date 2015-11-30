package org.modelio.module.javadesigner.commands;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelio.api.module.IModule;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.javadesigner.api.JavaDesignerParameters;
import org.modelio.module.javadesigner.editor.EditorManager;
import org.modelio.module.javadesigner.report.ReportManager;
import org.modelio.module.javadesigner.report.ReportModel;
import org.modelio.module.javadesigner.reverse.ReverseMode;
import org.modelio.module.javadesigner.reverse.Reversor;
import org.modelio.module.javadesigner.reverse.ui.ReverseException;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.vcore.smkernel.mapi.MObject;

public class UpdateModel extends DefaultModuleCommandHandler {
    private Set<NameSpace> elementsToUpdate = new HashSet<> ();


    /**
     * This methods authorizes a command to be displayed in a defined context. The commands are displayed, by default,
     * depending on the kind of metaclass on which the command has been launched.
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        if (!super.accept(selectedElements, module)) {
            return false;
        }
        this.elementsToUpdate.clear ();
        
        for (MObject element : selectedElements) {
            NameSpace producingParent = JavaDesignerUtils.getNearestNameSpace (element);
            if (producingParent == null) {
                return false;
            } else {
                this.elementsToUpdate.add (producingParent);
            }
        }
        return (selectedElements.size () != 0);
    }

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        try {
            JavaDesignerUtils.initCurrentGenRoot (this.elementsToUpdate);
        } catch (InterruptedException e) {
            return;
        }
        
        ReportModel report = ReportManager.getNewReport ();
        
        Reversor reversor = new Reversor (module, report);
        // No confirm box
        // Date file are checked. Elements are reverse only if the file is more
        // recent
        try {
        	Collection<NameSpace> baseElements = JavaDesignerUtils.getAllComponentsToTreat (this.elementsToUpdate, module);
            reversor.update (JavaDesignerUtils.getAllComponentsToTreat (baseElements, module), ReverseMode.Retrieve, EditorManager.getInstance ());
        } catch (ReverseException e) {
            // The Reverse was canceled
        }
        
        ReportManager.showGenerationReport (report);
        
        JavaDesignerUtils.setProjectGenRoot (null);
    }

    /**
     * This method precizes if a command has to be desactivated. If the command has to be displayed (which means that
     * the accept method has returned a positive value, it is sometimes needed to desactivate the command depending on
     * specific constraints that are specific to the MDAC.
     */
    @Override
    public boolean isActiveFor(List<MObject> selectedElements, IModule module) {
        if (!super.isActiveFor(selectedElements, module)) {
            return false;
        }
        // Release mode deactivates model update...
        String GENERATIONMODE = module.getConfiguration().getParameterValue(JavaDesignerParameters.GENERATIONMODE);
        if (GENERATIONMODE.equals (JavaDesignerParameters.GenerationMode.Release.toString ())) {
            return false;
        }
        
        // Use a temp storage to avoid concurrent modifications
        Set<NameSpace> tempList = new HashSet<> (this.elementsToUpdate);
        
        for (NameSpace element : tempList) {
            NameSpace producingParent = JavaDesignerUtils.getProducingNameSpace (element);
            if (producingParent != null) {
                // Update the Set content
                this.elementsToUpdate.remove (element);
                this.elementsToUpdate.add (producingParent);
            } else {
                return false;
            }
        }
        return true;
    }

}
