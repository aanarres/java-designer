package org.modelio.module.javadesigner.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;
import org.modelio.api.editor.IMDATextEditor;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.javadesigner.api.IRefreshService;
import org.modelio.module.javadesigner.api.JavaDesignerParameters;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MStatus;

public class EditorManager implements IRefreshService {
    private static EditorManager instance;

    protected EditorListener listener = null;

    protected IModule module = null;

    protected Vector<IMDATextEditor> editors;


    public Vector<IMDATextEditor> getEditors() {
        return this.editors;
    }

    public EditorManager() {
        this.editors = new Vector<> ();
        this.listener = new EditorListener ();
    }

    protected IMDATextEditor getEditor(NameSpace element) {
        IMDATextEditor editor = null;
        for (Iterator<IMDATextEditor> iterator = this.editors.iterator () ; iterator.hasNext () &&
                (editor == null) ;) {
            IMDATextEditor localeditor = iterator.next ();
            if (element.equals (localeditor.getElement ())) {
                editor = localeditor;
            }
        }
        return editor;
    }

    public static EditorManager getInstance() {
        if (instance == null) {
            instance = new EditorManager ();
        }
        return instance;
    }

    public void open(final NameSpace element, final IModule currentModule) {
        this.module = currentModule;
        final MStatus status = element.getStatus ();

        final String encoding = this.module.getConfiguration ().getParameterValue (JavaDesignerParameters.ENCODING);

        final File file = JavaDesignerUtils.getFilename (element, this.module);

        Display.getDefault().asyncExec (new Runnable () {
            @Override
            public void run () {
                boolean isReadOnly = !status.isModifiable ();
                
                // Retrieve the editor if already opened
                IMDATextEditor editor = getEditor (element);
                if (editor == null) {
                    // Open a new editor
                    org.modelio.api.editor.EditorType editorId;
                    if (JavaDesignerUtils.isRoundtripMode (currentModule)) {
                        editorId = org.modelio.api.editor.EditorType.RTEditor;
                    } else if (JavaDesignerUtils.isModelDrivenMode(currentModule)) {
                        editorId = org.modelio.api.editor.EditorType.MDDEditor;
                    } else {
                        // Release mode, use RT editor for coloration, but always in read only.
                        editorId = org.modelio.api.editor.EditorType.RTEditor;
                        isReadOnly = true;
                    }

                    editor = Modelio.getInstance().getEditionService().openEditor (element, file, editorId, isReadOnly, encoding);
                    editor.setListener (EditorManager.this.listener);
                    EditorManager.this.listener.setModule (currentModule);
                    EditorManager.this.editors.add (editor);
                } else {
                    Modelio.getInstance().getEditionService().activateEditor (editor);
                }
            }
        });
    }

    public void removeEditor(IMDATextEditor editor) {
        this.editors.remove (editor);
    }

    public void updateStatusForElement(final MObject element, final boolean isReadOnly, String encoding) {
        if ((element instanceof NameSpace)) {
            NameSpace modelelement = (NameSpace) element;
            final IMDATextEditor editor = getEditor (modelelement);
            if (editor != null) {
                        editor.setReadonlyMode (isReadOnly);
                editor.setCharsetName(encoding);
            }
        }
    }

    public void updateEditorsFromElements() {
        if (EditorManager.this.editors.size() == 0) {
            return;
        }

        final ArrayList<IMDATextEditor> toDelete = new ArrayList<> ();
        final ArrayList<IMDATextEditor> needModeUpdate = new ArrayList<> ();

        // Get the current generation mode from the module
        final boolean isReleaseMode;
        final org.modelio.api.editor.EditorType editorId;
        if (EditorManager.this.module != null && JavaDesignerUtils.isRoundtripMode (this.module)) {
            editorId = org.modelio.api.editor.EditorType.RTEditor;
            isReleaseMode = false;
        } else if (EditorManager.this.module != null && JavaDesignerUtils.isModelDrivenMode(this.module)) {
            editorId = org.modelio.api.editor.EditorType.MDDEditor;
            isReleaseMode = false;
        } else {
            // Release mode, use RT editor for coloration, but always in read only.
            editorId = org.modelio.api.editor.EditorType.RTEditor;
            isReleaseMode = true;
        }
        
        // Get the current encoding
        final String encoding = this.module.getConfiguration ().getParameterValue (JavaDesignerParameters.ENCODING);

        Display.getDefault().asyncExec (new Runnable () {
            @Override
            public void run () {
                // For all editors, check the mode for update, and verifies the element
                for (IMDATextEditor editor : EditorManager.this.editors) {
                    ModelElement modelElement = editor.getElement ();

                    if (modelElement == null || modelElement.isDeleted ()) {
                        toDelete.add (editor);
                    } else {
                        File openedFile = editor.getFile ();
                        String newPath = JavaDesignerUtils.getDirectory(EditorManager.this.module.getModelingSession(), (NameSpace) editor.getElement ()) + File.separator + JavaDesignerUtils.getJavaName(editor.getElement ()) + ".java";

                        // Check if the file has changed for this element
                        if (!openedFile.getAbsolutePath().endsWith(newPath) || !openedFile.exists ()) {
                            toDelete.add (editor);
                        } else if (editor.getType() != null && !editor.getType().equals (editorId)) {
                            needModeUpdate.add (editor);
                        } else {
                            boolean isReadOnly = isReleaseMode || !modelElement.getStatus().isModifiable ();
                            updateStatusForElement (modelElement, isReadOnly, encoding);
                        }
                    }
                }

                // For all editors with an invalid element, close it
                for (IMDATextEditor editor : toDelete) {
                    Modelio.getInstance().getEditionService().closeEditor (editor);
                    EditorManager.this.editors.remove (editor);
                }

                // For all editors having a bad mode, close the current editor and open it again
                for (IMDATextEditor textEditor : needModeUpdate) {
                    NameSpace theElement = (NameSpace) textEditor.getElement ();
                    Modelio.getInstance().getEditionService().closeEditor (textEditor);
                    open (theElement, EditorManager.this.module);
                }
            }
        });
    }

    public void closeEditors() {
        if (!this.editors.isEmpty()) {
            Display.getDefault().asyncExec (new Runnable () {
                @Override
                public void run () {
                    // Create a temp list to store editors to delete.
                    ArrayList<IMDATextEditor> toDelete = new ArrayList<> (EditorManager.this.editors);

                    for (IMDATextEditor editor : toDelete) {
                        Modelio.getInstance().getEditionService().closeEditor (editor);
                        EditorManager.this.editors.remove (editor);
                    }
                }
            });
        }
    }

    @Override
    public void refresh(final Collection<NameSpace> elements) {
        Display.getDefault().asyncExec (new Runnable () {
            @Override
            public void run () {
                // First, validate all editors
                updateEditorsFromElements ();

                // Check the dirty flag
                for (IMDATextEditor editor : getEditors ()) {
                    if (!editor.isDirty () && elements.contains (editor.getElement ())) {
                        editor.reload ();
                    }
                }
            }
        });
    }

}
