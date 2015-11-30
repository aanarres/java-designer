package org.modelio.module.javadesigner.reverse.javatoxml.source;

import org.modelio.module.javadesigner.reverse.javatoxml.identification.IdentifierManager;
import org.modelio.module.javadesigner.reverse.javatoxml.structuralModel.SourceStructuralModel;

import com.modelio.module.xmlreverse.IReportWriter;

/**
 * Context of a java to XML generation for both sources and binary
 * The life time of a context is the XML generation of a set of files.
 */
class Context {
    /*
     * Singleton used by both binary and source generators
     */
    private SourceStructuralModel sModel;

    /*
     * Singleton used by both binary and source generators
     */

    private IdentifierManager idManager;

    /*
     * Singletons used by source generator only 
     */
    private TypeFinder tFinder;

    /*
     * Singletons used by source generator only 
     */
    private GeneratorStack gStack;

    /*
     * Singletons used by source generator only 
     */
    private XMLGeneratorFactory gFactory;

    /**
     * Current report writer.
     */
    private IReportWriter report;
    
    /**
     * Constructor for the source generator
     */
    public Context(SourceStructuralModel sModel, IdentifierManager idManager, IReportWriter report, TypeFinder tFinder, GeneratorStack gStack, XMLGeneratorFactory gFactory) {
        this.sModel = sModel;
        this.idManager = idManager;
        this.report = report;
        this.tFinder = tFinder;
        this.gStack = gStack;
        this.gFactory = gFactory;
    }

    public SourceStructuralModel getSModel() {
        return this.sModel;
    }

    public IdentifierManager getIdManager() {
        return this.idManager;
    }

    public TypeFinder getTFinder() {
        return this.tFinder;
    }

    public GeneratorStack getGStack() {
        return this.gStack;
    }

    public XMLGeneratorFactory getGeneratorFactory() {
        return this.gFactory;
    }

    public IReportWriter getReport() {
        return this.report;
    }
}
