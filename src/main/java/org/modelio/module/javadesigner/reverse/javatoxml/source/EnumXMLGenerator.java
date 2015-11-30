package org.modelio.module.javadesigner.reverse.javatoxml.source;

import java.io.IOException;
import java.util.List;

import org.modelio.module.javadesigner.i18n.Messages;
import org.modelio.module.javadesigner.reverse.antlr.ASTTree;
import org.modelio.module.javadesigner.reverse.antlr.JavaParser;
import org.modelio.module.javadesigner.reverse.javatoxml.XMLBuffer;
import org.modelio.module.javadesigner.reverse.javatoxml.XMLGeneratorException;
import org.modelio.module.javadesigner.reverse.javatoxml.structuralModel.Modifiers;
import org.modelio.module.javadesigner.reverse.javatoxml.structuralModel.model.ClassifierDef;
import org.modelio.module.javadesigner.reverse.javatoxml.structuralModel.model.ClassifierDef.ClassifierDefKind;

/**
 *  Generate XML syntax for Enum definition
 */
class EnumXMLGenerator extends XMLGeneratorWithModifiers {

    @Override
    public void generateXML(ASTTree ast, final Context ctx) throws XMLGeneratorException, IOException {
        if (ast.getType () != JavaParser.ENUM_DEF) {
            throw new BadNodeTypeException (Messages.getString ("reverse.Node_must_be_ENUM_DEF"), ast); //$NON-NLS-1$
        }
        
		// Process class name
		ASTTree IDENTChild = (ASTTree) ast
				.getFirstChildWithType(JavaParser.IDENT);
		if (IDENTChild == null) {
			throw new XMLGeneratorException(
					Messages.getString("reverse.Enum_name_not_found")); //$NON-NLS-1$
		}
		
		String eltName = IDENTChild.getText();
		ClassifierDef elt = ctx.getSModel().findClassifier(eltName, ctx.getGStack().getCurrentStructuralElement());
		elt.setKind(ClassifierDefKind.ENUMERATION);
		ctx.getGStack().push(elt);
		// Define the internal id
		String id = ctx.getIdManager().defineIdentifier(elt);
		
        XMLBuffer.model.write ("<enumeration id=\""); //$NON-NLS-1$
        XMLBuffer.model.write (id);
        XMLBuffer.model.write ("\" name=\""); //$NON-NLS-1$
        XMLBuffer.model.write (eltName);
        XMLBuffer.model.write ("\""); //$NON-NLS-1$
        
		List<ASTTree> annotations = AnnotationAstServices.getAnnotations(ast);
		
		// Write objid as an XML attribute
		String objid = AnnotationAstServices.extractObjidValue(annotations);
		if (objid != null) {
			XMLBuffer.model.write(" objid=");
			XMLBuffer.model.write(objid);
		}
		
		// Process the modifiers translated as XML attributes
		Modifiers mod = this.compileModifier(ast);
		this.generateModifierAttributes(mod);

		XMLBuffer.model.write(">\n"); //$NON-NLS-1$

		// Generate modifier as tags
		this.generateModifierXMLTags(mod);

		// Generate remaining annotations
		this.generateAnnotationsXMLTags(annotations);

		// (No template parameter stuff initialization, Enum can't have template parameter...)

		// Process other child nodes
		for (ASTTree child : ast.getChildrenSafe()) {
			switch (child.getType()) {
			case JavaParser.IDENT:
			case JavaParser.MODIFIERS:
			case JavaParser.ANNOTATION:
				// Already processed node : skip it
				break;
			default:
				// Delegate the generation of the node to its specialized generator
				XMLGenerator xmlGenerator =  ctx.getGeneratorFactory().getXMLGenerator(child, XMLGeneratorFactory.Context.ENUM);
				if (xmlGenerator != null) {
					xmlGenerator.generateXML(child, ctx);
				}

			}
		}

		XMLBuffer.model.write("</enumeration>\n"); //$NON-NLS-1$
		if (elt.isTopLevel()) {
			// Clear the import context if at top level
			ctx.getTFinder().clearImports();
		}
		ctx.getGStack().pop();
    }
    
	@Override
	protected String formatModifierAttributes(Modifiers mods) {
		StringBuilder buf = new StringBuilder();
		mods.getXMLAttributeVisibility(buf);
		return buf.toString();
	}

	@Override
	protected void generateModifierXMLTags(Modifiers mods) throws IOException {
		// Empty
	}


}

