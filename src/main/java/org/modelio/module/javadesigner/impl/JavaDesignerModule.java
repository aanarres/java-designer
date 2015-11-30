package org.modelio.module.javadesigner.impl;

import org.modelio.api.log.ILogService;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.AbstractJavaModule;
import org.modelio.api.module.IModuleAPIConfiguration;
import org.modelio.api.module.IModuleSession;
import org.modelio.api.module.IModuleUserConfiguration;
import org.modelio.api.module.IParameterEditionModel;
import org.modelio.api.module.paramEdition.BoolParameterModel;
import org.modelio.api.module.paramEdition.DirectoryParameterModel;
import org.modelio.api.module.paramEdition.EnumParameterModel;
import org.modelio.api.module.paramEdition.FileParameterModel;
import org.modelio.api.module.paramEdition.ParameterGroupModel;
import org.modelio.api.module.paramEdition.ParameterModel;
import org.modelio.api.module.paramEdition.ParametersEditionModel;
import org.modelio.api.module.paramEdition.StringParameterModel;
import org.modelio.gproject.ramc.core.model.IModelComponent;
import org.modelio.gproject.ramc.core.packaging.IModelComponentContributor;
import org.modelio.metamodel.mda.ModuleComponent;
import org.modelio.module.javadesigner.api.JavaDesignerParameters;
import org.modelio.module.javadesigner.i18n.Messages;

public class JavaDesignerModule extends AbstractJavaModule {
    protected JavaDesignerPeerModule peerModule;

    protected JavaDesignerSession session;

    /**
     * Proxy instance for the Modelio {@link ILogService}.
     */
    public static JavaDesignerLogService logService;

    /**
     * Buids a new module.
     * <p>
     * <p>
     * This constructor must not be called by the user. It is automatically invoked by Modelio when the module is
     * installed, selected or started.
     */
    public JavaDesignerModule(IModelingSession modelingSession, ModuleComponent moduleComponent, IModuleUserConfiguration moduleConfiguration, IModuleAPIConfiguration peerConfiguration) {
        super(modelingSession, moduleComponent, moduleConfiguration);
        this.session  = new JavaDesignerSession(this);
        this.peerModule = new JavaDesignerPeerModule(this, peerConfiguration);

        JavaDesignerModule.logService = new JavaDesignerLogService(Modelio.getInstance().getLogService(), this);
    }

    @Override
    public JavaDesignerPeerModule getPeerModule() {
        return this.peerModule;
    }

    /**
     * Return the session attached to the current module.
     * <p>
     * <p>
     * This session is used to manage the module lifecycle by declaring the desired implementation on start, select...
     * methods
     */
    @Override
    public IModuleSession getSession() {
        return this.session;
    }

    @Override
    public String getDescription() {
        return Messages.getString ("Module.Description"); //$NON-NLS-1$
    }

    @Override
    public String getLabel() {
        return Messages.getString ("Module.Label"); //$NON-NLS-1$
    }

    @Override
    public String getModuleImagePath() {
        return "/res/bmp/JavaDesigner.png";
    }

