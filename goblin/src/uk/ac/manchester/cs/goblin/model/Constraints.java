package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class Constraints {

	private Set<Constraint> constraints = new HashSet<Constraint>();
	private ConflictResolver conflictChecker;

	Constraints(Model model) {

		conflictChecker = model.getConflictResolver();
	}

	void addRoot(ConstraintType type) {

		doAdd(type.createRootConstraint());
	}

	boolean add(Constraint constraint) {

		if (conflictChecker.checkConstraintAddition(constraint)) {

			checkRemove(constraint.getType());
			doAdd(constraint);

			return true;
		}

		return false;
	}

	void remove(Constraint constraint) {

		doRemove(constraint);
	}

	Set<Constraint> getAll() {

		return new HashSet<Constraint>(constraints);
	}

	Constraint lookFor(ConstraintType type) {

		for (Constraint constraint : constraints) {

			if (constraint.hasType(type)) {

				return constraint;
			}
		}

		return null;
	}

	private void checkRemove(ConstraintType type) {

		Constraint constraint = lookFor(type);

		if (constraint != null) {

			doRemove(constraint);
		}
	}

	private void doAdd(Constraint constraint) {

		constraints.add(constraint);

		for (Concept target : constraint.getTargetValues()) {

			target.addInwardConstraint(constraint);
		}
	}

	private void doRemove(Constraint constraint) {

		constraints.remove(constraint);

		for (Concept target : constraint.getTargetValues()) {

			target.removeInwardConstraint(constraint);
		}
	}
}
