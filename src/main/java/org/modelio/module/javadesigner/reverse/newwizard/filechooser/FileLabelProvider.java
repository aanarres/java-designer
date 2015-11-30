package org.modelio.module.javadesigner.reverse.newwizard.filechooser;

import java.io.File;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.modelio.module.javadesigner.reverse.newwizard.ImageManager;

class FileLabelProvider implements ILabelProvider {

    @Override
    public Image getImage(Object arg0) {
        if (arg0 instanceof File) {
            File f = (File)arg0;
            String name = f.getName();
        
            if (name != null && name.endsWith (".java")) { //$NON-NLS-1$
                return ImageManager.getInstance().getIcon("java");
            } else if (name != null && name.endsWith (".class")) {
                return ImageManager.getInstance().getIcon("class");
            } else if (name != null && name.endsWith (".jar")) {
                return ImageManager.getInstance().getIcon("jarfile");
            } else if (f.isDirectory()) {
                return ImageManager.getInstance().getIcon("directory");
            }
        }
        return null;
    }

    @Override
    public String getText(Object arg0) {
        if (arg0 instanceof File) {
            File f = (File)arg0;
            return f.getName();
        }
        return arg0.toString();
    }

    @Override
    public void addListener(ILabelProviderListener arg0) {
        // Nothing to do
    }

    @Override
    public void dispose() {
        // Nothing to do
    }

    @Override
    public boolean isLabelProperty(Object arg0, String arg1) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener arg0) {
        // Nothing to do
    }

}
