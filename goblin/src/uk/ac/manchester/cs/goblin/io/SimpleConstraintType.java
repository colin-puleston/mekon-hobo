package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class SimpleConstraintType extends ConstraintType {

	private EntityId linkingPropertyId;

	SimpleConstraintType(
		EntityId linkingPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		this.linkingPropertyId = linkingPropertyId;
	}

	EntityId getLinkingPropertyId() {

		return linkingPropertyId;
	}
}
