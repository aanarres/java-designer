package org.modelio.module.javadesigner.reverse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.ITransaction;
import org.modelio.api.model.InvalidTransactionException;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.IModule;
import org.modelio.api.module.IModuleSession;
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.infrastructure.ModelTree;
import org.modelio.metamodel.uml.statik.Artifact;
import org.modelio.metamodel.uml.statik.Component;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.javadesigner.api.IRefreshService;
import org.modelio.module.javadesigner.api.ISessionWithHandler;
import org.modelio.module.javadesigner.api.JavaDesignerParameters;
import org.modelio.module.javadesigner.api.JavaDesignerProperties;
import org.modelio.module.javadesigner.i18n.Messages;
import org.modelio.module.javadesigner.impl.JavaDesignerModule;
import org.modelio.module.javadesigner.reverse.newwizard.ImageManager;
import org.modelio.module.javadesigner.reverse.newwizard.api.IClasspathModel;
import org.modelio.module.javadesigner.reverse.newwizard.api.IExternalJarsModel;
import org.modelio.module.javadesigner.reverse.newwizard.api.IFileChooserModel;
import org.modelio.module.javadesigner.reverse.newwizard.classpath.JavaClasspathModel;
import org.modelio.module.javadesigner.reverse.newwizard.externaljars.ExternalJarsClasspathModel;
import org.modelio.module.javadesigner.reverse.newwizard.filechooser.JavaFileChooserModel;
import org.modelio.module.javadesigner.reverse.newwizard.wizard.JavaReverseWizardView;
import org.modelio.module.javadesigner.reverse.ui.ElementStatus;
import org.modelio.module.javadesigner.reverse.ui.ElementStatus.ElementType;
import org.modelio.module.javadesigner.reverse.ui.ElementStatus.ReverseStatus;
import org.modelio.module.javadesigner.reverse.ui.ReverseException;
import org.modelio.module.javadesigner.utils.IOtherProfileElements;
import org.modelio.module.javadesigner.utils.JavaDesignerUtils;
import org.modelio.module.javadesigner.utils.ModelUtils;
import org.modelio.vcore.smkernel.mapi.MStatus;

import com.modelio.module.xmlreverse.IReportWriter;

public class Reversor {
	private IModule module;

	private ISessionWithHandler javaDesignerSession;

	private Collection<NameSpace> elementsToReverse;

	private IReportWriter report;

	public ReverseMode lastReverseMode = ReverseMode.Ask;


	public Reversor(IModule module, IReportWriter report) {
		this.module = module;
		this.report = report;

		IModuleSession moduleSession = module.getSession ();
		if (moduleSession instanceof ISessionWithHandler) {
			this.javaDesignerSession = (ISessionWithHandler) moduleSession;
		} else {
			this.javaDesignerSession = null;
		}
	}

	public void reverseWizard(ReverseType reverseType, NameSpace reverseRoot) {
		removeModelHandler();

		this.elementsToReverse = new HashSet<> ();
		this.elementsToReverse.add (reverseRoot);
		reverseInRoundtripMode (true, reverseType);

		addModelhandler();
	}

