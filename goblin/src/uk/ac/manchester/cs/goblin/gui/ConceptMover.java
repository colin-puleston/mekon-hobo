/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConceptMover {

	private Concept rootMoveConcept = null;

	void startMove(Concept rootMoveConcept) {

		this.rootMoveConcept = rootMoveConcept;
	}

	void completeMove(Concept newParent) {

		rootMoveConcept.move(newParent);

		rootMoveConcept = null;
	}

	void abortMove() {

		rootMoveConcept = null;
	}

	boolean moveInProgress() {

		return rootMoveConcept != null;
	}

	boolean newParentCandidate(Concept concept) {

		return moveInProgress() && !rootMoveParent(concept) && !movingConcept(concept);
	}

	boolean movingConcept(Concept concept) {

		return moveInProgress() && concept.subsumedBy(rootMoveConcept);
	}

	private boolean rootMoveParent(Concept concept) {

		return concept.equals(rootMoveConcept.getParent());
	}
}
