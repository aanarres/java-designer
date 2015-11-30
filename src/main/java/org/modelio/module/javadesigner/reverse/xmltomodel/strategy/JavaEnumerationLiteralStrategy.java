package org.modelio.module.javadesigner.reverse.xmltomodel.strategy;

import java.util.List;

import org.modelio.api.model.IModelingSession;
import org.modelio.metamodel.uml.statik.Enumeration;
import org.modelio.metamodel.uml.statik.EnumerationLiteral;
import org.modelio.vcore.smkernel.mapi.MObject;

import com.modelio.module.xmlreverse.IReadOnlyRepository;
import com.modelio.module.xmlreverse.model.JaxbEnumerationLiteral;
import com.modelio.module.xmlreverse.strategy.EnumerationLiteralStrategy;

public class JavaEnumerationLiteralStrategy extends EnumerationLiteralStrategy {

    public JavaEnumerationLiteralStrategy(IModelingSession session) {
        super (session);
    }

    @Override
    public List<MObject> updateProperties(JaxbEnumerationLiteral jaxb_element, EnumerationLiteral modelio_element, MObject owner, IReadOnlyRepository repository) {
        Enumeration treeOwner = (Enumeration) owner;
        treeOwner.getValue().remove(modelio_element);
        modelio_element.setValuated (treeOwner);
        return super.updateProperties (jaxb_element, modelio_element, owner, repository);
    }

}
