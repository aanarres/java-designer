package org.modelio.module.javadesigner.generator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.javadesigner.api.CustomException;
import org.modelio.module.javadesigner.api.IJavaDesignerPeerModule;
import org.modelio.module.javadesigner.i18n.Messages;
import org.modelio.module.javadesigner.impl.JavaDesignerModule;
import org.modelio.module.javadesigner.progress.ProgressBar;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;

import com.modelio.module.xmlreverse.IReportWriter;

public class GenerateProgressBar extends ProgressBar implements IRunnableWithProgress {
    private Collection<NameSpace> elementsToGenerate;

    private IReportWriter report;


    public GenerateProgressBar(IModule module, Collection<NameSpace> elementsToGenerate, IReportWriter report) {
        super (module, elementsToGenerate.size ());
        this.elementsToGenerate = elementsToGenerate;
        this.report = report;
    }

    @Override
    public void run(IProgressMonitor localMonitor) throws InvocationTargetException, InterruptedException {
        class JavaTempFileStruct {
            File tempFile;
            File realFile;
            NameSpace element;
        
            JavaTempFileStruct(File tempFile, File realFile, NameSpace element) {
                this.tempFile = tempFile;
                this.realFile = realFile;
                this.element = element;
            }
        }
        
        init (true);
        monitor = localMonitor;
        monitor.beginTask ("Generating", this.elementsToGenerate.size ());
        
        List<JavaTempFileStruct> tempFiles = new ArrayList<> ();
        File tempDir;
        // Create a temporary directory
        try {
            tempDir = File.createTempFile (IJavaDesignerPeerModule.MODULE_NAME, ""); //$NON-NLS-1$ //$NON-NLS-2$
            tempDir.delete ();
            tempDir.mkdir ();
            tempDir.deleteOnExit ();
        } catch (IOException e) {
            JavaDesignerModule.logService.error(e);
            // Should nether happen
            throw new InterruptedException (e.getMessage());
        }

        IModelingSession session = this.module.getModelingSession ();
        
        ClassTemplate classTemplate = new ClassTemplate (this.report, session);
        
        // Cr?ation du cache des param?tres du module
        JavaConfiguration javaConfig = new JavaConfiguration (this.module.getConfiguration ());
        
        // Generation
        int id = 0;
        for (NameSpace element : this.elementsToGenerate) {
            if (element instanceof Package &&
                    !JavaDesignerUtils.isPackageAnnotated ((Package) element)) {
                // Nothing to do, the package will be created later
            } else {
                // Compute the real file to generate
                File realFile = JavaDesignerUtils.getFilename (element, this.module);

                monitor.setTaskName (Messages.getString ("Info.ProgressBar.Generating", realFile)); //$NON-NLS-1$

                JavaDesignerModule.logService.info (Messages.getString ("Info.ProgressBar.Generating", realFile));
                
                // Compute the temporary file to generate
                File tempFile = new File (tempDir, "tempFile" + id);
                tempFile.deleteOnExit ();
        
                tempFiles.add (new JavaTempFileStruct(tempFile, realFile, element));
                id++;
        
                try (FileOutputStream fos = new FileOutputStream (tempFile); PrintStream out = new PrintStream (new BufferedOutputStream (fos), false, javaConfig.ENCODING)) {
                    // Call the template
                    try {
                        classTemplate.generate (element, out, javaConfig);
        
                        // Flush the output in case the buffer is not empty
                        out.flush ();
                        
                        out.close();
                    } catch (TemplateException e) {
                        this.report.addError (e.getMessage (), element, "");
                    } catch (CustomException e) {
                        this.report.addError (e.getMessage (), element, "");
                    } catch (Exception e) {
                        JavaDesignerModule.logService.error(e);
                        this.report.addError (e.getMessage (), element, "");
                    }
                } catch (FileNotFoundException e) {
                    this.report.addError (e.getMessage (), element, "");
                } catch (UnsupportedEncodingException e) {
                    this.report.addError (e.getMessage (), element, "");
                } catch (IOException e) {
                    this.report.addError (e.getMessage (), element, "");
                } finally {
                    if (javaConfig.LOCKGENERATEDFILES && !element.getStatus ().isModifiable ()) {
                        tempFile.setReadOnly ();
                    }
                }
        
                updateProgressBar (null);
                if (monitor.isCanceled ()) {
                    break;
                }
            }
        }
        
        // Move all temporary files towards their real destinations
        if (!this.report.hasErrors ()) {
            for (JavaTempFileStruct tempStruct : tempFiles) {
                if (tempStruct.tempFile.exists ()) {
                    if (tempStruct.realFile.exists ()) {
                        tempStruct.realFile.delete ();
                    }
        
                    if (!tempStruct.tempFile.renameTo (tempStruct.realFile)) {
                        // If the rename fails, copy the file manually then delete the original...
                        JavaDesignerUtils.copyFile (tempStruct.tempFile, tempStruct.realFile);
                        tempStruct.tempFile.delete ();
                    }
                    
                    // Save the file time on the element
                    JavaDesignerUtils.updateDate (session, tempStruct.element, tempStruct.realFile.lastModified ());
                }
            }
        } else {
            this.report.addError (Messages.getString ("Error.GenerationCanceled"), null, Messages.getString ("Error.GenerationCanceled.GenerationFailed")); //$NON-NLS-1$  //$NON-NLS-2$
        }
        
        JavaDesignerModule.logService.info (this.elementsToGenerate.size () + " elements generated in " + formatTime (getElapsedTime ()));
        
        ProgressBar.monitor.done ();
    }

}
