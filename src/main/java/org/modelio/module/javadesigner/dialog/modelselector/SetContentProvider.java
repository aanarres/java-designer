package org.modelio.module.javadesigner.dialog.modelselector;

import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SetContentProvider implements ITreeContentProvider {

    @Override
    public void dispose() {
        // Nothing to do
    }

    @Override
    public Object[] getChildren(Object obj) {
        if (obj instanceof Object[]) {
            return (Object[]) obj;
        } else if (obj instanceof Set<?>) {
            return ((Set<?>) obj).toArray ();
        }
        return null;
    }

    @Override
    public Object[] getElements(Object obj) {
        return this.getChildren (obj);
    }

    @Override
    public Object getParent(Object obj) {
        return null;
    }

    @Override
    public boolean hasChildren(Object obj) {
        return (obj instanceof Set<?>);
    }

    @Override
    public void inputChanged(Viewer viewer, Object obj, Object obj1) {
        // Nothing to do
    }

}
