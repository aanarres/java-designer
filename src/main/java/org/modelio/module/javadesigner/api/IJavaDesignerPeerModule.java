package org.modelio.module.javadesigner.api;

import java.io.File;
import java.util.Collection;

import org.modelio.api.module.IPeerModule;
import org.modelio.metamodel.factory.ElementNotUniqueException;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.statik.Artifact;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.metamodel.uml.statik.Classifier;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;


public interface IJavaDesignerPeerModule extends IPeerModule {
    public static final String MODULE_NAME = "JavaDesigner";
    
    void generate(NameSpace element);

    void generate(Collection<NameSpace> elements);

    void generateAntFile(Artifact artifact);

    String executeTarget(Artifact artifact, String target);

    void updateModel(Collection<NameSpace> elements);

    void generate(NameSpace element, boolean withGUI);

    void generate(Collection<NameSpace> elements, boolean withGUI);

    void generateAntFile(Artifact artifact, boolean withGUI);

    String executeTarget(Artifact artifact, String target, boolean withGUI);

    void updateModel(Collection<NameSpace> elements, boolean withGUI);

    File getFilename(NameSpace element);

    /**
     * Update all the accessors corresponding to this element (type, card, name...).
     * @param theAttribute The model element to synchronize accessors from.
     * @param createNewAccessors Indicates whereas the method should create accessors if necessary, or only synchronize the existing ones.
     * @return true if something changed in the model.
     * @throws ElementNotUniqueException 
     */
    boolean updateAccessors(Attribute theAttribute, boolean createNewAccessors) throws CustomException, ExtensionNotFoundException, ElementNotUniqueException;

    /**
     * Update all the accessors corresponding to this element (type, card, name...).
     * @param theAssociationEnd The model element to synchronize accessors from.
     * @param createNewAccessors Indicates whereas the method should create accessors if necessary, or only synchronize the existing ones.
     * @return true if something changed in the model.
     * @throws ElementNotUniqueException 
     */
    boolean updateAccessors(AssociationEnd theAssociationEnd, boolean createNewAccessors) throws CustomException, ExtensionNotFoundException, ElementNotUniqueException;

    /**
     * Check all operations, and deletes getters and setters that are no more linked to Attributes/AssociationEnds.
     * @return True if at least one operation was deleted.
     */
    boolean deleteAccessors(Classifier theClassifier);
    
    void generateJavaDoc(Package element, boolean withGUI);
}
