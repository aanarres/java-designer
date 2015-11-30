package org.modelio.module.javadesigner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;

public class JavaFileManager {
    private IModule module;

    private Map<NameSpace, File> correspondingFiles;


    public JavaFileManager(IModule module) {
        this.module = module;
        this.correspondingFiles = new HashMap<> ();
    }

    public File getCorrespondingFile(NameSpace element) {
        File correspondingFile = this.correspondingFiles.get (element);
        return correspondingFile;
    }

    public void removeCorrespondingFile(NameSpace element) {
        this.correspondingFiles.remove (element);
    }

    public File setCorrespondingFile(NameSpace element) {
        File correspondingFile = JavaDesignerUtils.getFilename (element, this.module);
        setCorrespondingFile (element, correspondingFile);
        return correspondingFile;
    }

    public void setCorrespondingFile(NameSpace element, File newFile) {
        File oldFile = getCorrespondingFile (element);
        
        if (oldFile != null && !newFile.equals (oldFile) && oldFile.exists ()) {
            File parent = newFile.getParentFile ();
            if (parent != null && !parent.exists ()) {
                parent.mkdirs ();
            }
            oldFile.renameTo (newFile);
        }
        
        this.correspondingFiles.put (element, newFile);
    }

    public void deleteCorrespondingElements(NameSpace deletedElement) {
        File oldFile = getCorrespondingFile (deletedElement);
        
        if (oldFile != null) {
            oldFile.delete ();
        }
    }

}
