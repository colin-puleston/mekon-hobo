/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */

class ConstraintTargetsTree extends ConceptTree {

	static private final long serialVersionUID = -1;

	private ConceptCellDisplay cellDisplay;

	ConstraintTargetsTree(Constraint constraint, ConceptCellDisplay cellDisplay) {

		this.cellDisplay = cellDisplay;

		initialise(constraint.getTargetValues());
	}

	ConceptCellDisplay getConceptDisplay(Concept concept) {

		return cellDisplay;
	}
}