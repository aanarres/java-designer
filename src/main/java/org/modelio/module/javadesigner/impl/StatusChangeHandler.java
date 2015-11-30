package org.modelio.module.javadesigner.impl;

import org.eclipse.swt.widgets.Display;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.change.IStatusChangeEvent;
import org.modelio.api.model.change.IStatusChangeHandler;
import org.modelio.api.module.IModule;
import org.modelio.module.javadesigner.api.JavaDesignerParameters;
import org.modelio.module.javadesigner.editor.EditorManager;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.vcore.smkernel.mapi.MObject;

public class StatusChangeHandler implements IStatusChangeHandler {

    private IModule module;

    public StatusChangeHandler(IModule module) {
        this.module = module;
    }

    @Override
    public void handleStatusChange(IModelingSession session, final IStatusChangeEvent event) {
        final EditorManager editorManager = EditorManager.getInstance ();

        // In release mode, all editors are read only
        final boolean isReleaseMode = JavaDesignerUtils.isReleaseMode(this.module);

        // Get the current encoding
        final String encoding = this.module.getConfiguration ().getParameterValue (JavaDesignerParameters.ENCODING);

        Display.getDefault().asyncExec (new Runnable () {
            @Override
            public void run () {
        for (MObject element : event.getStatusChanged()) {
            boolean isReadOnly = isReleaseMode || !element.getStatus ().isModifiable ();
                    editorManager.updateStatusForElement (element, isReadOnly, encoding);
        }
            }
        });
    }
}
