/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.io.*;

/**
 * @author Colin Puleston
 */
class ModelHandler {

	static private final String OWL_FILE_EXTN = "owl";
	static private final String OWL_FILE_SUFFIX = "." + OWL_FILE_EXTN;

	private JFrame parentFrame;

	private ModelSerialiser serialiser;
	private Model model;

	private boolean modelUpdates = false;

	private List<ModelEditListener> editListeners = new ArrayList<ModelEditListener>();

	private class UpdateTracker implements ModelEditListener {

		public void onEdit() {

			modelUpdates = true;
		}

		UpdateTracker() {

			addEditListener(this);
		}
	}

	ModelHandler(JFrame parentFrame) {

		this.parentFrame = parentFrame;

		serialiser = new ModelSerialiser();
		model = loadDefaultOrExit();

		initialiseModel();

		new UpdateTracker();
	}

	void addEditListener(ModelEditListener listener) {

		editListeners.add(listener);
		model.addEditListener(listener);
	}

	void loadNew() {

		checkSaveModelUpdates();

		File contentFile = checkContentFileSelection("Load");

		if (contentFile != null && loadFrom(contentFile)) {

			copyEditListenersToNewModel();
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

	Model getModel() {

		return model;
	}

	private void initialiseModel() {

		model.setConfirmations(new UserConfirmations());
		model.startEditTracking();
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

			initialiseModel();

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

		if (chooser.showDialog(parentFrame, action) == JFileChooser.APPROVE_OPTION) {

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

		return "Cannot start " + Goblin.SIMPLE_TITLE + ": " + specificMsg;
	}

	private String createCannotLoadContentFileMessage(String specificMsg) {

		return "Cannot load content-file: " + specificMsg;
	}

	private String createOverwriteContentFileMessage(File contentFile) {

		return "Save model to \"" + contentFile + "\": Overwrite current file?";
	}

	private void copyEditListenersToNewModel() {

		for (ModelEditListener listener : editListeners) {

			model.addEditListener(listener);
		}
	}

	private File getModelDir() {

		return serialiser.getContentFile().getParentFile();
	}
}
