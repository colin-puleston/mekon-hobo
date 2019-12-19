package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class Constraint extends EditTarget {

	private ConstraintType type;

	private ConceptTracker sourceValue;
	private ConceptTrackerSet targetValues;

	public void remove() {

		performAction(new RemoveAction(this));
	}

	public String toString() {

		return getSourceValue() + " --> " + getTargetValues();
	}

	public Model getModel() {

		return getSourceValue().getModel();
	}

	public ConstraintType getType() {

		return type;
	}

	public Concept getSourceValue() {

		return sourceValue.getEntity();
	}

	public Set<Concept> getTargetValues() {

		return targetValues.getEntities();
	}

	Constraint(ConstraintType type, Concept sourceValue, Collection<Concept> targetValues) {

		this.type = type;
		this.sourceValue = new ConceptTracker(sourceValue);
		this.targetValues = new ConceptTrackerSet(getModel(), targetValues);

		checkTargetConflicts(targetValues);
	}

	boolean add() {

		ConflictResolution conflictRes = checkAdditionConflicts();

		if (conflictRes.resolvable()) {

			EditAction action = new AddAction(this);

			action = conflictRes.incorporateResolvingEdits(action);
			action = checkIncorporateTypeConstraintRemoval(action);

			performAction(action);

			return true;
		}

		return false;
	}

	void doAdd(boolean replacement) {

		getSourceValue().doAddConstraint(this);

		for (Concept target : getTargetValues()) {

			target.addInwardConstraint(this);
		}
	}

	void doRemove(boolean replacing) {

		getSourceValue().doRemoveConstraint(this);

		for (Concept target : getTargetValues()) {

			target.removeInwardConstraint(this);
		}
	}

	void removeTargetValue(Concept target) {

		Set<Concept> targets = getTargetValues();

		targets.remove(target);

		if (targets.isEmpty()) {

			remove();
		}
		else {

			replace(new Constraint(type, getSourceValue(), targets));
		}
	}

	boolean hasType(ConstraintType testType) {

		return testType.equals(type);
	}

	private void checkTargetConflicts(Collection<Concept> targetValues) {

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

	private void replace(Constraint replacement) {

		performAction(new ReplaceConstraintAction(this, replacement));
	}

	private EditAction checkIncorporateTypeConstraintRemoval(EditAction action) {

		Constraint constraint = lookForCurrentTypeConstraint();

		if (constraint == null) {

			return action;
		}

		return new CompoundEditAction(new RemoveAction(constraint), action);
	}

	private Constraint lookForCurrentTypeConstraint() {

		return getSourceValue().lookForLocalConstraint(type);
	}

	private void performAction(EditAction action) {

		getModel().getEditActions().perform(action);
	}

	private ConflictResolution checkAdditionConflicts() {

		return getModel().getConflictResolver().checkConstraintAddition(this);
	}
}