	public void update(Collection<NameSpace> elements, ReverseMode mode, IRefreshService refreshService) throws ReverseException {
		boolean isRoundTripMode = JavaDesignerUtils.isRoundtripMode (this.module);
		boolean cancel = false;
		boolean confirmBox = true;

		removeModelHandler();

		LocalReverseMode retrieveBehavior = LocalReverseMode.RETRIEVE;

		this.elementsToReverse = elements;

		if (mode == ReverseMode.Ask) {
			confirmBox = true;
			this.lastReverseMode = ReverseMode.Ask;
		} else {
			confirmBox = false;

			if (mode == ReverseMode.Retrieve) {
				retrieveBehavior = LocalReverseMode.RETRIEVE_ALL;
				this.lastReverseMode = ReverseMode.Retrieve;
			} else {
				retrieveBehavior = LocalReverseMode.KEEP_ALL;
				this.lastReverseMode = ReverseMode.Ask;
			}
		}

		HashSet<NameSpace> concreteElementToReverse = new HashSet<> ();

		if (retrieveBehavior != LocalReverseMode.KEEP_ALL) {
			for (NameSpace element : this.elementsToReverse) {
				// Only modifiable elements can be added for reverse...
				MStatus status = element.getStatus ();
				if (status != null && status.isModifiable ()) {
					if (element instanceof Package &&
							!JavaDesignerUtils.isPackageAnnotated ((Package) element)) {
						if (isRoundTripMode) {
							concreteElementToReverse.add (element);
						}
					} else {
						File currentFile = JavaDesignerUtils.getFilename (element, this.module);
						long fileTime = currentFile.lastModified ();
						String stringElementTime = ModelUtils.getLocalProperty (element, JavaDesignerProperties.JAVALASTGENERATED);
						long elementTime = 0L;
						if (stringElementTime.length () > 0) {
							elementTime = Long.parseLong (stringElementTime);
						}

						if (fileTime > elementTime) {
							if (confirmBox) {
								retrieveBehavior = getRetrieveBehavior (currentFile);

								if ((retrieveBehavior == LocalReverseMode.RETRIEVE_ALL) ||
										(retrieveBehavior == LocalReverseMode.KEEP_ALL)) {
									// No box anymore
									confirmBox = false;
								} else if (retrieveBehavior == LocalReverseMode.CANCEL) {
									// Set cancel to avoid continuing
									cancel = true;
									break;
								}
							}

							if (retrieveBehavior == LocalReverseMode.RETRIEVE) {
								concreteElementToReverse.add (element);
							} else if (retrieveBehavior == LocalReverseMode.RETRIEVE_ALL) {
								concreteElementToReverse.add (element);
								this.lastReverseMode = ReverseMode.Retrieve;
							} else if (retrieveBehavior == LocalReverseMode.KEEP_ALL) {
								this.lastReverseMode = ReverseMode.Keep;
							}
						}
					}
				}
			}
		}
		this.elementsToReverse = concreteElementToReverse;

		if (!cancel) {
			if (!this.elementsToReverse.isEmpty ()) {
				if (isRoundTripMode) {
					reverseInRoundtripMode (false, ReverseType.SOURCE);
				} else {
					// Model driven: only reverse between identifiers
					reverseInModelDrivenMode ();
				}
			}

			// Refresh the editors
			if (refreshService != null) {
				refreshService.refresh (this.elementsToReverse);
			}
		}

		addModelhandler();

		if (cancel) {
			throw new ReverseException ("Reverse canceled");
		}
	}

	private ArrayList<File> getClassPath() throws IOException {
		ArrayList<File> classpath = new ArrayList<> ();
		StringTokenizer st;

		String separator = System.getProperty ("os.name").startsWith ("Windows") ? ";" : ":";

		st = new StringTokenizer (this.module.getConfiguration ().getParameterValue (JavaDesignerParameters.ACCESSIBLECLASSES), separator); //$NON-NLS-1$
		while (st.hasMoreTokens ()) {
			File file = new File (st.nextToken ());
			if (file.exists() && !isInFileList (classpath, file)) {
				classpath.add (file);
			}
		}

		for (File file : getRamcClasspath ()) {
			if (!isInFileList (classpath, file)) {
				classpath.add (file);
			}
		}
		return classpath;
	}

	private ArrayList<File> getCompilationPaths() throws IOException {
		ArrayList<File> compilationpath = new ArrayList<> ();

		for (Project project : Modelio.getInstance().getModelingSession().findByClass(Project.class)) {
		    StringTokenizer st;
		    String mainCompilationPath = JavaDesignerUtils.getCompilationPath(project.getModel(), this.module).getAbsolutePath();
		    st = new StringTokenizer(mainCompilationPath, ";"); //$NON-NLS-1$
		    while (st.hasMoreTokens ()) {
		        File file = new File (st.nextToken ());
		        if (file.exists() && !isInFileList (compilationpath, file)) {
		            compilationpath.add (file);
		        }
		    }
		}
		return compilationpath;
	}

