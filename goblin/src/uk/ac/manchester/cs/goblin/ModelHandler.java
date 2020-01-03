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

		File contentFile = checkContentFileSelection("Load");

		if (contentFile != null && loadFrom(contentFile)) {

			copyEditListenersToNewModel();
			resetEditCounts();
		}
	}

	boolean save() {

		if (unsavedEdits() && confirmOverwrite(serialiser.getContentFile())) {

			serialiser.save(model);
			resetEditCounts();

			return true;
		}

		return false;
	}

	void saveAs() {

		File contentFile = checkContentFileSelection("Save");

		if (contentFile != null) {

			if (!contentFile.exists() || confirmOverwrite(contentFile)) {

				serialiser.saveAs(model, contentFile);
				resetEditCounts();
			}
		}
	}

	boolean checkExit() {

		if (unsavedEdits()) {

			Confirmation confirm = confirmOverwriteAndExit(serialiser.getContentFile());

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

	private void resetEditCounts() {

		editCount = 0;
		undoCount = 0;
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

	private Confirmation confirmOverwriteAndExit(File contentFile) {

		return InfoDisplay.checkConfirmOrCancel(
					"Save unsaved model?",
					createOverwriteMessage(contentFile));
	}

	private boolean confirmOverwrite(File contentFile) {

		return InfoDisplay.checkContinue(createOverwriteMessage(contentFile));
	}

	private String createCannotStartMessage(String specificMsg) {

		return "Cannot start " + Goblin.SIMPLE_TITLE + ": " + specificMsg;
	}

	private String createCannotLoadContentFileMessage(String specificMsg) {

		return "Cannot load content-file: " + specificMsg;
	}

	private String createOverwriteMessage(File contentFile) {

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
