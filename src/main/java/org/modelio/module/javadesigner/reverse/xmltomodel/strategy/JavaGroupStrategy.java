package org.modelio.module.javadesigner.reverse.xmltomodel.strategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.modelio.api.model.IModelingSession;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.statik.ElementImport;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.javadesigner.api.IJavaDesignerPeerModule;
import org.modelio.module.javadesigner.api.JavaDesignerStereotypes;
import org.modelio.module.javadesigner.i18n.Messages;
import org.modelio.module.javadesigner.impl.JavaDesignerModule;
import org.modelio.module.javadesigner.progress.ProgressBar;
import org.modelio.module.javadesigner.utils.ModelUtils;
import org.modelio.vcore.smkernel.mapi.MObject;

import com.modelio.module.xmlreverse.IReadOnlyRepository;
import com.modelio.module.xmlreverse.model.IVisitorElement;
import com.modelio.module.xmlreverse.model.JaxbGroup;
import com.modelio.module.xmlreverse.strategy.GroupStrategy;

public class JavaGroupStrategy extends GroupStrategy {

    public JavaGroupStrategy(IModelingSession session) {
        super (session);
    }

    @Override
    public MObject getCorrespondingElement(JaxbGroup jaxb_element, MObject owner, IReadOnlyRepository repository) {
        File f = new File (jaxb_element.getName ());
        boolean mustContinue = ProgressBar.updateProgressBar (Messages.getString ("Gui.Reverse.getCorrespondingElement", f.getName ()));
        if (!mustContinue) {
            throw new RuntimeException ();
        }
        return super.getCorrespondingElement (jaxb_element, owner, repository);
    }

    @Override
    public List<MObject> updateProperties(JaxbGroup jaxb_element, MObject modelio_element, MObject owner, IReadOnlyRepository repository) {
        File f = new File (jaxb_element.getName ());
        boolean mustContinue = ProgressBar.updateProgressBar (Messages.getString ("Gui.Reverse.updateProperties", f.getName ()));
        if (!mustContinue) {
            throw new RuntimeException ();
        }
        return super.updateProperties (jaxb_element, modelio_element, owner, repository);
    }

    @Override
    public void deleteSubElements(JaxbGroup jaxb_element, MObject modelio_element, Collection<MObject> element_todelete, IReadOnlyRepository repository) {
        File f = new File (jaxb_element.getName ());
        boolean mustContinue = ProgressBar.updateProgressBar (Messages.getString ("Gui.Reverse.deleteSubElements", f.getName ()));
        if (!mustContinue) {
            throw new RuntimeException ();
        }
        
        super.deleteSubElements (jaxb_element, modelio_element, element_todelete, repository);
    }

    @Override
    public void postTreatment(JaxbGroup jaxb_element, MObject modelio_element, IReadOnlyRepository repository) {
        File f = new File (jaxb_element.getName ());
        boolean mustContinue = ProgressBar.updateProgressBar (Messages.getString ("Gui.Reverse.postTreatment", f.getName ()));
        if (!mustContinue) {
            throw new RuntimeException ();
        }
        
        super.postTreatment (jaxb_element, modelio_element, repository);
        
        try {
            NameSpace targetElement = null;
            List<NameSpace> linkedNamespaces = new ArrayList<> ();
        
            for (Object sub : jaxb_element.getPackageOrClazzOrInterface ()) {
                if (sub instanceof IVisitorElement) {
                    MObject sub_modelio_element = repository.getElement ((IVisitorElement) sub);
        
                    if (targetElement == null) {
                        targetElement = (NameSpace) sub_modelio_element;
        
                        for (ElementImport theImport : targetElement.getOwnedImport ()) {
                            NameSpace importingElement = theImport.getImportedElement ();
                            if (importingElement.isStereotyped(IJavaDesignerPeerModule.MODULE_NAME, JavaDesignerStereotypes.JAVAFILEGROUP)) {
                                linkedNamespaces.add (importingElement);
                            }
                        }
                    } else {
                        if (!linkedNamespaces.contains (sub_modelio_element)) {
                            ElementImport newImport = this.model.createElementImport (targetElement, (NameSpace) sub_modelio_element);
                            ModelUtils.addStereotype (newImport, JavaDesignerStereotypes.JAVAFILEGROUP);
                        } else {
                            linkedNamespaces.add ((NameSpace) sub_modelio_element);
                        }
                    }
                }
            }
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(e.getMessage ());
        }
    }

}
