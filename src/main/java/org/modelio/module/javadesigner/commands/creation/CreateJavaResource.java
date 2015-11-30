package org.modelio.module.javadesigner.commands.creation;


import java.util.List;

import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.ITransaction;
import org.modelio.api.model.IUmlModel;
import org.modelio.api.model.InvalidTransactionException;
import org.modelio.api.module.IModule;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.statik.Artifact;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.javadesigner.api.JavaDesignerStereotypes;
import org.modelio.module.javadesigner.i18n.Messages;
import org.modelio.module.javadesigner.impl.JavaDesignerModule;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.module.javadesigner.utils.ModelUtils;
import org.modelio.vcore.smkernel.mapi.MObject;

public class CreateJavaResource extends DefaultModuleCommandHandler {

    /**
     * This methods authorizes a command to be displayed in a defined context. The commands are displayed, by default,
     * depending on the kind of metaclass on which the command has been launched.
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        if (!super.accept(selectedElements, module)) {
            return false;
        }
        for (MObject element : selectedElements) {
            if (!JavaDesignerUtils.isJavaElement (element)) {
                return false;
            }
        }
        return (selectedElements.size () != 0);
    }

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        IModelingSession session = module.getModelingSession ();
        try (ITransaction transaction = session.createTransaction (Messages.getString ("Info.Session.CreateJavaResource"))) {
            IUmlModel model = session.getModel ();
            boolean result = true;

            for (MObject element : selectedElements) {
                if (element instanceof Package) {
                    Package currentPackage = (Package) element;
                    try {
                        Artifact createdElement = model.createArtifact ("", currentPackage, null);
                        ModelUtils.addStereotype(createdElement, JavaDesignerStereotypes.JAVARESOURCE);

                        model.getDefaultNameService().setDefaultName(createdElement, "ResourceFile");
                    } catch (ExtensionNotFoundException e) {
                        JavaDesignerModule.logService.error(Messages.getString ("Error.StereotypeNotFound", JavaDesignerStereotypes.JAVARESOURCE)); //$NON-NLS-1$
                        result = false;
                    }
                }
            }

            // An error has occured: commit or rollback of the transaction
            if (result) {
                transaction.commit();
            } else {
                transaction.rollback();
            }
        } catch (InvalidTransactionException e) {
            // Transaction rollbacked by audit. 
        }
    }

    /**
     * The command is always active
     */
    @Override
    public boolean isActiveFor(List<MObject> selectedElements, IModule module) {
        if (!super.isActiveFor(selectedElements, module)) {
            return false;
        }
        boolean result = true;

        // Not available on model components
        for (MObject element : selectedElements) {
            if (ModelUtils.isLibrary(element)) {
                result = false;
                break;
            }
        }
        return result;
    }

}
