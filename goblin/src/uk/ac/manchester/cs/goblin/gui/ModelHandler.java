/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

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

	private int editCount = 0;
	private int undoCount = 0;

	private List<ModelEditListener> editListeners = new ArrayList<ModelEditListener>();

	private class EditCounter implements ModelEditListener {

		public void onEdit() {

			editCount++;
		}

		EditCounter() {

			addEditListener(this);
		}
	}

	ModelHandler(JFrame parentFrame) {

		this.parentFrame = parentFrame;

		serialiser = new ModelSerialiser();
		model = loadDefaultOrExit();

		initialiseModel();

		new EditCounter();
	}

	void addEditListener(ModelEditListener listener) {

		editListeners.add(listener);
		model.addEditListener(listener);
	}

	void loadNew() {

		save();

		File dynamicFile = checkDynamicFileSelection("Load");

		if (dynamicFile != null && loadFrom(dynamicFile)) {

			copyEditListenersToNewModel();
			resetEditCounts();
		}
	}

	boolean save() {

		if (unsavedEdits() && confirmOverwrite(serialiser.getDynamicFile())) {

			serialiser.save(model);
			resetEditCounts();

			return true;
		}

		return false;
	}

	void saveAs() {

		File dynamicFile = checkDynamicFileSelection("Save");

		if (dynamicFile != null) {

			if (!dynamicFile.exists() || confirmOverwrite(dynamicFile)) {

				serialiser.saveAs(model, dynamicFile);
				resetEditCounts();
			}
		}
	}

	boolean checkExit() {

		if (unsavedEdits()) {

			Confirmation confirm = confirmOverwriteAndExit(serialiser.getDynamicFile());

			if (confirm.cancel()) {

				return false;
			}

			if (confirm.yes()) {

				serialiser.save(model);
			}
		}

		return true;
	}

	EditLocation undo() {

		undoCount++;
		editCount--;

		return model.undo();
	}

	EditLocation redo() {

		undoCount--;
		editCount--;

		return model.redo();
	}

	Model getModel() {

		return model;
	}

	boolean unsavedEdits() {

		return editCount != undoCount;
	}

	private void initialiseModel() {

		model.setConfirmations(new UserConfirmations());
		model.startEditTracking();
	}

	private Model loadDefaultOrExit() {

		try {

			return serialiser.load();
		}
		catch (BadDynamicOntologyException e) {

			System.out.println(createCannotStartMessage(e.getMessage()));
			System.exit(0);

			return null;
		}
	}

	private boolean loadFrom(File dynamicFile) {

		try {

			model = serialiser.loadFrom(dynamicFile);

			initialiseModel();

			return true;
		}
		catch (BadDynamicOntologyException e) {

			informCannotLoadDynamicFile(e.getMessage());

			return false;
		}
	}

	private void resetEditCounts() {

		editCount = 0;
		undoCount = 0;
	}

	private File checkDynamicFileSelection(String action) {

		JFileChooser chooser = createDynamicFileChooser();

		if (chooser.showDialog(parentFrame, action) == JFileChooser.APPROVE_OPTION) {

			return resolveDynamicFileSelection(chooser.getSelectedFile());
		}

		return null;
	}

	private JFileChooser createDynamicFileChooser() {

		JFileChooser chooser = new JFileChooser(getModelDir());

		chooser.setFileFilter(createDynamicFileFilter());

		return chooser;
	}

	private FileNameExtensionFilter createDynamicFileFilter() {

		return new FileNameExtensionFilter("OWL ontology file", OWL_FILE_EXTN);
	}

	private File resolveDynamicFileSelection(File file) {

		String name = file.getName();

		if (name.endsWith(OWL_FILE_SUFFIX)) {

			return file;
		}

		return new File(file.getParent(), name + OWL_FILE_SUFFIX);
	}

	private void informCannotLoadDynamicFile(String specificMsg) {

		InfoDisplay.inform(createCannotLoadDynamicFileMessage(specificMsg));
	}

	private Confirmation confirmOverwriteAndExit(File dynamicFile) {

		return InfoDisplay.checkConfirmOrCancel(
					"Save unsaved model?",
					createOverwriteMessage(dynamicFile));
	}

	private boolean confirmOverwrite(File dynamicFile) {

		return InfoDisplay.checkContinue(createOverwriteMessage(dynamicFile));
	}

	private String createCannotStartMessage(String specificMsg) {

		return "Cannot start " + Goblin.SIMPLE_TITLE + ": " + specificMsg;
	}

	private String createCannotLoadDynamicFileMessage(String specificMsg) {

		return "Cannot load dynamic-ontology file: " + specificMsg;
	}

	private String createOverwriteMessage(File dynamicFile) {

		return "Save model to \"" + dynamicFile + "\": Overwrite current file?";
	}

	private void copyEditListenersToNewModel() {

		for (ModelEditListener listener : editListeners) {

			model.addEditListener(listener);
		}
	}

	private File getModelDir() {

		return serialiser.getDynamicFile().getParentFile();
	}
}
