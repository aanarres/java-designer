package org.modelio.module.javadesigner.reverse.retrieve;

import org.modelio.api.model.IModelingSession;
import org.modelio.metamodel.uml.infrastructure.ModelElement;

public interface IRetrieveData {

    void inject(IModelingSession session, ModelElement elementToRetrieve) throws Exception;

}
