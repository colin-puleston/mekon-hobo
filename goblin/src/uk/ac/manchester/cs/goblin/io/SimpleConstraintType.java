package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class SimpleConstraintType extends ConstraintType {

	private EntityId linkingPropertyId;

	SimpleConstraintType(
		String name,
		EntityId linkingPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept,
		ConstraintSemantics semantics) {

		super(name, rootSourceConcept, rootTargetConcept, semantics);

		this.linkingPropertyId = linkingPropertyId;
	}

	EntityId getLinkingPropertyId() {

		return linkingPropertyId;
	}
}
