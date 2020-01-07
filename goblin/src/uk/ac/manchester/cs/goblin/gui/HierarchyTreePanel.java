/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyTreePanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String ADD_LABEL = "Add...";
	static private final String REMOVE_LABEL = "Del";
	static private final String CUT_LABEL = "Mv-";
	static private final String STOP_CUT_LABEL = "Mv!";
	static private final String PASTE_LABEL = "Mv+";
	static private final String RESET_ID_LABEL = "Id...";

	private Model model;
	private HierarchyTree tree;

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

	private class ResetIdButton extends EditButton {

		static private final long serialVersionUID = -1;

		ResetIdButton() {

			super(RESET_ID_LABEL);
		}

		boolean enableIfRootSelected() {

			return false;
		}

		void doConceptEdit(Concept concept) {

			if (checkResetConceptId(concept)) {

				tree.update();
			}
		}
	}

	HierarchyTreePanel(Hierarchy hierarchy) {

		super(new BorderLayout());

		model = hierarchy.getModel();
		tree = new HierarchyTree(hierarchy, conceptMover);

		add(new JScrollPane(tree), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);
	}

	HierarchyTree getTree() {

		return tree;
	}

	private JComponent createButtonsPanel() {

		return ControlsPanel.horizontal(
					new AddButton(),
					new RemoveButton(),
					new CutButton(),
					new PasteButton(),
					new StopCutButton(),
					new ResetIdButton());
	}

	private void checkAddConcept(Concept parent) {

		EntityIdSpec idSpec = checkObtainConceptIdSpec(null);

		if (idSpec != null) {

			if (model.contentConceptExists(idSpec)) {

				showConceptAlreadyExistsMessage(idSpec);
			}
			else {

				parent.addChild(idSpec);
			}
		}
	}

	private boolean checkResetConceptId(Concept concept) {

		EntityIdSpec currentIdSpec = concept.getConceptId().toSpec();
		EntityIdSpec newIdSpec = checkObtainConceptIdSpec(currentIdSpec);

		if (newIdSpec != null) {

			if (concept.resetId(newIdSpec)) {

				return true;
			}

			showConceptAlreadyExistsMessage(newIdSpec);
		}

		return false;
	}

	private void checkRemoveConcept(Concept concept) {

		if (obtainConceptRemovalConfirmation(concept)) {

			concept.remove();
		}
	}

	private EntityIdSpec checkObtainConceptIdSpec(EntityIdSpec currentIdSpec) {

		return new ConceptIdSelector(this, currentIdSpec).getSelection();
	}

	private void showConceptAlreadyExistsMessage(EntityIdSpec idSpec) {

		InfoDisplay.inform("Concept already exists: " + idSpec.getName());
	}

	private boolean obtainConceptRemovalConfirmation(Concept concept) {

		String msg = "Removing concept";

		if (!concept.isLeaf()) {

			msg += " and all descendant-concepts";
		}

		return InfoDisplay.checkContinue(msg + ": " + concept.getConceptId());
	}
}
