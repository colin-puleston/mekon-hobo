/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */

abstract class ConceptTreeSelectionDependentButton extends SelectionDependentButton<GNode> {

	static private final long serialVersionUID = -1;

	ConceptTreeSelectionDependentButton(String label, ConceptTree tree) {

		super(label);

		tree.addNodeSelectionListener(initialise());
	}

	boolean enableOnSelection(GNode selection) {

		return enableOnSelectedConcept(ConceptTree.extractConcept(selection));
	}

	boolean enableOnSelectedConcept(Concept selection) {

		return true;
	}
}
