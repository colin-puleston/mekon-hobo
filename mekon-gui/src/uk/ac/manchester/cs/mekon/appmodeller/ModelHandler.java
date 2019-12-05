/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;
import uk.ac.manchester.cs.mekon.appmodeller.io.*;

/**
 * @author Colin Puleston
 */
class ModelHandler {

	static private final String OWL_FILE_EXTN = "owl";
	static private final String OWL_FILE_SUFFIX = "." + OWL_FILE_EXTN;

	private Modeller modeller;

	private ModelSerialiser serialiser;
	private Model model;

	private boolean modelUpdates = false;

	private List<ModelListener> modelListeners = new ArrayList<ModelListener>();

	private class UpdateTracker implements ModelListener {

		public void onModelUpdate() {

			modelUpdates = true;
		}

		UpdateTracker() {

			addModelListener(this);
		}
	}

	ModelHandler(Modeller modeller) {

		this.modeller = modeller;

		serialiser = new ModelSerialiser();
		model = loadDefaultOrExit();

		model.setConfirmations(new UserConfirmations());

		new UpdateTracker();
	}

	void addModelListener(ModelListener listener) {

		modelListeners.add(listener);
		model.addListener(listener);
	}

	Model getModel() {

		return model;
	}

	void loadNew() {

		checkSaveModelUpdates();

		File contentFile = checkContentFileSelection("Load");

		if (contentFile != null && loadFrom(contentFile)) {

			copyModelListenersToNewModel();
			modelUpdates = false;
		}
	}

	boolean checkSave() {

		if (confirmOverwriteContentFile(serialiser.getContentFile())) {

			serialiser.save(model);
			modelUpdates = false;

			return true;
		}

		return false;
	}

	boolean saveAs() {

		File contentFile = checkContentFileSelection("Save");

		if (contentFile != null) {

			if (!contentFile.exists() || confirmOverwriteContentFile(contentFile)) {

				serialiser.saveAs(model, contentFile);
				modelUpdates = false;
			}

			return true;
		}

		return false;
	}

	void checkSaveOnExit() {

		checkSaveModelUpdates();
	}

	private Model loadDefaultOrExit() {

		try {

			return serialiser.load();
		}
		catch (BadContentOntologyException e) {

			System.out.println(createCannotStartMessage(e.getMessage()));
			System.exit(0);

			return null;
		}
	}

	private boolean loadFrom(File contentFile) {

		try {

			model = serialiser.loadFrom(contentFile);

			return true;
		}
		catch (BadContentOntologyException e) {

			informCannotLoadContentFile(e.getMessage());

			return false;
		}
	}

	private void checkSaveModelUpdates() {

		if (modelUpdates) {

			checkSave();
		}
	}

	private File checkContentFileSelection(String action) {

		JFileChooser chooser = createContentFileChooser();

		if (chooser.showDialog(modeller, action) == JFileChooser.APPROVE_OPTION) {

			return resolveContentFileSelection(chooser.getSelectedFile());
		}

		return null;
	}

	private JFileChooser createContentFileChooser() {

		JFileChooser chooser = new JFileChooser(getModelDir());

		chooser.setFileFilter(createContentFileFilter());

		return chooser;
	}

	private FileNameExtensionFilter createContentFileFilter() {

		return new FileNameExtensionFilter("New \"content\" file", OWL_FILE_EXTN);
	}

	private File resolveContentFileSelection(File file) {

		String name = file.getName();

		if (name.endsWith(OWL_FILE_SUFFIX)) {

			return file;
		}

		return new File(file.getParent(), name + OWL_FILE_SUFFIX);
	}

	private void informCannotLoadContentFile(String specificMsg) {

		InfoDisplay.inform(createCannotLoadContentFileMessage(specificMsg));
	}

	private boolean confirmOverwriteContentFile(File contentFile) {

		return InfoDisplay.checkContinue(createOverwriteContentFileMessage(contentFile));
	}

	private String createCannotStartMessage(String specificMsg) {

		return "Cannot start " + Modeller.SIMPLE_TITLE + ": " + specificMsg;
	}

	private String createCannotLoadContentFileMessage(String specificMsg) {

		return "Cannot load content-file: " + specificMsg;
	}

	private String createOverwriteContentFileMessage(File contentFile) {

		return "Save model to \"" + contentFile + "\": Overwrite current file?";
	}

	private void copyModelListenersToNewModel() {

		for (ModelListener listener : modelListeners) {

			model.addListener(listener);
		}
	}

	private File getModelDir() {

		return serialiser.getContentFile().getParentFile();
	}
}
