package org.modelio.module.javadesigner.commands.ant;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Artifact;
import org.modelio.module.javadesigner.ant.AntExecutor;
import org.modelio.module.javadesigner.api.IJavaDesignerPeerModule;
import org.modelio.module.javadesigner.api.JavaDesignerStereotypes;
import org.modelio.module.javadesigner.dialog.InfoDialogManager;
import org.modelio.module.javadesigner.dialog.JConsoleWithDialog;
import org.modelio.vcore.smkernel.mapi.MObject;

public class ExecuteAntTarget extends DefaultModuleCommandHandler {

    /**
     * This methods authorizes a command to be displayed in a defined context. The commands are displayed, by default,
     * depending on the kind of metaclass on which the command has been launched.
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        if (!super.accept(selectedElements, module)) {
            return false;
        }
        boolean result = (selectedElements.size () != 0);
        
        for (MObject element : selectedElements) {
            if (element instanceof ModelElement) {
                ModelElement modelelement = (ModelElement) element;
                if (!modelelement.isStereotyped(IJavaDesignerPeerModule.MODULE_NAME, JavaDesignerStereotypes.JARFILE)) {
                    result = false;
                }
            } else {
                result = false;
            }
        }
        return result;
    }

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        JConsoleWithDialog console = new JConsoleWithDialog (InfoDialogManager.getExecuteAntTargetDialog ());
        
        AntExecutor antGenerator = new AntExecutor(module, console);
        for (MObject element : selectedElements) {
            antGenerator.executeTarget((Artifact) element);
        }
    }

    /**
     * This method precizes if a command has to be desactivated. If the command has to be displayed (which means that
     * the accept method has returned a positive value, it is sometimes needed to desactivate the command depending on
     * specific constraints that are specific to the module.
     */
    @Override
    public boolean isActiveFor(List<MObject> selectedElements, IModule module) {
        if (!super.isActiveFor(selectedElements, module)) {
            return false;
        }
        return true;
    }

}
