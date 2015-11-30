package org.modelio.module.javadesigner.utils;


import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Note;
import org.modelio.metamodel.uml.infrastructure.TaggedValue;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.metamodel.uml.statik.EnumerationLiteral;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Operation;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.metamodel.uml.statik.Parameter;
import org.modelio.metamodel.uml.statik.TemplateParameter;
import org.modelio.metamodel.visitors.DefaultModelVisitor;
import org.modelio.vcore.smkernel.mapi.MObject;

class FileGeneratorGetter {
    private static FileGeneratorGetter INSTANCE;

    /**
     * Visitor that will navigate ownership to retrieve the file generator.
     */
    private FileGeneratorVisitor visitor;

    NameSpace fileGenerator;


    private FileGeneratorGetter() {
        this.visitor = new FileGeneratorVisitor ();
    }

    /**
     * Returns the element if it is a NameSpace, or its nearest parent NameSpace.
     * @param theChild The element from which we will get the owner.
     * @return the nearest NameSpace of the element given as parameter.
     */
    public NameSpace getNearestNameSpace(MObject theChild) {
        this.fileGenerator = null;
        this.visitor.launchVisit (theChild);
        return this.fileGenerator;
    }

    public static FileGeneratorGetter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileGeneratorGetter ();
        }
        return INSTANCE;
    }

    /**
     * This class extends DefaultModelVisitor and visits the ownership link defined at meta-level. The class is
     * inner and private, encapsulated to mask the "visit" methods to the user of the FileGeneratorGetter class. The
     * user will not see this class.
     */
    protected class FileGeneratorVisitor extends DefaultModelVisitor {

        /**
         * Launches the visit.
         */
        public Object launchVisit(MObject theChild) {
            return theChild.accept (this);
        }

        @Override
        public Object visitEnumerationLiteral(EnumerationLiteral theEnumerationLiteral) {
            return theEnumerationLiteral.getValuated ().accept (this);
        }

        /**
         * For an operation, launch the visitor on the owned classifier.
         */
        @Override
        public Object visitOperation(Operation theOperation) {
            NameSpace parent = theOperation.getOwner ();
            if (parent != null) {
                return parent.accept (this);
            }
            return super.visitFeature (theOperation);
        }

        /**
         * For an association end, launch the visitor on the owned classifier.
         */
        @Override
        public Object visitAssociationEnd(AssociationEnd theAssociationEnd) {
            NameSpace parent = theAssociationEnd.getSource ();
            if (parent != null) {
                return parent.accept (this);
            }
            return super.visitFeature (theAssociationEnd);
        }

        /**
         * For a model element, return null.
         */
        @Override
        public Object visitModelElement(ModelElement theModelElement) {
            return null;
        }

        @Override
        public Object visitNameSpace(NameSpace theNameSpace) {
            // Check if this is a correct Java Element
            if (!(theNameSpace instanceof Package) &&
                    !JavaDesignerUtils.isJavaElement (theNameSpace)) {
                FileGeneratorGetter.this.fileGenerator = null;
            } else {
                FileGeneratorGetter.this.fileGenerator = theNameSpace;
            }
            return FileGeneratorGetter.this.fileGenerator;
        }

        @Override
        public Object visitNote(Note theNote) {
            ModelElement parent = theNote.getSubject ();
            if (parent != null) {
                return parent.accept (this);
            }
            return super.visitNote (theNote);
        }

        @Override
        public Object visitParameter(Parameter theParameter) {
            Operation parent = theParameter.getComposed ();
            if (parent == null) {
                parent = theParameter.getReturned ();
            }
            
            if (parent != null) {
                return parent.accept (this);
            }
            return super.visitParameter (theParameter);
        }

        @Override
        public Object visitTaggedValue(TaggedValue theTaggedValue) {
            ModelElement parent = theTaggedValue.getAnnoted ();
            if (parent != null) {
                return parent.accept (this);
            }
            return super.visitTaggedValue (theTaggedValue);
        }

        @Override
        public Object visitAttribute(Attribute theAttribute) {
            AssociationEnd parent = theAttribute.getQualified();
            if (parent != null) {
                return parent.accept (this);
            }
            NameSpace owner = theAttribute.getOwner ();
            if (owner != null) {
                return owner.accept (this);
            }
            return super.visitAttribute(theAttribute);
        }

        @Override
        public Object visitTemplateParameter(TemplateParameter theTemplateParameter) {
            NameSpace parentNS = theTemplateParameter.getParameterized();
            if (parentNS != null) {
                return parentNS.accept (this);
    }

            Operation parentOp = theTemplateParameter.getParameterizedOperation();
            if (parentOp != null) {
                return parentOp.accept (this);
            }
            return super.visitTemplateParameter(theTemplateParameter);
        }
    }
}
