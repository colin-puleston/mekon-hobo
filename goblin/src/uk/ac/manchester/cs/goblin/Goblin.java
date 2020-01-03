/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import java.util.*;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class Goblin extends GFrame {

	static private final long serialVersionUID = -1;

	static final String SIMPLE_TITLE = "GOBLIN OWL-Editor";

	static private final String COMPOUND_TITLE_FORMAT = SIMPLE_TITLE + ": %s";

	static private final String LOAD_BUTTON_LABEL = "Load...";
	static private final String SAVE_BUTTON_LABEL = "Save";
	static private final String SAVE_AS_BUTTON_LABEL = "Save As...";
	static private final String EXIT_BUTTON_LABEL = "Exit";

	static private final String UNDO_BUTTON_LABEL = "Undo";
	static private final String REDO_BUTTON_LABEL = "Redo";

	static private final int FRAME_WIDTH = 1200;
	static private final int FRAME_HEIGHT = 700;

	private ModelHandler modelHandler;
	private ModelEditPanel modelEditPanel;

	private class ModelEditPanel extends ConceptTreesPanel<Hierarchy> {

		static private final long serialVersionUID = -1;

		ModelEditPanel() {

			super(JTabbedPane.LEFT);

			setFont(GFonts.toMedium(getFont()));

			populate();
		}

		List<Hierarchy> getSources() {

			return getCurrentModel().getHierarchies();
		}

		Concept getRootConcept(Hierarchy hierarchy) {

			return hierarchy.getRoot();
		}

		JComponent createComponent(Hierarchy hierarchy) {

			return new HierarchyPanel(hierarchy);
		}

		void makeEditVisible(EditLocation location) {

			int hierarchyIdx = makeHierarchyVisible(location);

			if (location.constraintEdit()) {

				Constraint constraint = location.getEditedConstraint();

				getHierarchyPanel(hierarchyIdx).makeConstraintVisible(constraint);
			}
		}

		private int makeHierarchyVisible(EditLocation location) {

			return makeSourceVisible(location.getPrimaryEditHierarchy().getRoot());
		}

		private HierarchyPanel getHierarchyPanel(int hierarchyIdx) {

			return (HierarchyPanel)getComponentAt(hierarchyIdx);
		}
	}

	private abstract class EditsEnabledButton extends GButton {

		static private final long serialVersionUID = -1;

		private class Enabler implements ModelEditListener {

			public void onEdit() {

				Model model = getCurrentModel();

				setEnabled(redoDependent() ? model.canRedo() : model.canUndo());
			}

			Enabler() {

				modelHandler.addEditListener(this);
			}
		}

		EditsEnabledButton(String label) {

			super(label);

			setEnabled(false);

			new Enabler();
		}

		boolean redoDependent() {

			return false;
		}
	}

	private class LoadButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelHandler.loadNew();

			displayNewModel();
		}

		LoadButton() {

			super(LOAD_BUTTON_LABEL);
		}
	}

	private class SaveButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (modelHandler.checkSave()) {

				setEnabled(false);
			}
		}

		SaveButton() {

			super(SAVE_BUTTON_LABEL);
		}
	}

	private class SaveAsButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelHandler.saveAs();
		}

		SaveAsButton() {

			super(SAVE_AS_BUTTON_LABEL);
		}
	}

	private class ExitButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelHandler.checkSaveOnExit();

			dispose();
		}

		ExitButton() {

			super(EXIT_BUTTON_LABEL);
		}
	}

	private class UndoButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelEditPanel.makeEditVisible(getCurrentModel().undo());
		}

		UndoButton() {

			super(UNDO_BUTTON_LABEL);
		}
	}

	private class RedoButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelEditPanel.makeEditVisible(getCurrentModel().redo());
		}

		RedoButton() {

			super(REDO_BUTTON_LABEL);
		}

		boolean redoDependent() {

			return true;
		}
	}

	static public void main(String[] args) {

		new Goblin(getTitle(args));
	}

	static private String getTitle(String[] args) {

		return args.length == 1 ? createCompoundTitle(args[0]) : SIMPLE_TITLE;
	}

	static private String createCompoundTitle(String subTitle) {

		return String.format(COMPOUND_TITLE_FORMAT, subTitle);
	}

	public Goblin(String title) {

		super(title, FRAME_WIDTH, FRAME_HEIGHT);

		modelHandler = new ModelHandler(this);
		modelEditPanel = new ModelEditPanel();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		display(createMainPanel());
	}

	private void displayNewModel() {

		modelEditPanel.repopulate();
	}

	private JComponent createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(modelEditPanel, BorderLayout.CENTER);
		panel.add(createExternalActionsPanel(), BorderLayout.NORTH);

		return panel;
	}

	private JComponent createExternalActionsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(LineBorder.createGrayLineBorder());
		panel.add(createReEditActionButtons(), BorderLayout.WEST);
		panel.add(createExternalActionButtons(), BorderLayout.EAST);

		return panel;
	}

	private JComponent createReEditActionButtons() {

		return ControlsPanel.horizontal(new UndoButton(), new RedoButton());
	}

	private JComponent createExternalActionButtons() {

		return ControlsPanel.horizontal(
					new LoadButton(),
					new SaveButton(),
					new SaveAsButton(),
					new ExitButton());
	}

	private Model getCurrentModel() {

		return modelHandler.getModel();
	}
}
