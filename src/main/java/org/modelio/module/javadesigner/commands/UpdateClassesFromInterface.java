package org.modelio.module.javadesigner.commands;

import java.util.List;

import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.ITransaction;
import org.modelio.api.model.InvalidTransactionException;
import org.modelio.api.module.IModule;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.metamodel.uml.statik.Interface;
import org.modelio.module.javadesigner.automation.InterfaceImplementer;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.vcore.smkernel.mapi.MObject;

public class UpdateClassesFromInterface extends DefaultModuleCommandHandler {

    /**
     * This methods authorizes a command to be displayed in a defined context. The commands are displayed, by default,
     * depending on the kind of metaclass on which the command has been launched.
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        if (!super.accept(selectedElements, module)) {
            return false;
        }
        for (MObject theElement : selectedElements) {
            if (!JavaDesignerUtils.isJavaElement (theElement)) {
                return false;
            }
            Interface theInterface = (Interface) theElement;
            if (theInterface.getImplementedLink ().size () == 0) {
                return false;
            }
        }
        return (selectedElements.size () != 0);
    }

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        InterfaceImplementer interfaceManager = new InterfaceImplementer (module.getModelingSession());
        IModelingSession session = module.getModelingSession ();
        try (ITransaction transaction = session.createTransaction ("Update classes from interface")) {
            boolean hasDoneWork = false;
            for (MObject theElement : selectedElements) {
                Interface theInterface = (Interface) theElement;

                boolean newResult = interfaceManager.updateImplementingClassifiers (theInterface);
                hasDoneWork = hasDoneWork || newResult;
            }

            if (hasDoneWork) {
                transaction.commit();
            } else {
                transaction.rollback();
            }
        } catch (InvalidTransactionException e) {
            // Transaction rollbacked by audit. 
        }
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
        return true;
    }

}
