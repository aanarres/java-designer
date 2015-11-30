package org.modelio.module.javadesigner.impl;

import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.change.IModelChangeEvent;
import org.modelio.api.model.change.IModelChangeListener;
import org.modelio.module.javadesigner.JavaFileManager;
import org.modelio.module.javadesigner.editor.EditorManager;

public class ModelChangeListener implements IModelChangeListener {
    JavaFileManager fileManager;


    public ModelChangeListener(JavaFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void modelChanged(IModelingSession session, IModelChangeEvent event) {
        // Update editors if necessary
        if (event.getUpdateEvents ().size () > 0 ||
                event.getDeleteEvents ().size() > 0) {
            EditorManager.getInstance ().updateEditorsFromElements ();
        }
        
        /**
         * TODO complete this - The JavaFileManager must be called in all accept methods, to keep the cache up to date.
         * - Enum contained in packages/components are not given in the delete events.
         */
        
        /**
         * for (Element createdElement : event.getCreationEvents ()) { if (createdElement instanceof NameSpace) { if
         * (JavaDesignerUtils.isJavaElement (createdElement)) { this.fileManager.setCorrespondingFile ((NameSpace)
         * createdElement); } } }
         * 
         * for (Element updatedElement : event.getUpdateEvents ()) { if (updatedElement instanceof NameSpace) { if
         * (JavaDesignerUtils.isJavaElement (updatedElement)) { this.fileManager.setCorrespondingFile ((NameSpace)
         * updatedElement); } } }
         * 
         * for (ElementMovedEvent moveEvent : event.getMoveEvents ()) { Element movedElement =
         * moveEvent.getMovedElement ();
         * 
         * if (movedElement instanceof NameSpace) { if (JavaDesignerUtils.isJavaElement (movedElement)) {
         * this.fileManager.setCorrespondingFile ((NameSpace) movedElement); } } }
         * 
         * for (ElementDeletedEvent deleteEvent : event.getDeleteEvents ()) { Element deletedElement =
         * deleteEvent.getDeletedElement ();
         * 
         * // Impossible to check the stereotype, but only java elements are stored... if (deletedElement instanceof
         * NameSpace) { this.fileManager.deleteCorrespondingElements ((NameSpace) deletedElement); } }
         */
    }

}
