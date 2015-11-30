package org.modelio.module.javadesigner.api;

import java.util.Collection;

import org.modelio.metamodel.uml.statik.NameSpace;

public interface IRefreshService {

    void refresh(Collection<NameSpace> elements);

}
