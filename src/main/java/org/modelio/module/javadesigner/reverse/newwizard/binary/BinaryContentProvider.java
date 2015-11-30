package org.modelio.module.javadesigner.reverse.newwizard.binary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.modelio.module.xmlreverse.model.IVisitorElement;
import com.modelio.module.xmlreverse.model.JaxbClass;
import com.modelio.module.xmlreverse.model.JaxbEnumeration;
import com.modelio.module.xmlreverse.model.JaxbGroup;
import com.modelio.module.xmlreverse.model.JaxbInterface;
import com.modelio.module.xmlreverse.model.JaxbModel;
import com.modelio.module.xmlreverse.model.JaxbPackage;
import com.modelio.module.xmlreverse.model.defaultvisitor.DefaultReverseModelVisitor;

public class BinaryContentProvider implements ITreeContentProvider {

    private CompositionVisitor visitor = new CompositionVisitor();

    private Map<Object, IVisitorElement> ownershipMap = new HashMap<>();

    @Override
    public void dispose() {
        this.ownershipMap.clear();
    }

    @Override
    public Object[] getChildren(Object p_Object) {
        if (p_Object instanceof IVisitorElement) {
            IVisitorElement jaxb_elt = (IVisitorElement) p_Object;
        
            List<?> children = (List<?>) jaxb_elt.accept(this.visitor);
            for (Object child : children) {
                this.ownershipMap.put(child, jaxb_elt);
            }
            
            return children.toArray();
        }
        return Collections.emptyList().toArray();
    }

    @Override
    public Object[] getElements(Object p_Object) {
        Object[] res = this.getChildren(p_Object);
        return res;
    }

    @Override
    public boolean hasChildren(Object p_Object) {
        boolean res = (this.getChildren(p_Object).length > 0);
        return res;
    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
        // reset ownership cache
        this.ownershipMap.clear();
    }

    @Override
    public Object getParent(Object elt) {
        return this.ownershipMap.get(elt);
    }
    
    private static class CompositionVisitor extends DefaultReverseModelVisitor {
        public CompositionVisitor() {
            // Empty c'tor
        }
        
        private boolean keepElement(IVisitorElement element) {
            return (element instanceof JaxbClass)
            || (element instanceof JaxbEnumeration)
            || (element instanceof JaxbGroup)
            || (element instanceof JaxbInterface)
            || (element instanceof JaxbModel)
            || (element instanceof JaxbPackage);
        }

        @Override
        public Object visitClass(JaxbClass element) {
            List<IVisitorElement> ret = new ArrayList<>();

            for (Object collection : element.getClazzOrInterfaceOrInstance()) {
                if (collection instanceof IVisitorElement) {
                    IVisitorElement c_element = (IVisitorElement) collection;
                    if (keepElement(c_element)) {
                        ret.add(c_element);
                    }
                }
            }
            return ret;
        }

        @Override
        public Object visitEnumeration(JaxbEnumeration element) {
            List<IVisitorElement> ret = new ArrayList<>();

            for (Object collection : element.getNoteOrConstraintOrStereotype()) {
                if (collection instanceof IVisitorElement) {
                    IVisitorElement c_element = (IVisitorElement) collection;
                    if (keepElement(c_element)) {
                        ret.add(c_element);
                    }
                }
            }
            return ret;
        }


        @Override
        public Object visitInterface(JaxbInterface element) {
            List<IVisitorElement> ret = new ArrayList<>();

            for (Object collection : element.getClazzOrInterfaceOrEnumeration()) {
                if (collection instanceof IVisitorElement) {
                    IVisitorElement c_element = (IVisitorElement) collection;
                    if (keepElement(c_element)) {
                        ret.add(c_element);
                    }
                }
            }
            return ret;
        }

        @Override
        public Object visitPackage(JaxbPackage element) {
            List<IVisitorElement> ret = new ArrayList<>();

            for (Object collection : element.getGroupOrPackageOrClazz()) {
                if (collection instanceof IVisitorElement) {
                    IVisitorElement c_element = (IVisitorElement) collection;
                    if (keepElement(c_element)) {
                        ret.add(c_element);
                    }
                }
            }
            return ret;
        }

        @Override
        public Object visitModel(JaxbModel element) {
            List<IVisitorElement> ret = new ArrayList<>();

            for (Object collection : element.getPackageOrClazzOrInterface()) {
                if (collection instanceof IVisitorElement) {
                    IVisitorElement c_element = (IVisitorElement) collection;
                    if (keepElement(c_element)) {
                        ret.add(c_element);
                    }
                }
            }
            return ret;
        }

        @Override
        public Object visitGroup(JaxbGroup element) {
            List<IVisitorElement> ret = new ArrayList<>();

            for (Object collection : element.getPackageOrClazzOrInterface()) {
                if (collection instanceof IVisitorElement) {
                    IVisitorElement c_element = (IVisitorElement) collection;
                    if (keepElement(c_element)) {
                        ret.add(c_element);
                    }
                }
            }
            return ret;
        }
    }

}
