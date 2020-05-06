/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.goblin.gui;

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConstraintPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String EDIT_PANEL_TITLE_FORMAT = "%s targets";

	static private final String ADD_TARGETS_LABEL = "Add";
	static private final String REMOVE_TARGETS_LABEL = "Del";
	static private final String CLEAR_TARGETS_LABEL = "Clear";
	static private final String APPLY_EDITS_LABEL = "Apply edits";

	private ConstraintType type;
	private ConstraintSemantics semantics;

	private ConceptTree sourcesTree;

	private abstract class PanelPopulator {

		static private final long serialVersionUID = -1;

		final Concept source;
		final TargetsTree targetsTree;

		private class TargetsTree extends ConstraintTargetsTree {

			static private final long serialVersionUID = -1;

			GCellDisplay getConceptDisplay(Concept concept) {

				return getCellDisplay(concept).forConcept(concept);
			}
		}

		PanelPopulator(Concept source) {

			this.source = source;

			targetsTree = new TargetsTree();
		}

		void populate() {

			targetsTree.initialise(getPotentialValidValues());

			add(new ConceptTreeSelectorPanel(targetsTree), BorderLayout.NORTH);
			add(new JScrollPane(targetsTree), BorderLayout.CENTER);
		}

		void repopulate() {

			removeAll();
			populate();
			revalidate();
		}

		abstract Constraint getPotentialValidValues();

		GoblinCellDisplay getCellDisplay(Concept concept) {

			return GoblinCellDisplay.CONSTRAINTS_VALID_TARGET;
		}
	}

	private class DefaultPanelPopulator extends PanelPopulator {

		static private final long serialVersionUID = -1;

		DefaultPanelPopulator() {

			super(type.getRootSourceConcept());
		}

		Constraint getPotentialValidValues() {

			return source.lookForValidValuesConstraint(type);
		}
	}

	private abstract class EditPanelPopulator extends PanelPopulator {

		static private final long serialVersionUID = -1;

		final Map<Concept, Constraint> impliedValuesByTarget = new HashMap<Concept, Constraint>();

		private TargetSelectionsList targetSelectionsList = new TargetSelectionsList();

		private class TargetSelectionsList extends GList<Concept> {

			static private final long serialVersionUID = -1;

			TargetSelectionsList() {

				super(true, true);
			}

			void populate() {

				for (Constraint constraint : getEditConstraints(source)) {

					addTargets(constraint.getTargetValues());
				}
			}

			void addTarget(Concept target) {

				addEntity(target, createCellDisplay(target));
			}

			private void addTargets(Set<Concept> targets) {

				for (Concept target : targets) {

					addTarget(target);
				}
			}

			private GCellDisplay createCellDisplay(Concept target) {

				return getCellDisplay(target).forConcept(target);
			}
		}

		private class TargetAddButton extends ConceptTreeSelectionDependentButton {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				addTargetSelections(targetsTree.getAllSelectedConcepts());
				setEnabled(false);
			}

			TargetAddButton() {

				super(ADD_TARGETS_LABEL, targetsTree);
			}

			boolean enableOnSelectedConcept(Concept selection) {

				return !selection.isRoot() && !selectionTarget(selection);
			}
		}

		private class TargetRemoveButton extends ListSelectionDependentButton<Concept> {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				removeTargetSelections(targetSelectionsList.getSelectedEntities());
			}

			TargetRemoveButton() {

				super(REMOVE_TARGETS_LABEL, targetSelectionsList);
			}
		}

		private class TargetsClearButton extends GButton {

			static private final long serialVersionUID = -1;

			private class Enabler extends GListListener<Concept> {

				protected void onAdded(Concept entity) {

					setEnabled(true);
				}

				protected void onRemoved(Concept entity) {

					setEnabledIfAnyTargets();
				}

				Enabler() {

					setEnabledIfAnyTargets();

					targetSelectionsList.addListListener(this);
				}

				private void setEnabledIfAnyTargets() {

					setEnabled(targetSelectionsList.anyElements());
				}
			}

			protected void doButtonThing() {

				targetSelectionsList.clearList();
			}

			TargetsClearButton() {

				super(CLEAR_TARGETS_LABEL);

				new Enabler();
			}
		}

		private class ApplyEditsButton extends GButton {

			static private final long serialVersionUID = -1;

			private class Enabler extends GListListener<Concept> {

				protected void onAdded(Concept entity) {

					setEnabled(true);
				}

				protected void onRemoved(Concept entity) {

					setEnabled(true);
				}

				Enabler() {

					setEnabled(false);

					targetSelectionsList.addListListener(this);
				}
			}

			protected void doButtonThing() {

				applyEdits(source, getTargetSelections());
				sourcesTree.redisplayForConstraintsEdit();
			}

			ApplyEditsButton() {

				super(APPLY_EDITS_LABEL);

				new Enabler();
			}
		}

		EditPanelPopulator(Concept source) {

			super(source);

			for (Constraint constraint : source.getImpliedValueConstraints(type)) {

				impliedValuesByTarget.put(constraint.getTargetValue(), constraint);
			}
		}

		void populate() {

			super.populate();

			targetSelectionsList.populate();
			add(createActionsPanel(), BorderLayout.SOUTH);
		}

		GoblinCellDisplay getCellDisplay(Concept concept) {

			if (!validTargetConcept(concept)) {

				return GoblinCellDisplay.CONSTRAINTS_POTENTIAL_TARGET;
			}

			if (impliedValuesByTarget.keySet().contains(concept)) {

				return GoblinCellDisplay.CONSTRAINTS_IMPLIED_TARGET;
			}

			return GoblinCellDisplay.CONSTRAINTS_VALID_TARGET;
		}

		abstract Constraint getCurrentValidValues();

		abstract void applyEdits(Concept source, List<Concept> targets);

		private JPanel createActionsPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(new JScrollPane(targetSelectionsList), BorderLayout.CENTER);
			panel.add(createActionsInvocationPanel(), BorderLayout.SOUTH);

			return TitledPanels.setTitle(panel, createActionsPanelTitle());
		}

		private JComponent createActionsInvocationPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(createTargetsEditButtons(), BorderLayout.WEST);
			panel.add(new ApplyEditsButton(), BorderLayout.EAST);

			return panel;
		}

		private JComponent createTargetsEditButtons() {

			return ControlsPanel.horizontal(
						new TargetAddButton(),
						new TargetRemoveButton(),
						new TargetsClearButton());
		}

		private List<Constraint> getEditConstraints(Concept source) {

			return semantics.select(source.getConstraints(type));
		}

		private String createActionsPanelTitle() {

			return String.format(EDIT_PANEL_TITLE_FORMAT, semantics.getDisplayLabel());
		}

		private void addTargetSelections(List<Concept> newSelections) {

			for (Concept newSelection : newSelections) {

				addTargetSelection(newSelection);
			}
		}

		private void addTargetSelection(Concept newSelection) {

			for (Concept selection : getTargetSelections()) {

				if (conflictingConcepts(newSelection, selection)) {

					removeTargetSelection(selection);
				}
			}

			targetSelectionsList.addTarget(newSelection);
		}

		private void removeTargetSelections(List<Concept> selections) {

			for (Concept selection : selections) {

				removeTargetSelection(selection);
			}
		}

		private void removeTargetSelection(Concept selection) {

			targetSelectionsList.removeEntity(selection);
		}

		private List<Concept> getTargetSelections() {

			return targetSelectionsList.getEntityList();
		}

		private boolean selectionTarget(Concept concept) {

			return targetSelectionsList.containsEntity(concept);
		}

		private boolean conflictingConcepts(Concept concept1, Concept concept2) {

			return concept1.descendantOf(concept2) || concept2.descendantOf(concept1);
		}

		private boolean validTargetConcept(Concept concept) {

			return concept.subsumedByAny(getCurrentValidValues().getTargetValues());
		}
	}

	private class ValidValuesEditPanelPopulator extends EditPanelPopulator {

		private Constraint potentialValidValues;
		private Constraint localValidValues;

		ValidValuesEditPanelPopulator(Concept source) {

			super(source);

			potentialValidValues = source.getClosestAncestorValidValuesConstraint(type);
			localValidValues = source.lookForValidValuesConstraint(type);
		}

		Constraint getPotentialValidValues() {

			return potentialValidValues;
		}

		Constraint getCurrentValidValues() {

			return localValidValues != null ? localValidValues : potentialValidValues;
		}

		void applyEdits(Concept source, List<Concept> targets) {

			if (targets.isEmpty()) {

				if (localValidValues != null) {

					localValidValues.remove();
				}
			}
			else {

				source.addValidValuesConstraint(type, targets);
			}
		}
	}

	private class ImpliedValueEditPanelPopulator extends EditPanelPopulator {

		private Constraint validValues;

		ImpliedValueEditPanelPopulator(Concept source) {

			super(source);

			validValues = source.getClosestValidValuesConstraint(type);
		}

		Constraint getPotentialValidValues() {

			return validValues;
		}

		Constraint getCurrentValidValues() {

			return validValues;
		}

		void applyEdits(Concept source, List<Concept> targets) {

			for (Concept target : impliedValuesByTarget.keySet()) {

				if (!targets.contains(target)) {

					impliedValuesByTarget.get(target).remove();
				}
			}

			for (Concept target : targets) {

				if (!impliedValuesByTarget.keySet().contains(target)) {

					source.addImpliedValueConstraint(type, target);
				}
			}
		}
	}

	private class SourceConceptTracker extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			Concept selected = sourcesTree.getSelectedConcept();

			if (selected != null) {

				resetSourceConcept(selected);
			}
		}

		protected void onDeselected(GNode node) {

			clearSourceConcept();
		}
	}

	ConstraintPanel(
		ConstraintType type,
		ConstraintSemantics semantics,
		ConceptTree sourcesTree) {

		super(new BorderLayout());

		this.type = type;
		this.semantics = semantics;
		this.sourcesTree = sourcesTree;

		new DefaultPanelPopulator().populate();

		sourcesTree.addNodeSelectionListener(new SourceConceptTracker());
	}

	private void resetSourceConcept(Concept source) {

		createPanelPopulator(source).repopulate();
	}

	private void clearSourceConcept() {

		new DefaultPanelPopulator().repopulate();
	}

	private PanelPopulator createPanelPopulator(Concept source) {

		if (source.equals(type.getRootSourceConcept())) {

			return new DefaultPanelPopulator();
		}

		return semantics.validValues()
				? new ValidValuesEditPanelPopulator(source)
				: new ImpliedValueEditPanelPopulator(source);
	}
}
