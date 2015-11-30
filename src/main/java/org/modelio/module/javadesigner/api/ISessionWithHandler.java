package org.modelio.module.javadesigner.api;

import org.modelio.api.model.change.IModelChangeHandler;

public interface ISessionWithHandler {

    IModelChangeHandler getModelChangeHandler();

}
