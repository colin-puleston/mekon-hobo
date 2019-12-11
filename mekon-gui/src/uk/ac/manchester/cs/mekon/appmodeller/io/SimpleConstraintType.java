package uk.ac.manchester.cs.mekon.appmodeller.io;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

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
