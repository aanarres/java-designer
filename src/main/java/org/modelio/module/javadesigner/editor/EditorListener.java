package org.modelio.module.javadesigner.editor;

import java.io.File;
import java.util.HashSet;

import org.modelio.api.editor.IMDAEditorListener;
import org.modelio.api.editor.IMDATextEditor;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.javadesigner.report.ReportManager;
import org.modelio.module.javadesigner.report.ReportModel;
import org.modelio.module.javadesigner.reverse.ReverseMode;
import org.modelio.module.javadesigner.reverse.Reversor;
import org.modelio.module.javadesigner.reverse.ui.ReverseException;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;

public class EditorListener implements IMDAEditorListener {
    private IModule module;


    public EditorListener() {
        // Nothing to initialize
    }

    public void setModule(IModule module) {
        this.module = module;
    }

    @Override
    public void documentSaved(IMDATextEditor editor, ModelElement element, File file) {
        if (element instanceof NameSpace) {
            HashSet<NameSpace> elementsToReverse = new HashSet<> ();
            elementsToReverse.add ((NameSpace) element);
        
            try {
                JavaDesignerUtils.initCurrentGenRoot (elementsToReverse);
            } catch (InterruptedException e) {
                return;
            }
            
            ReportModel report = ReportManager.getNewReport ();
        
            Reversor reversor = new Reversor (this.module, report);
            // No confirm box
            // Date file are checked. Elements are reverse only if the file is
            // more recent
            try {
                reversor.update (elementsToReverse, ReverseMode.Retrieve, EditorManager.getInstance ());
            } catch (ReverseException e) {
                // The reverse was canceled
            }
        
            ReportManager.showGenerationReport (report);
            
            JavaDesignerUtils.setProjectGenRoot (null);
        }
    }

    @Override
    public void editorClosed(IMDATextEditor editor) {
        EditorManager.getInstance ().removeEditor (editor);
    }

}
