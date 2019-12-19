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

		return (Concept)target;
	}

	public Constraint getConstraint() {

		return (Constraint)target;
	}

	PrimaryEdit(boolean addition, EditTarget target) {

		this.addition = addition;
		this.target = target;
	}
}