	private LocalReverseMode getRetrieveBehavior(File currentFile) {
		ReverseDialogRunnable r = new ReverseDialogRunnable (currentFile.getAbsolutePath ());
		Display.getDefault ().syncExec (r);
		return r.getResult ();
	}

	private ElementType getTypePath(File file) {
		ElementStatus.ElementType type;

		if (file.getAbsolutePath ().endsWith (".java")) { //$NON-NLS-1$
			type = ElementStatus.ElementType.JAVA_FILE;
		} else if (file.getAbsolutePath ().endsWith (".class")) { //$NON-NLS-1$
			type = ElementStatus.ElementType.CLASS_FILE;
		} else {
			type = ElementStatus.ElementType.DIRECTORY;
		}
		return type;
	}

	private boolean isInFileList(List<File> list, File file) throws IOException {
		String filename;
		filename = file.getCanonicalPath();
		for (File f : list) {
			if (f.getCanonicalPath().equals(filename)) {
				return true;
			}
		}
		return false;
	}

	private void reverseInModelDrivenMode() {
		IModelingSession session = this.module.getModelingSession ();
		try (ITransaction transaction = session.createTransaction (Messages.getString ("Info.Session.Reverse"))) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			dialog.open();
			dialog.run(true, true, new MDReverseProgressBar (this.module, this.elementsToReverse, this.report));
			transaction.commit();
		} catch (InvalidTransactionException e) {
			// Transaction rollbacked by audit. 
		} catch (Exception e) {
			JavaDesignerModule.logService.error(e);
		}
	}

	private void reverseInRoundtripMode(final boolean withWizard, ReverseType reverseType) {
		final IModelingSession session = this.module.getModelingSession ();

		boolean error = false;
		File file;

		File containerFile = new File (this.module.getConfiguration ().getModuleResourcesPath () + File.separator + "bin" + File.separator + "containers.xml"); //$NON-NLS-1$//$NON-NLS-2$

		try {
			File outputFile = File.createTempFile ("java", ".xml"); //$NON-NLS-1$//$NON-NLS-2$
			outputFile.deleteOnExit ();
			if (withWizard) {
				// Reverse with wizard
				// Build the elements to reverse
				try (ITransaction transaction = session.createTransaction (Messages.getString ("Info.Session.Reverse"))) {

					ReverseConfig config = new ReverseConfig (new Hashtable<String, ElementStatus> (), new ArrayList<File>(), new ArrayList<File>(), reverseType, containerFile, outputFile);
					config.setReport (this.report);
					config.setStrategyConfiguration (new ReverseStrategyConfiguration (this.module.getConfiguration ()));

					// Store the reverse root
					if (this.elementsToReverse.size () == 1) {
						config.setReverseRoot (this.elementsToReverse.iterator ().next ());
					} else {
						config.setReverseRoot (getFirstRootPackage());
					}

					List<String> extensions = new ArrayList<>();

					String modulePath = this.module.getConfiguration().getModuleResourcesPath().toAbsolutePath().toString();
					ImageManager.setModulePath(modulePath);

					IClasspathModel classpathModel = createClasspathModel();
					IExternalJarsModel externalJarsClasspathModel = new ExternalJarsClasspathModel();

					if (reverseType == ReverseType.SOURCE) {
						// Source reverse with wizard
						extensions.add(".java");

						IFileChooserModel fileChooserModel = new JavaFileChooserModel(this.module.getConfiguration().getProjectSpacePath().toFile(), extensions, config);

						JavaReverseWizardView rw = new JavaReverseWizardView(Display.getDefault().getActiveShell(),
								fileChooserModel,
								classpathModel,
								externalJarsClasspathModel,
								false);
						int ret = rw.open();
						if (ret == 0) {
							config.setReverseMode(fileChooserModel.getGranularity());

							// Set up classpath
							config.getClasspath().addAll (classpathModel.getClasspath());
							config.getClasspath().addAll (externalJarsClasspathModel.getClasspath());
							config.getClasspath().addAll (getCompilationPaths ());

							// Set up source path for namespace lookup
							config.getSourcepath().add(fileChooserModel.getInitialDirectory());
							config.getSourcepath().addAll(fileChooserModel.getReverseRoots());

							config.setFilesToReverse(new Hashtable<String, ElementStatus>());
							for (File f : fileChooserModel.getFilesToImport()) {
								if (f.isDirectory()) {
									config.getFilesToReverse().put (f.getAbsolutePath(), new ElementStatus(f.getAbsolutePath(), ElementType.DIRECTORY, ReverseStatus.REVERSE));
								} else {
									config.getFilesToReverse().put (f.getAbsolutePath(), new ElementStatus(f.getAbsolutePath(), ElementType.JAVA_FILE, ReverseStatus.REVERSE));
								}
							}

							if (processRun (config)) {
								JavaDesignerModule.logService.info ("Commit start at " +
										Calendar.getInstance ().getTime ().toGMTString ());
								transaction.commit();
								JavaDesignerModule.logService.info ("Commit end at " +
										Calendar.getInstance ().getTime ().toGMTString ());
							}
						}
					} else {
						// Binary reverse from wizard

						// Set up classpath
						config.getClasspath().addAll (getClassPath());

						extensions.add(".jar");

						IFileChooserModel fileChooserModel = new JavaFileChooserModel(this.module.getConfiguration().getProjectSpacePath().toFile(), extensions, config);

						JavaReverseWizardView rw = new JavaReverseWizardView(Display.getDefault().getActiveShell(),
								fileChooserModel,
								classpathModel,
								externalJarsClasspathModel,
								true);
						int ret = rw.open();
						if (ret == 0) {
							// Do reverse
							config.setReverseMode(fileChooserModel.getGranularity());

							config.setModel (fileChooserModel.getAssemblyContentModel());
							config.setFilteredElements (fileChooserModel.getResult());

							processRun (config);

							JavaDesignerModule.logService.info ("Commit start at " + Calendar.getInstance ().getTime ().toGMTString ());
							transaction.commit();
							JavaDesignerModule.logService.info ("Commit end at " + Calendar.getInstance ().getTime ().toGMTString ());
						}
					}
				} catch (InvalidTransactionException e) {
					// Error during the commit, the rollback is already done
				} catch (Exception e) {
					JavaDesignerModule.logService.error(e);
				}
			} else {
				// Real round trip mode (no wizard)
				if (!this.elementsToReverse.isEmpty ()) {
					Hashtable<String, ElementStatus> filesToReverse = new Hashtable<> ();
					List<File> classpath = getClassPath ();
					List<File> sourcepath = new ArrayList<>();

					for (Project project : Modelio.getInstance().getModelingSession().findByClass(Project.class)) {
					    File applicationPath = JavaDesignerUtils.getGenerationPath (project.getModel(), this.module);
					    sourcepath.add(applicationPath);
					}

					// Add all components
					for (Component javaComponent : JavaDesignerUtils.getJavaComponents (this.module.getModelingSession())) {
						File path = JavaDesignerUtils.getGenerationPath (javaComponent, this.module);
						if (path.exists() && !isInFileList (sourcepath, path)) {
							sourcepath.add (path);
						}
					}

					// Build the elements to reverse
					try (ITransaction transaction = session.createTransaction (Messages.getString ("Info.Session.Reverse"))) {
						Set<NameSpace> reverseRoot = new HashSet<> ();

						for (NameSpace element : this.elementsToReverse) {
							// Compute the reverse root for this element, can be the root package, a "simple" package or a
							// component
							ModelTree namespaceRoot = element.getOwner ();
							while (namespaceRoot != null &&
									namespaceRoot.getOwner() != null &&
									JavaDesignerUtils.isJavaElement (namespaceRoot) &&
									!JavaDesignerUtils.isAJavaComponent (namespaceRoot) &&
									!JavaDesignerUtils.isAModule (namespaceRoot)) {
								namespaceRoot = namespaceRoot.getOwner ();
							}

							if (namespaceRoot != null) {
								reverseRoot.add ((NameSpace) namespaceRoot);
							}

							file = JavaDesignerUtils.getFilename (element, this.module);
							ElementStatus eStatus = filesToReverse.get (file.getAbsolutePath ());
							if (eStatus == null) {
								eStatus = new ElementStatus (file.getAbsolutePath (), getTypePath (file), ElementStatus.ReverseStatus.REVERSE);
								filesToReverse.put (file.getAbsolutePath (), eStatus);
							}

							JavaDesignerUtils.updateDate (this.module.getModelingSession(), element, Calendar.getInstance ().getTimeInMillis ());
						}
						ReverseConfig config = new ReverseConfig (filesToReverse,
								sourcepath,
								classpath, 
								reverseType, 
								containerFile, 
								outputFile);

						config.setReport (this.report);
						config.setStrategyConfiguration (new ReverseStrategyConfiguration (this.module.getConfiguration ()));

						// Store the reverse root
						if (reverseRoot.size () == 1) {
							config.setReverseRoot (reverseRoot.iterator ().next ());
						} else {
							config.setReverseRoot (getFirstRootPackage());
						}

						final RTReverseProgressBar progressBar = new RTReverseProgressBar (this.module, config);

						class JavaRunnable
						implements Runnable
						{
							public Exception lastException = null;

							@Override
							public void run ()
							{
								try {
									ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
									dialog.open();
									dialog.run(true, true, progressBar);
								} catch (Exception e) {
									this.lastException = e;
								}
							}

						}


						JavaRunnable r = new JavaRunnable ();
						Display.getDefault ().syncExec (r);

						JavaDesignerModule.logService.info ("Commit start at " + Calendar.getInstance ().getTime ().toGMTString ());

						if (r.lastException != null) {
							error = true;
						}

						if (!error) {
							transaction.commit();
	                        JavaDesignerModule.logService.info ("Commit end at " + Calendar.getInstance ().getTime ().toGMTString ());
						} else {
							transaction.rollback();
	                        JavaDesignerModule.logService.info ("Transaction rollbacked at " + Calendar.getInstance ().getTime ().toGMTString ());
						}
					}
				}
			}
		} catch (IOException e) {
			JavaDesignerModule.logService.error(e);
		}
	}

    /**
	 * Puts back the model change handler.
	 */
	private void addModelhandler() {
		if (this.javaDesignerSession != null) {
			IModelingSession session = this.module.getModelingSession ();
			session.addModelHandler (this.javaDesignerSession.getModelChangeHandler ());
		}
	}

	/**
	 * Removes the model change handler for the reverse to avoid attribute visibility to be changed.
	 */
	private void removeModelHandler() {
		// 
		if (this.javaDesignerSession != null) {
			IModelingSession session = this.module.getModelingSession ();
			session.removeModelHandler (this.javaDesignerSession.getModelChangeHandler ());
		}
	}

	/**
	 * This operation returns the class path for all deployed ramcs
	 */
	private Vector<File> getRamcClasspath() {
		Vector<File> classpath = new Vector<> ();
		File compilationPath;
		File completeCompilationPath;

		// Retrieve all ramc artifact
		for (ModelTree modeltree : Modelio.getInstance().getModelingSession().findByClass(Component.class)) {
			Component component = (Component) modeltree;
			if (component.isStereotyped (IOtherProfileElements.MODULE_NAME, IOtherProfileElements.MODEL_COMPONENT)) { //$NON-NLS-1$
				for (ModelTree obArtifact : component.getOwnedElement (Artifact.class)) {
					Artifact artifact = (Artifact) obArtifact;
					if (artifact.isStereotyped (IOtherProfileElements.MODULE_NAME, IOtherProfileElements.MODEL_COMPONENT_ARCHIVE) && ModelUtils.isLibrary(artifact)) { //$NON-NLS-1$
						compilationPath = JavaDesignerUtils.getCompilationPath (artifact, this.module);

						completeCompilationPath = new File (compilationPath +
								File.separator +
								".." + File.separator + JavaDesignerUtils.getJavaName (artifact) + File.separator + "bin" + File.separator + JavaDesignerUtils.getJavaName (artifact) + ".jar"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						if (completeCompilationPath.exists ()) {
							classpath.add (completeCompilationPath);
						} else {
							completeCompilationPath = new File (compilationPath +
									File.separator +
									".." + File.separator + JavaDesignerUtils.getJavaName (artifact) + File.separator + "lib" + File.separator + JavaDesignerUtils.getJavaName (artifact) + ".jar"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							if (completeCompilationPath.exists ()) {
								classpath.add (completeCompilationPath);
							} else {
								completeCompilationPath = new File (JavaDesignerUtils.getGenRoot (this.module), File.separator + "lib" + File.separator + JavaDesignerUtils.getJavaName (artifact) + ".jar"); //$NON-NLS-1$ //$NON-NLS-2$
								if (completeCompilationPath.exists ()) {
									classpath.add (completeCompilationPath);
								} else {
								    // Ignore missing jars
								}
							}
						}
					}
				}
			}
		}
		return classpath;
	}

	private IClasspathModel createClasspathModel() throws IOException {
		IClasspathModel classpathModel = new JavaClasspathModel(new File (this.module.getConfiguration().getProjectSpacePath().toFile(), "lib"));

		List<File> currentClasspath = classpathModel.getClasspath();
		currentClasspath.addAll(getClassPath());
		return classpathModel;
	}

	private boolean processRun(ReverseConfig config) {
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			dialog.open();
			dialog.run(true, true, new RTReverseProgressBar (this.module, config));
			return true;
		} catch (InvocationTargetException e) {
			JavaDesignerModule.logService.error(e);
			return false;
		} catch (InterruptedException e) {
			JavaDesignerModule.logService.error(e);
			return false;
		}
	}

	private class ReverseDialogRunnable implements Runnable {
		int result = 0;

		String path;


		public ReverseDialogRunnable(String path) {
			this.path = path;
		}

		@Override
		public void run() {
			MessageDialog dialog = new MessageDialog (Display.getDefault ().getActiveShell (), Messages.getString ("Gui.AskForReverseBoxTitle"), null, Messages.getString ("Gui.AskForReverseBoxLabel", this.path), MessageDialog.QUESTION, new String[] { //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString ("Gui.RetrieveButton"), //$NON-NLS-1$
				Messages.getString ("Gui.RetrieveAllButton"), //$NON-NLS-1$
				Messages.getString ("Gui.KeepButton"), //$NON-NLS-1$
				Messages.getString ("Gui.KeepAllButton"),
				Messages.getString ("Gui.CancelButton")}, 0); //$NON-NLS-1$
			this.result = dialog.open ();
		}

		public LocalReverseMode getResult() {
			switch (this.result) {
			case 0:
				return LocalReverseMode.RETRIEVE;
			case 1:
				return LocalReverseMode.RETRIEVE_ALL;
			case 2:
				return LocalReverseMode.KEEP;
			case 3:
				return LocalReverseMode.KEEP_ALL;
			default:
				return LocalReverseMode.CANCEL;
			}
		}

	}

	enum LocalReverseMode {
		RETRIEVE,
		RETRIEVE_ALL,
		KEEP,
		KEEP_ALL,
		CANCEL;
	}


	private Package getFirstRootPackage() {
	    for (Project project : Modelio.getInstance().getModelingSession().findByClass(Project.class)) {
	        return project.getModel();
	    }
	    return null;
    }
}
