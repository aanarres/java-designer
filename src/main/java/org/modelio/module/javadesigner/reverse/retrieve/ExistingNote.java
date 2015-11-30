package org.modelio.module.javadesigner.reverse.retrieve;

import org.modelio.api.model.IModelingSession;
import org.modelio.metamodel.factory.ElementNotUniqueException;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Note;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.metamodel.uml.statik.Operation;
import org.modelio.module.javadesigner.api.IJavaDesignerPeerModule;
import org.modelio.module.javadesigner.api.JavaDesignerNoteTypes;
import org.modelio.module.javadesigner.i18n.Messages;
import org.modelio.module.javadesigner.reverse.xmltomodel.NoteReverseUtils;
import org.modelio.module.javadesigner.utils.IOtherProfileElements;

import com.modelio.module.xmlreverse.IReportWriter;
import com.modelio.module.xmlreverse.Repository;

public class ExistingNote extends NoteData {
    private String noteId = null;


    @Override
    public void inject(IModelingSession session, ModelElement elementToRetrieve) throws ExtensionNotFoundException, ElementNotUniqueException {
        Note theNote = session.findElementById(Note.class, this.noteId);
        
        if (theNote != null) {
            if (this.noteType == null) {
                this.noteType = theNote.getModel() != null ? theNote.getModel().getName():"";
            }
            if (this.noteType.equals (JavaDesignerNoteTypes.CLASS_JAVADOC) ||
                    this.noteType.equals (JavaDesignerNoteTypes.ATTRIBUTE_JAVAINITVALUECOMMENT) ||
                    this.noteType.equals (IOtherProfileElements.MODELELEMENT_DESCRIPTION)) {
                // Remove all comment marks and indent
                StringBuilder finalNoteContent = new StringBuilder();
                NoteReverseUtils.getInstance().cleanUpComments (finalNoteContent, this.noteContent.trim ());
        
                String tempContent = finalNoteContent.toString();
        
                ModelElement noteOwner = theNote.getSubject();
                if (noteOwner instanceof GeneralClass) {
                    tempContent = NoteReverseUtils.getInstance ().reverseJavadoc (session, tempContent, (GeneralClass) noteOwner, new Repository());
                } else if (noteOwner instanceof Operation) {
                    String moduleName = this.noteType.equals (IOtherProfileElements.MODELELEMENT_DESCRIPTION) ?IOtherProfileElements.MODULE_NAME:IJavaDesignerPeerModule.MODULE_NAME;
                    tempContent = NoteReverseUtils.getInstance ().reverseJavadoc (session, tempContent, (Operation) noteOwner, moduleName, this.noteType, new Repository());
                }
        
                if (tempContent.isEmpty ()) {
                	theNote.delete();
                } else {
                    theNote.setContent (tempContent);
                }
            } else {
                // Remove indent
                StringBuilder finalNoteContent = new StringBuilder();
                NoteReverseUtils.getInstance().cleanUpCode (finalNoteContent, this.noteContent);
                
                theNote.setContent(finalNoteContent.toString());
            }
        } else {
            this.getReport().addWarning(Messages.getString ("Error.RetrieveError.NoteNotFound", this.noteId, elementToRetrieve.getName()), elementToRetrieve, Messages.getString ("Error.RetrieveError.NoteNotFoundDescription", this.noteContent));
        }
    }

    public ExistingNote(String noteType, String noteContent, String noteId, IReportWriter report) {
        super(noteType, noteContent, report);
        this.noteId = noteId;
    }

    public String getNoteId() {
        return this.noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    @Override
    public String toString() {
        return "ExistingNote : type=\"" + this.noteType + "\" id=\"" + this.noteId + "\"";
    }

}
