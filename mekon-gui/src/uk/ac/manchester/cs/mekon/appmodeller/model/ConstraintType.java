package uk.ac.manchester.cs.mekon.appmodeller.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class ConstraintType {

	private EntityId focusConceptId;

	private Link sourceLink;
	private Link targetLink;

	public ConstraintType(EntityId focusConceptId, Link sourceLink, Link targetLink) {

		this.focusConceptId = focusConceptId;
		this.sourceLink = sourceLink;
		this.targetLink = targetLink;
	}

	public EntityId getFocusConceptId() {

		return focusConceptId;
	}

	public Link getSourceLink() {

		return sourceLink;
	}

	public Link getTargetLink() {

		return targetLink;
	}

	Constraint createRootConstraint() {

		return new Constraint(this, sourceLink.getValue(), targetValueAsList());
	}

	Constraint createConstraint(Concept sourceValue, Collection<Concept> targetValues) {

		sourceLink.checkSubValue(sourceValue);

		for (Concept targetValue : targetValues) {

			targetLink.checkSubValue(targetValue);
		}

		return new Constraint(this, sourceValue, targetValues);
	}

	Link deriveSubSourceLink(Concept value) {

		return sourceLink.deriveSubLink(value);
	}

	Link deriveSubTargetLink(Concept value) {

		return targetLink.deriveSubLink(value);
	}

	private List<Concept> targetValueAsList() {

		return Collections.singletonList(targetLink.getValue());
	}
}
