package org.modelio.module.javadesigner.utils;

import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.mda.ModuleComponent;
import org.modelio.metamodel.uml.infrastructure.Element;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Profile;
import org.modelio.metamodel.uml.statik.Artifact;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Component;
import org.modelio.metamodel.uml.statik.DataType;
import org.modelio.metamodel.uml.statik.Enumeration;
import org.modelio.metamodel.uml.statik.Interface;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.metamodel.visitors.DefaultModelVisitor;
import org.modelio.module.javadesigner.api.JavaDesignerStereotypes;
import org.modelio.module.javadesigner.impl.JavaDesignerModule;

public class JavaElementStereotypeCreator extends DefaultModelVisitor {
    private static final JavaElementStereotypeCreator INSTANCE = new JavaElementStereotypeCreator ();

    public static boolean addJavaStereotype(ModelElement element) {
        if (element != null) {
            Object ret = element.accept (INSTANCE);
        
            return ret != null && ret.equals (true);
        } else {
            return false;
        }
    }

    @Override
    public Object visitArtifact(Artifact theArtifact) {
        try {
        	ModelUtils.addStereotype (theArtifact, JavaDesignerStereotypes.JARFILE);
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(e.getMessage ());
        }
        return true;
    }

    @Override
    public Object visitClass(Class theClass) {
        try {
        	ModelUtils.addStereotype (theClass, JavaDesignerStereotypes.JAVACLASS);
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(e.getMessage ());
        }
        return true;
    }

    @Override
    public Object visitComponent(Component theComponent) {
        try {
        	ModelUtils.addStereotype (theComponent, JavaDesignerStereotypes.JAVACOMPONENT);
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(e.getMessage ());
        }
        return true;
    }

    @Override
    public Object visitDataType(DataType theDataType) {
        try {
        	ModelUtils.addStereotype (theDataType, JavaDesignerStereotypes.JAVADATATYPE);
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(e.getMessage ());
        }
        return true;
    }

    @Override
    public Object visitEnumeration(Enumeration theEnumeration) {
        try {
        	ModelUtils.addStereotype (theEnumeration, JavaDesignerStereotypes.JAVAENUMERATION);
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(e.getMessage ());
        }
        return true;
    }

    @Override
    public Object visitInterface(Interface theInterface) {
        try {
        	ModelUtils.addStereotype (theInterface, JavaDesignerStereotypes.JAVAINTERFACE);
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(e.getMessage ());
        }
        return true;
    }

    @Override
    public Object visitPackage(Package thePackage) {
        try {
        	ModelUtils.addStereotype (thePackage, JavaDesignerStereotypes.JAVAPACKAGE);
        } catch (ExtensionNotFoundException e) {
            JavaDesignerModule.logService.error(e.getMessage ());
        }
        return true;
    }

    @Override
    public Object visitModuleComponent(ModuleComponent theModule) {
        return false;
    }

    @Override
    public Object visitProfile(Profile theProfile) {
        return false;
    }

    @Override
    public Object visitElement(Element theElement) {
        return false;
    }

}
