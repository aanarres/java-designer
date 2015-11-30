package org.modelio.module.javadesigner.reverse.newwizard.api;

import java.io.File;
import java.util.List;

import org.modelio.module.javadesigner.reverse.ReverseConfig.GeneralReverseMode;

import com.modelio.module.xmlreverse.model.IVisitorElement;
import com.modelio.module.xmlreverse.model.JaxbReversedData;

public interface IFileChooserModel {

    List<String> getValidExtensions();

    File getInitialDirectory();

    void setInitialDirectory(File initialDirectory);

    List<File> getFilesToImport();

    void setFilesToImport(List<File> filesToImport);

    GeneralReverseMode getGranularity();

    void setGranularity(GeneralReverseMode value);

    JaxbReversedData getAssemblyContentModel();

    List<IVisitorElement> getResult();

    String getValidExtensionsList();

    List<File> getReverseRoots();
}
