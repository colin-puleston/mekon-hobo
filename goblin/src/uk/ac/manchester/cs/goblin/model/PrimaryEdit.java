package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public class PrimaryEdit {

	private boolean addition;
	private EditTarget target;

	public boolean addition() {

		return addition;
	}

	public boolean removal() {

		return !addition;
	}

	public boolean conceptEdit() {

		return target instanceof Concept;
	}

	public boolean constraintEdit() {

		return target instanceof Constraint;
	}

	public Concept getConcept() {

		if (conceptEdit()) {

			return (Concept)target;
		}

		if (constraintEdit()) {

			return getConstraint().getSourceValue();
		}

		throw new Error("Unrecognised target type: " + target.getClass());
	}

	public Constraint getConstraint() {

		return constraintEdit() ? (Constraint)target : null;
	}

	PrimaryEdit(boolean addition, EditTarget target) {

		this.addition = addition;
		this.target = target;
	}
}
