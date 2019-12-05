/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyEditPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String ADD_LABEL = "Add...";
	static private final String REMOVE_LABEL = "Del";
	static private final String CUT_LABEL = "Mv-";
	static private final String STOP_CUT_LABEL = "Mv!";
	static private final String PASTE_LABEL = "Mv+";
	static private final String RENAME_LABEL = "Ren...";

	private Model model;
	private HierarchyEditTree tree;

	private ConceptMover conceptMover = new ConceptMover();

	private abstract class EditButton extends ConceptTreeSelectionDependentButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			doConceptEdit(tree.getSelectedConcept());
		}

		EditButton(String label) {

			super(label, tree);
		}

		boolean enableOnSelectedConcept(Concept selection) {

			return enableForRootSelection(selection) && enableForMoveInProgress(selection);
		}

		boolean enableIfRootSelected() {

			return true;
		}

		boolean enableForMoveInProgress(Concept selection) {

			return !conceptMover.moveInProgress();
		}

		abstract void doConceptEdit(Concept concept);

		private boolean enableForRootSelection(Concept selection) {

			return enableIfRootSelected() || !selection.isRoot();
		}
	}

	private class AddButton extends EditButton {

		static private final long serialVersionUID = -1;

		AddButton() {

			super(ADD_LABEL);
		}

		void doConceptEdit(Concept concept) {

			checkAddConcept(concept);
		}
	}

	private class RemoveButton extends EditButton {

		static private final long serialVersionUID = -1;

		RemoveButton() {

			super(REMOVE_LABEL);
		}

		boolean enableIfRootSelected() {

			return false;
		}

		void doConceptEdit(Concept concept) {

			checkRemoveConcept(concept);
		}
	}

	private class CutButton extends EditButton {

		static private final long serialVersionUID = -1;

		CutButton() {

			super(CUT_LABEL);
		}

		boolean enableIfRootSelected() {

			return false;
		}

		void doConceptEdit(Concept concept) {

			conceptMover.startMove(concept);

			tree.update();
		}
	}

	private class StopCutButton extends EditButton {

		static private final long serialVersionUID = -1;

		StopCutButton() {

			super(STOP_CUT_LABEL);
		}

		boolean enableOnNoSelection() {

			return conceptMover.moveInProgress();
		}

		boolean enableForMoveInProgress(Concept selection) {

			return conceptMover.moveInProgress();
		}

		void doConceptEdit(Concept concept) {

			conceptMover.abortMove();

			tree.update();
		}
	}

	private class PasteButton extends EditButton {

		static private final long serialVersionUID = -1;

		PasteButton() {

			super(PASTE_LABEL);
		}

		boolean enableForMoveInProgress(Concept selection) {

			return conceptMover.newParentCandidate(selection);
		}

		void doConceptEdit(Concept concept) {

			conceptMover.completeMove(concept);

			tree.update();
		}
	}

	private class RenameButton extends EditButton {

		static private final long serialVersionUID = -1;

		RenameButton() {

			super(RENAME_LABEL);
		}

		boolean enableIfRootSelected() {

			return false;
		}

		void doConceptEdit(Concept concept) {

			checkRenameConcept(concept);

			tree.update();
		}
	}

	HierarchyEditPanel(Hierarchy hierarchy) {

		super(new BorderLayout());

		model = hierarchy.getModel();
		tree = new HierarchyEditTree(hierarchy, conceptMover);

		add(new JScrollPane(tree), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);
	}

	HierarchyEditTree getTree() {

		return tree;
	}

	private JComponent createButtonsPanel() {

		return ControlsPanel.horizontal(
					new AddButton(),
					new RemoveButton(),
					new CutButton(),
					new PasteButton(),
					new StopCutButton(),
					new RenameButton());
	}

	private void checkAddConcept(Concept parent) {

		String name = checkObtainNewConceptName();

		if (name != null) {

			parent.addChild(name);
		}
	}

	private void checkRenameConcept(Concept concept) {

		String name = checkObtainNewConceptName();

		if (name != null) {

			concept.rename(name);
		}
	}

	private void checkRemoveConcept(Concept concept) {

		if (obtainConceptRemovalConfirmation(concept)) {

			concept.remove();
		}
	}

	private String checkObtainNewConceptName() {

		String name = new ConceptNameSelector(this).getSelection();

		if (name != null && model.isContentConcept(name)) {

			showConceptAlreadyExistsMessage(name);

			return null;
		}

		return name;
	}

	private void showConceptAlreadyExistsMessage(String name) {

		InfoDisplay.inform("Concept already exists: " + name);
	}

	private boolean obtainConceptRemovalConfirmation(Concept concept) {

		String msg = "Removing concept";

		if (!concept.isLeaf()) {

			msg += " and all descendant-concepts";
		}

		return InfoDisplay.checkContinue(msg + ": " + concept.getConceptId());
	}
}