    @Override
    public IParameterEditionModel getParametersEditionModel() {
        if (this.parameterEditionModel == null) {
            IModuleUserConfiguration configuration = this.getConfiguration();
            ParametersEditionModel parameters = new ParametersEditionModel(this);
            ParameterModel parameter;
            EnumParameterModel enum_parameter;
            this.parameterEditionModel = parameters;

            ParameterGroupModel Group01 = new ParameterGroupModel("Group01", Messages.getString ("Ui.Parameter.Group01"));
            parameters.addGroup(Group01);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.GENERATIONMODE, Messages.getString ("Ui.Parameter.GenerationMode.Label"), Messages.getString ("Ui.Parameter.GenerationMode.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.GenerationMode.RoundTrip.toString(), Messages.getString ("Ui.Parameter.GenerationMode.RoundTrip"));
            enum_parameter.addItem(JavaDesignerParameters.GenerationMode.ModelDriven.toString(), Messages.getString ("Ui.Parameter.GenerationMode.ModelDriven"));
            enum_parameter.addItem(JavaDesignerParameters.GenerationMode.Release.toString(), Messages.getString ("Ui.Parameter.GenerationMode.Release"));
            parameter = enum_parameter;
            Group01.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.RETRIEVEDEFAULTBEHAVIOUR, Messages.getString ("Ui.Parameter.RetrieveDefaultBehaviour.Label"), Messages.getString ("Ui.Parameter.RetrieveDefaultBehaviour.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.RetrieveMode.Ask.toString(), Messages.getString ("Ui.Parameter.RetrieveMode.Ask"));
            enum_parameter.addItem(JavaDesignerParameters.RetrieveMode.Retrieve.toString(), Messages.getString ("Ui.Parameter.RetrieveMode.Retrieve"));
            enum_parameter.addItem(JavaDesignerParameters.RetrieveMode.Keep.toString(), Messages.getString ("Ui.Parameter.RetrieveMode.Keep"));
            parameter = enum_parameter;
            Group01.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.ENCODING, Messages.getString ("Ui.Parameter.Encoding.Label"), Messages.getString ("Ui.Parameter.Encoding.Description"),"UTF-8");
            enum_parameter.addItem("ISO-8859-1", Messages.getString ("Ui.Parameter.Encodings.ISOmoins8859moins1"));
            enum_parameter.addItem("US-ASCII", Messages.getString ("Ui.Parameter.Encodings.USmoinsASCII"));
            enum_parameter.addItem("UTF-8", Messages.getString ("Ui.Parameter.Encodings.UTFmoins8"));
            enum_parameter.addItem("UTF-16BE", Messages.getString ("Ui.Parameter.Encodings.UTFmoins16BE"));
            enum_parameter.addItem("UTF-16LE", Messages.getString ("Ui.Parameter.Encodings.UTFmoins16LE"));
            enum_parameter.addItem("UTF-16", Messages.getString ("Ui.Parameter.Encodings.UTFmoins16"));
            parameter = enum_parameter;
            Group01.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.JAVACOMPATIBILITY, Messages.getString ("Ui.Parameter.JavaCompatibility.Label"), Messages.getString ("Ui.Parameter.JavaCompatibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.CompatibilityLevel.Java7.toString(), Messages.getString ("Ui.Parameter.CompatibilityLevel.Java7"));
            enum_parameter.addItem(JavaDesignerParameters.CompatibilityLevel.Java8.toString(), Messages.getString ("Ui.Parameter.CompatibilityLevel.Java8"));
            parameter = enum_parameter;
            Group01.addParameter (parameter);

            ParameterGroupModel Group11 = new ParameterGroupModel("Group11", Messages.getString ("Ui.Parameter.Group11"));
            parameters.addGroup(Group11);
            parameter = new DirectoryParameterModel(configuration, JavaDesignerParameters.JDKPATH, Messages.getString ("Ui.Parameter.JDKPath.Label"), Messages.getString ("Ui.Parameter.JDKPath.Description"),"");
            Group11.addParameter (parameter);
            // parameter = new StringParameterModel(configuration, JavaDesignerParameters.AccessibleClasses, Messages.getString ("Ui.Parameter.AccessibleClasses.Label"), Messages.getString ("Ui.Parameter.AccessibleClasses.Description"),"");
            // Group11.addParameter (parameter);
            parameter = new DirectoryParameterModel(configuration, JavaDesignerParameters.GENERATIONPATH, Messages.getString ("Ui.Parameter.GenerationPath.Label"), Messages.getString ("Ui.Parameter.GenerationPath.Description"),"");
            Group11.addParameter (parameter);
            parameter = new StringParameterModel(configuration, JavaDesignerParameters.COMPONENTSUBPATH, Messages.getString ("Ui.Parameter.ComponentSubPath.Label"), Messages.getString ("Ui.Parameter.ComponentSubPath.Description"),"");
            Group11.addParameter (parameter);
            parameter = new DirectoryParameterModel(configuration, JavaDesignerParameters.JAVAHGENERATIONPATH, Messages.getString ("Ui.Parameter.JavahGenerationPath.Label"), Messages.getString ("Ui.Parameter.JavahGenerationPath.Description"),"");
            Group11.addParameter (parameter);
            parameter = new DirectoryParameterModel(configuration, JavaDesignerParameters.COMPILATIONPATH, Messages.getString ("Ui.Parameter.CompilationPath.Label"), Messages.getString ("Ui.Parameter.CompilationPath.Description"),"");
            Group11.addParameter (parameter);
            parameter = new DirectoryParameterModel(configuration, JavaDesignerParameters.JARFILEPATH, Messages.getString ("Ui.Parameter.JarFilePath.Label"), Messages.getString ("Ui.Parameter.JarFilePath.Description"),"");
            Group11.addParameter (parameter);
            parameter = new DirectoryParameterModel(configuration, JavaDesignerParameters.GENDOCPATH, Messages.getString ("Ui.Parameter.GenDocPath.Label"), Messages.getString ("Ui.Parameter.GenDocPath.Description"),"");
            Group11.addParameter (parameter);

            ParameterGroupModel Group21 = new ParameterGroupModel("Group21", Messages.getString ("Ui.Parameter.Group21"));
            parameters.addGroup(Group21);
            // FIXME add extensions for all file parameters
            parameter = new FileParameterModel(configuration, JavaDesignerParameters.CUSTOMIZATIONFILE, Messages.getString ("Ui.Parameter.CustomizationFile.Label"), Messages.getString ("Ui.Parameter.CustomizationFile.Description"),"");
            Group21.addParameter (parameter);
            parameter = new FileParameterModel(configuration, JavaDesignerParameters.COPYRIGHTFILE, Messages.getString ("Ui.Parameter.CopyrightFile.Label"), Messages.getString ("Ui.Parameter.CopyrightFile.Description"),"");
            Group21.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.ERRORONFIRSTWARNING, Messages.getString ("Ui.Parameter.ErrorOnFirstWarning.Label"), Messages.getString ("Ui.Parameter.ErrorOnFirstWarning.Description"),"");
            Group21.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.READONLYELEMENTNOTGENERATED, Messages.getString ("Ui.Parameter.ReadOnlyElementNotGenerated.Label"), Messages.getString ("Ui.Parameter.ReadOnlyElementNotGenerated.Description"),"");
            Group21.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.LOCKGENERATEDFILES, Messages.getString ("Ui.Parameter.LockGeneratedFiles.Label"), Messages.getString ("Ui.Parameter.LockGeneratedFiles.Description"),"");
            Group21.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.FULLNAMEGENERATION, Messages.getString ("Ui.Parameter.FullNameGeneration.Label"), Messages.getString ("Ui.Parameter.FullNameGeneration.Description"),"");
            Group21.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.GENERATEFINALPARAMETERS, Messages.getString ("Ui.Parameter.GenerateFinalParameters.Label"), Messages.getString ("Ui.Parameter.GenerateFinalParameters.Description"),"");
            Group21.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.GENERATEDEFAULTRETURN, Messages.getString ("Ui.Parameter.GenerateDefaultReturn.Label"), Messages.getString ("Ui.Parameter.GenerateDefaultReturn.Description"),"");
            Group21.addParameter (parameter);

            ParameterGroupModel Group31 = new ParameterGroupModel("Group31", Messages.getString ("Ui.Parameter.Group31"));
            parameters.addGroup(Group31);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.INTERFACEIMPLEMENTATION, Messages.getString ("Ui.Parameter.InterfaceImplementation.Label"), Messages.getString ("Ui.Parameter.InterfaceImplementation.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.InterfaceImplementationMode.Ask.toString(), Messages.getString ("Ui.Parameter.InterfaceImplementationMode.Ask"));
            enum_parameter.addItem(JavaDesignerParameters.InterfaceImplementationMode.Always.toString(), Messages.getString ("Ui.Parameter.InterfaceImplementationMode.Always"));
            enum_parameter.addItem(JavaDesignerParameters.InterfaceImplementationMode.Never.toString(), Messages.getString ("Ui.Parameter.InterfaceImplementationMode.Never"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.ACCESSORSGENERATION, Messages.getString ("Ui.Parameter.AccessorsGeneration.Label"), Messages.getString ("Ui.Parameter.AccessorsGeneration.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorsGenerationMode.Always.toString(), Messages.getString ("Ui.Parameter.AccessorsGenerationMode.Always"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorsGenerationMode.Smart.toString(), Messages.getString ("Ui.Parameter.AccessorsGenerationMode.Smart"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorsGenerationMode.Never.toString(), Messages.getString ("Ui.Parameter.AccessorsGenerationMode.Never"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.PUBLICACCESSORVISIBILITY, Messages.getString ("Ui.Parameter.PublicAccessorVisibility.Label"), Messages.getString ("Ui.Parameter.PublicAccessorVisibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Public.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Public"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Protected.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Protected"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Friendly.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Friendly"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Private.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Private"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.PUBLICMODIFIERVISIBILITY, Messages.getString ("Ui.Parameter.PublicModifierVisibility.Label"), Messages.getString ("Ui.Parameter.PublicModifierVisibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Public.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Public"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Protected.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Protected"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Friendly.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Friendly"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Private.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Private"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.PROTECTEDACCESSORVISIBILITY, Messages.getString ("Ui.Parameter.ProtectedAccessorVisibility.Label"), Messages.getString ("Ui.Parameter.ProtectedAccessorVisibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Public.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Public"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Protected.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Protected"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Friendly.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Friendly"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Private.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Private"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.PROTECTEDMODIFIERVISIBILITY, Messages.getString ("Ui.Parameter.ProtectedModifierVisibility.Label"), Messages.getString ("Ui.Parameter.ProtectedModifierVisibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Public.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Public"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Protected.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Protected"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Friendly.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Friendly"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Private.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Private"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.FRIENDLYACCESSORVISIBILITY, Messages.getString ("Ui.Parameter.FriendlyAccessorVisibility.Label"), Messages.getString ("Ui.Parameter.FriendlyAccessorVisibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Public.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Public"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Protected.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Protected"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Friendly.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Friendly"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Private.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Private"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.FRIENDLYMODIFIERVISIBILITY, Messages.getString ("Ui.Parameter.FriendlyModifierVisibility.Label"), Messages.getString ("Ui.Parameter.FriendlyModifierVisibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Public.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Public"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Protected.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Protected"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Friendly.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Friendly"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Private.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Private"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.PRIVATEACCESSORVISIBILITY, Messages.getString ("Ui.Parameter.PrivateAccessorVisibility.Label"), Messages.getString ("Ui.Parameter.PrivateAccessorVisibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Public.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Public"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Protected.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Protected"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Friendly.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Friendly"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Private.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Private"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);
            enum_parameter = new EnumParameterModel(configuration, JavaDesignerParameters.PRIVATEMODIFIERVISIBILITY, Messages.getString ("Ui.Parameter.PrivateModifierVisibility.Label"), Messages.getString ("Ui.Parameter.PrivateModifierVisibility.Description"),"");
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Public.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Public"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Protected.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Protected"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Friendly.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Friendly"));
            enum_parameter.addItem(JavaDesignerParameters.AccessorVisibility.Private.toString(), Messages.getString ("Ui.Parameter.AccessorVisibility.Private"));
            parameter = enum_parameter;
            Group31.addParameter (parameter);

            ParameterGroupModel Group41 = new ParameterGroupModel("Group41", Messages.getString ("Ui.Parameter.Group41"));
            parameters.addGroup(Group41);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.GENERATEPREPOSTCONDITIONS, Messages.getString ("Ui.Parameter.GeneratePrePostConditions.Label"), Messages.getString ("Ui.Parameter.GeneratePrePostConditions.Description"),"");
            Group41.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.GENERATEINVARIANTS, Messages.getString ("Ui.Parameter.GenerateInvariants.Label"), Messages.getString ("Ui.Parameter.GenerateInvariants.Description"),"");
            Group41.addParameter (parameter);
            parameter = new StringParameterModel(configuration, JavaDesignerParameters.INVARIANTSNAME, Messages.getString ("Ui.Parameter.InvariantsName.Label"), Messages.getString ("Ui.Parameter.InvariantsName.Description"),"");
            Group41.addParameter (parameter);

            ParameterGroupModel Group51 = new ParameterGroupModel("Group51", Messages.getString ("Ui.Parameter.Group51"));
            parameters.addGroup(Group51);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.USEEXTERNALEDITION, Messages.getString ("Ui.Parameter.UseExternalEdition.Label"), Messages.getString ("Ui.Parameter.UseExternalEdition.Description"),"");
            Group51.addParameter (parameter);
            parameter = new FileParameterModel(configuration, JavaDesignerParameters.EXTERNALEDITORCOMMANDLINE, Messages.getString ("Ui.Parameter.ExternalEditorCommandLine.Label"), Messages.getString ("Ui.Parameter.ExternalEditorCommandLine.Description"),"");
            Group51.addParameter (parameter);

            ParameterGroupModel Group61 = new ParameterGroupModel("Group61", Messages.getString ("Ui.Parameter.Group61"));
            parameters.addGroup(Group61);
            parameter = new StringParameterModel(configuration, JavaDesignerParameters.COMPILATIONOPTIONS, Messages.getString ("Ui.Parameter.CompilationOptions.Label"), Messages.getString ("Ui.Parameter.CompilationOptions.Description"),"");
            Group61.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.USEJAVAH, Messages.getString ("Ui.Parameter.UseJavah.Label"), Messages.getString ("Ui.Parameter.UseJavah.Description"),"");
            Group61.addParameter (parameter);
            parameter = new StringParameterModel(configuration, JavaDesignerParameters.RUNPARAMETERS, Messages.getString ("Ui.Parameter.RunParameters.Label"), Messages.getString ("Ui.Parameter.RunParameters.Description"),"");
            Group61.addParameter (parameter);

            ParameterGroupModel Group71 = new ParameterGroupModel("Group71", Messages.getString ("Ui.Parameter.Group71"));
            parameters.addGroup(Group71);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.GENERATEJAVADOC, Messages.getString ("Ui.Parameter.GenerateJavadoc.Label"), Messages.getString ("Ui.Parameter.GenerateJavadoc.Description"),"");
            Group71.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.DESCRIPTIONASJAVADOC, Messages.getString ("Ui.Parameter.DescriptionAsJavadoc.Label"), Messages.getString ("Ui.Parameter.DescriptionAsJavadoc.Description"),"");
            Group71.addParameter (parameter);
            parameter = new StringParameterModel(configuration, JavaDesignerParameters.JAVADOCOPTIONS, Messages.getString ("Ui.Parameter.JavaDocOptions.Label"), Messages.getString ("Ui.Parameter.JavaDocOptions.Description"),"");
            Group71.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.AUTOMATICALLYOPENJAVADOC, Messages.getString ("Ui.Parameter.AutomaticallyOpenJavadoc.Label"), Messages.getString ("Ui.Parameter.AutomaticallyOpenJavadoc.Description"),"");
            Group71.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.GENERATEJAVADOC_MARKERS, Messages.getString ("Ui.Parameter.GenerateJavadocMarkers.Label"), Messages.getString ("Ui.Parameter.GenerateJavadocMarkers.Description"),"Generate Markers");
            Group71.addParameter (parameter);

            ParameterGroupModel Group81 = new ParameterGroupModel("Group81", Messages.getString ("Ui.Parameter.Group81"));
            parameters.addGroup(Group81);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.PACKAGESRCINRAMC, Messages.getString ("Ui.Parameter.PackageSrcInRamc.Label"), Messages.getString ("Ui.Parameter.PackageSrcInRamc.Description"),"");
            Group81.addParameter (parameter);
            parameter = new BoolParameterModel(configuration, JavaDesignerParameters.PACKAGEJARINRAMC, Messages.getString ("Ui.Parameter.PackageJarInRamc.Label"), Messages.getString ("Ui.Parameter.PackageJarInRamc.Description"),"");
            Group81.addParameter (parameter);
        }
        return this.parameterEditionModel;
    }

    @Override
    public IModelComponentContributor getModelComponentContributor(IModelComponent mc) {
        return new JavaRamcContributor (this, mc);
    }
}
