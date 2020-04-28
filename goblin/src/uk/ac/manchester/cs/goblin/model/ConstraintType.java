package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class ConstraintType {

	private String name;
	private Concept rootSourceConcept;
	private Concept rootTargetConcept;
	private ConstraintSemantics semantics;

	public String getName() {

		return name;
	}

	public Concept getRootSourceConcept() {

		return rootSourceConcept;
	}

	public Concept getRootTargetConcept() {

		return rootTargetConcept;
	}

	public ConstraintSemantics getSemantics() {

		return semantics;
	}

	protected ConstraintType(
				String name,
				Concept rootSourceConcept,
				Concept rootTargetConcept,
				ConstraintSemantics semantics) {

		this.name = name;
		this.rootSourceConcept = rootSourceConcept;
		this.rootTargetConcept = rootTargetConcept;
		this.semantics = semantics;
	}

	Constraint createRootConstraint() {

		return new Constraint(this, rootSourceConcept, targetValueAsList());
	}

	Constraint createConstraint(Concept sourceValue, Collection<Concept> targetValues) {

		checkValidSourceValue(sourceValue);

		for (Concept targetValue : targetValues) {

			checkValidTargetValue(targetValue);
		}

		return new Constraint(this, sourceValue, targetValues);
	}

	private List<Concept> targetValueAsList() {

		return Collections.singletonList(rootTargetConcept);
	}

	private void checkValidSourceValue(Concept value) {

		checkValidValue(rootSourceConcept, value, "Source");
	}

	private void checkValidTargetValue(Concept value) {

		checkValidValue(rootTargetConcept, value, "Target");
	}

	private void checkValidValue(Concept root, Concept value, String function) {

		if (!value.descendantOf(root)) {

			throw new RuntimeException(
						function + "-value concept \"" + value + "\""
						+ " not a descendant-concept of \"" + root + "\"");
		}
	}
}
