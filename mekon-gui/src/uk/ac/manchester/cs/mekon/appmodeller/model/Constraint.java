package uk.ac.manchester.cs.mekon.appmodeller.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class Constraint {

	private ConstraintType type;

	private Concept sourceValue;
	private Set<Concept> targetValues;

	private class TargetValueRemovalHandler implements ConceptListener {

		public void onConceptMoved(Concept concept) {
		}

		public void onConceptRemoved(Concept concept) {

			targetValues.remove(concept);

			if (targetValues.isEmpty()) {

				remove();
			}
		}

		public void onChildAdded(Concept child) {
		}

		TargetValueRemovalHandler() {

			for (Concept targetValue : targetValues) {

				targetValue.addListener(this);
			}
		}
	}

	public Constraint(
				ConstraintType type,
				Concept sourceValue,
				Collection<Concept> targetValues) {

		this.type = type;
		this.sourceValue = sourceValue;
		this.targetValues = new HashSet<Concept>(targetValues);

		checkTargetConflicts();

		new TargetValueRemovalHandler();
	}

	public void remove() {

		sourceValue.removeConstraint(Constraint.this);
	}

	public ConstraintType getType() {

		return type;
	}

	public Concept getSourceValue() {

		return sourceValue;
	}

	public Set<Concept> getTargetValues() {

		return new HashSet<Concept>(targetValues);
	}

	boolean hasType(ConstraintType testType) {

		return testType.equals(type);
	}

	private void checkTargetConflicts() {

		for (Concept value1 : targetValues) {

			for (Concept value2 : targetValues) {

				if (value1 != value2 && value1.descendantOf(value2)) {

					throw new RuntimeException(
								"Conflicting target-values: "
								+ value1 + " descendant-of " + value2);
				}
			}
		}
	}
}
