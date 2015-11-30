package org.modelio.module.javadesigner.reverse.xmltomodel.strategy;

import java.util.List;

import org.modelio.api.model.IModelingSession;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Note;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.module.javadesigner.reverse.ReverseStrategyConfiguration;
import org.modelio.module.javadesigner.reverse.xmltomodel.JavaModelElementDeleteStrategy;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.vcore.smkernel.mapi.MObject;

import com.modelio.module.xmlreverse.IReadOnlyRepository;
import com.modelio.module.xmlreverse.model.JaxbStereotype;
import com.modelio.module.xmlreverse.strategy.StereotypeStrategy;

public class JavaStereotypeStrategy extends StereotypeStrategy {
    public ReverseStrategyConfiguration reverseConfig;

    private JavaModelElementDeleteStrategy deleteStrategy;

    public JavaStereotypeStrategy(IModelingSession session, ReverseStrategyConfiguration reverseConfig, JavaModelElementDeleteStrategy deleteStrategy) {
        super (session);
        this.reverseConfig = reverseConfig;
        this.deleteStrategy = deleteStrategy;
    }

    @Override
    public Stereotype getCorrespondingElement(JaxbStereotype jaxb_element, MObject owner, IReadOnlyRepository repository) {
        if (owner instanceof Note) {
            ModelElement treeOwner = (ModelElement) owner.getCompositionOwner();

            for(Stereotype stereotype : treeOwner.getExtension()){
                if(stereotype.getName().equals(jaxb_element.getStereotypeType())){
                    this.deleteStrategy.putJavaStereotypeUsage(treeOwner, stereotype);
                    return stereotype;
                }
            }

            String type = jaxb_element.getStereotypeType();
            for (Stereotype stereotype :this.session.getMetamodelExtensions().findStereotypes(type, owner.getMClass())) {
                if (JavaDesignerUtils.isAnnotationStereotype(stereotype)) {
                    this.deleteStrategy.putJavaStereotypeUsage(treeOwner, stereotype);
                    return stereotype;
                }
            }
            // Not found, do not produce an error in the report as the annotation will be kept as an Annotation
            return null;
        } else {
            final Stereotype stereotype = super.getCorrespondingElement(jaxb_element, owner, repository);
            this.deleteStrategy.putJavaStereotypeUsage((ModelElement) owner, stereotype);
            return stereotype;
        }
    }

    @Override
    public List<MObject> updateProperties(JaxbStereotype jaxb_element, Stereotype modelio_element, MObject owner, IReadOnlyRepository repository) {
        if (owner instanceof Note) {
            return super.updateProperties(jaxb_element, modelio_element, owner.getCompositionOwner(), repository);
        } else {
            return super.updateProperties(jaxb_element, modelio_element, owner, repository);    
        }
    }

    @Override
    public void postTreatment(JaxbStereotype jaxb_element, Stereotype modelio_element, IReadOnlyRepository repository) {
        super.postTreatment(jaxb_element, modelio_element, repository);
    }

}
