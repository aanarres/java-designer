package org.modelio.module.javadesigner.commands.creation;

import java.util.List;

import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.ITransaction;
import org.modelio.api.model.IUmlModel;
import org.modelio.api.model.InvalidTransactionException;
import org.modelio.api.module.IModule;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.metamodel.uml.statik.Component;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.module.javadesigner.api.JavaDesignerStereotypes;
import org.modelio.module.javadesigner.i18n.Messages;
import org.modelio.module.javadesigner.impl.JavaDesignerModule;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.module.javadesigner.utils.ModelUtils;
import org.modelio.vcore.smkernel.mapi.MObject;

public class CreateAttributeProperty extends DefaultModuleCommandHandler {

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        IModelingSession session = module.getModelingSession ();
        try (ITransaction transaction = session.createTransaction("CreateAttributeProperty")) {
        	IUmlModel model = session.getModel ();
            
            for (MObject element : selectedElements) {
                if (element instanceof GeneralClass) {
                    GeneralClass currentClass = (GeneralClass) element;
        
                    Attribute att = model.createAttribute("", session.getModel().getUmlTypes().getSTRING(), currentClass, null);
                    ModelUtils.addStereotype(att, JavaDesignerStereotypes.JAVAATTRIBUTEPROPERTY);
                    model.getDefaultNameService().setDefaultName(att, "JavaProperty");
                }
            }
        
            transaction.commit();
        } catch (InvalidTransactionException e) {
            JavaDesignerModule.logService.error(e);
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(Messages.getString ("Error.StereotypeNotFound", JavaDesignerStereotypes.JAVACOMPONENT)); //$NON-NLS-1$
        } catch (Exception e) {
            JavaDesignerModule.logService.error(e);
        }
    }

    /**
     * This methods authorizes a command to be displayed in a defined context.
     * The commands are displayed, by default, depending on the kind of metaclass on which the command has been launched.
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        if (!super.accept(selectedElements, module)) {
            return false;
        }
        for (MObject element : selectedElements) {
            if (!JavaDesignerUtils.isJavaElement (element) || (element instanceof Component)) {
                return false;
            }
        }
        return (selectedElements.size () != 0);
    }

    /**
     * This method specifies whether or not a command must be deactivated.
     * If the command has to be displayed (which means that the accept method has returned a positive value, it is sometimes needed to desactivate the command depending on specific constraints that are specific to the module.
     */
    @Override
    public boolean isActiveFor(List<MObject> selectedElements, IModule module) {
        if (!super.isActiveFor(selectedElements, module)) {
            return false;
        }
        boolean result = true;
        
        // Not available on libraries
        for (MObject element : selectedElements) {
            if (ModelUtils.isLibrary(element)) {
                result = false;
                break;
            }
        }
        return result;
    }

}
