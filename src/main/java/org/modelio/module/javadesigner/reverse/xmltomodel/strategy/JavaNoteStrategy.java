package org.modelio.module.javadesigner.reverse.xmltomodel.strategy;

import java.util.List;
import java.util.regex.Pattern;

import org.modelio.api.model.IModelingSession;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Note;
import org.modelio.metamodel.uml.infrastructure.NoteType;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.module.javadesigner.api.IJavaDesignerPeerModule;
import org.modelio.module.javadesigner.api.JavaDesignerNoteTypes;
import org.modelio.module.javadesigner.api.JavaDesignerTagTypes;
import org.modelio.module.javadesigner.reverse.ReverseStrategyConfiguration;
import org.modelio.module.javadesigner.reverse.xmltomodel.NoteReverseUtils;
import org.modelio.module.javadesigner.utils.IOtherProfileElements;
import org.modelio.vcore.smkernel.mapi.MObject;

import com.modelio.module.xmlreverse.IReadOnlyRepository;
import com.modelio.module.xmlreverse.model.JaxbNote;
import com.modelio.module.xmlreverse.strategy.NoteStrategy;

public class JavaNoteStrategy extends NoteStrategy {
    public ReverseStrategyConfiguration reverseConfig;


    public JavaNoteStrategy(IModelingSession session, ReverseStrategyConfiguration reverseConfig) {
        super (session);
        this.reverseConfig = reverseConfig;
    }

    @Override
    public Note getCorrespondingElement(JaxbNote jaxb_element, MObject owner, IReadOnlyRepository repository) {
        String noteType = jaxb_element.getNoteType ();
        String module = IJavaDesignerPeerModule.MODULE_NAME;
        if (this.reverseConfig.DESCRIPTIONASJAVADOC) {
            if (noteType.equals (JavaDesignerNoteTypes.CLASS_JAVADOC)) {
                noteType = "description";
                module = IOtherProfileElements.MODULE_NAME;
            }
        }

        Note new_element = ((ModelElement) owner).getNote (module, noteType);
        if (new_element == null) {
            new_element = this.model.createNote ();
        }
        return new_element;
    }

    @Override
    public List<MObject> updateProperties(JaxbNote jaxb_element, Note modelio_element, MObject owner, IReadOnlyRepository repository) {
        ModelElement treeOwner = (ModelElement) owner;
        modelio_element.setSubject (treeOwner);
        modelio_element.setContent ("");

        String moduleName = IJavaDesignerPeerModule.MODULE_NAME;
        String noteType = jaxb_element.getNoteType ();
        if (this.reverseConfig.DESCRIPTIONASJAVADOC) {
            if (noteType.equals (JavaDesignerNoteTypes.CLASS_JAVADOC)) {
                noteType = IOtherProfileElements.MODELELEMENT_DESCRIPTION;
                moduleName = IOtherProfileElements.MODULE_NAME;
            }
        }
        NoteType ntype = this.session.getMetamodelExtensions ().getNoteType (moduleName, noteType, modelio_element.getSubject ().getMClass ());
        modelio_element.setModel (ntype);
        return null;
    }

    @Override
    public void postTreatment(JaxbNote jaxb_element, Note modelio_element, IReadOnlyRepository repository) {
        String noteType = jaxb_element.getNoteType ();

        if (modelio_element != null) {
            String originalContent = modelio_element.getContent ();
            StringBuilder ret = new StringBuilder ();
            if (!originalContent.isEmpty ()) {
                ret.append (originalContent);
                ret.append ("\n");
            }
            String jaxbContent = "";

            for (Object jaxb_sub_element : jaxb_element.getStereotypeOrTaggedValueOrContent ()) {
                if (jaxb_sub_element instanceof String) {
                    jaxbContent = (String) jaxb_sub_element;
                }
            }

            final ModelElement subject = modelio_element.getSubject();
            if (noteType.equals (JavaDesignerNoteTypes.CLASS_JAVADOC) ||
                    noteType.equals (JavaDesignerNoteTypes.ATTRIBUTE_JAVAINITVALUECOMMENT) ||
                    noteType.equals (IOtherProfileElements.MODELELEMENT_DESCRIPTION)) {
                NoteReverseUtils.getInstance().cleanUpComments (ret, jaxbContent);
            } else if (noteType.equals (JavaDesignerNoteTypes.CLASS_JAVAANNOTATION)) {
                if (!isAnnotatedWithStereotype(subject, jaxbContent)) {
                    NoteReverseUtils.getInstance().cleanUpCode (ret, jaxbContent);
                }
            } else {
                NoteReverseUtils.getInstance().cleanUpCode (ret, jaxbContent);
            }

            // Delete empty notes
            if (ret.length () == 0) {
                modelio_element.delete();
            } else {
                modelio_element.setContent (ret.toString ());
                modelio_element.setMimeType("text/plain");

                // Set the type from Javadoc to Description if the parameter is set
                if (this.reverseConfig.DESCRIPTIONASJAVADOC) {
                    if (noteType.equals (JavaDesignerNoteTypes.CLASS_JAVADOC)) {
                        convertJavaDocToDescription (modelio_element);
                    }
                }
                
                // Annotations on complex types are sometimes duplicated in the JavaTypeExpr tag... clean it up before ending the note's process
                if (noteType.equals(JavaDesignerNoteTypes.PARAMETER_JAVAANNOTATION)) {
                    final String typeExpr = subject.getTagValue(IJavaDesignerPeerModule.MODULE_NAME, JavaDesignerTagTypes.PARAMETER_JAVATYPEEXPR);
                    if (typeExpr != null && typeExpr.startsWith(ret.toString ())) {
                        try {
                            subject.putTagValue(IJavaDesignerPeerModule.MODULE_NAME, JavaDesignerTagTypes.PARAMETER_JAVATYPEEXPR, typeExpr.substring(ret.length()));
                        } catch (ExtensionNotFoundException e) {
                            // Can't happen, a note with this type already exists
                        }
                    }
                }
            }
        }
    }

    private void convertJavaDocToDescription(Note modelio_element) {
        NoteType ntype = this.session.getMetamodelExtensions ().getNoteType (IOtherProfileElements.MODULE_NAME, IOtherProfileElements.MODELELEMENT_DESCRIPTION, modelio_element.getSubject ().getMClass ());
        modelio_element.setModel (ntype);
    }

    private boolean isAnnotatedWithStereotype(ModelElement modelio_element, String annotationContent) {
        for (Stereotype stereotype : modelio_element.getExtension()) {
            Pattern pattern = Pattern.compile("^\\s*@" + stereotype.getName());
            if (pattern.matcher(annotationContent).lookingAt()) {
                return true;
            }
        }

        return false;
    }
}
