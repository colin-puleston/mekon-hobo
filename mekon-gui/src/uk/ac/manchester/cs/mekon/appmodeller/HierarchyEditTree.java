/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */

class HierarchyEditTree extends ConceptTree {

	static private final long serialVersionUID = -1;

	private ConceptMover conceptMover;

	HierarchyEditTree(Hierarchy hierarchy, ConceptMover conceptMover) {

		this.conceptMover = conceptMover;

		initialise(hierarchy.getRoot());
	}

	ConceptCellDisplay getConceptDisplay(Concept concept) {

		return conceptMover.movingConcept(concept)
				? ConceptCellDisplay.FULL_HIERARCHY_MOVE_SUBJECT
				: ConceptCellDisplay.FULL_HIERARCHY_DEFAULT;
	}

	void update() {

		GNode selected = getSelectedNode();

		if (selected != null) {

			TreePath selectedPath = selected.getTreePath();

			setSelectionPath(null);
			setSelectionPath(selectedPath);
		}

		updateAllNodeDisplays();
	}
}