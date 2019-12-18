package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConflictResolver {

	private Confirmations confirmations = new AutoConfirmations();

	private abstract class ConflictFinder {

		private ConstraintType type;

		private Concept source;
		private Set<Concept> targets;

		private List<Constraint> conflicts = new ArrayList<Constraint>();

		ConflictFinder(Constraint constraint) {

			type = constraint.getType();
			source = constraint.getSourceValue();
			targets = constraint.getTargetValues();
		}

		boolean any() {

			findFromLinkedConcepts(source);

			return !conflicts.isEmpty();
		}

		List<Constraint> findAll() {

			findFromLinkedConcepts(source);

			return conflicts;
		}

		abstract Set<Concept> getLinkedConcepts(Concept current);

		abstract boolean targetSubsumptionsOk(Set<Concept> targets, Set<Concept> testTargets);

		private void findFromLinkedConcepts(Concept current) {

			for (Concept child : getLinkedConcepts(current)) {

				findFrom(child);
			}
		}

		private void findFrom(Concept current) {

			Constraint test = current.lookForLocalConstraint(type);

			if (test != null) {

				if (!conflictingTargets(test.getTargetValues())) {

					return;
				}

				conflicts.add(test);
			}

			findFromLinkedConcepts(current);
		}

		private boolean conflictingTargets(Set<Concept> testTargets) {

			if (targets.equals(testTargets)) {

				return true;
			}

			return !targetSubsumptionsOk(targets, testTargets);
		}
	}

	private class UpwardsConflictFinder extends ConflictFinder {

		UpwardsConflictFinder(Constraint constraint) {

			super(constraint);
		}

		Set<Concept> getLinkedConcepts(Concept current) {

			return Collections.singleton(current.getParent());
		}

		boolean targetSubsumptionsOk(Set<Concept> targets, Set<Concept> testTargets) {

			return allSubsumed(testTargets, targets);
		}
	}

	private class DownwardsConflictFinder extends ConflictFinder {

		DownwardsConflictFinder(Constraint constraint) {

			super(constraint);
		}

		Set<Concept> getLinkedConcepts(Concept current) {

			return current.getChildren();
		}

		boolean targetSubsumptionsOk(Set<Concept> targets, Set<Concept> testTargets) {

			return allSubsumed(targets, testTargets);
		}
	}

	private class ConceptMoveConflictsFinder {

		final List<Constraint> conflicts = new ArrayList<Constraint>();

		ConceptMoveConflictsFinder(Concept moved) {

			findDownwardsFrom(moved);
		}

		private void findDownwardsFrom(Concept moved) {

			findFor(moved.getConstraints(), true);
			findFor(moved.getInwardConstraints(), false);

			for (Concept child : moved.getChildren()) {

				findDownwardsFrom(child);
			}
		}

		private void findFor(Set<Constraint> constraints, boolean upOnly) {

			for (Constraint constraint : constraints) {

				if (anyUpwards(constraint) || (!upOnly && anyDownwards(constraint))) {

					conflicts.add(constraint);
				}
			}
		}

		private boolean anyUpwards(Constraint constraint) {

			return new UpwardsConflictFinder(constraint).any();
		}

		private boolean anyDownwards(Constraint constraint) {

			return new DownwardsConflictFinder(constraint).any();
		}
	}

	private abstract class ConstraintConflictsResolver {

		private List<Constraint> conflicts;

		private class RemovalsInvoker extends EditsInvoker {

			void invokeEdits() {

				for (Constraint conflict : conflicts) {

					conflict.remove();
				}
			}
		}

		void initialise(List<Constraint> conflicts) {

			this.conflicts = conflicts;
		}

		ConflictResolution check() {

			if (conflicts.isEmpty()) {

				return ConflictResolution.NO_CONFLICTS;
			}

			if (confirmConflictRemovals(conflicts)) {

				return new ConflictResolution(new RemovalsInvoker());
			}

			return ConflictResolution.NO_RESOLUTION;
		}

		abstract boolean confirmConflictRemovals(List<Constraint> conflicts);
	}

	private class ConstraintAdditionConflictsResolver extends ConstraintConflictsResolver {

		ConstraintAdditionConflictsResolver(Constraint constraint) {

			initialise(findConflicts(constraint));
		}

		boolean confirmConflictRemovals(List<Constraint> conflicts) {

			return confirmations.confirmConstraintAddition(conflicts);
		}

		private List<Constraint> findConflicts(Constraint constraint) {

			List<Constraint> conflicts = new ArrayList<Constraint>();

			conflicts.addAll(new UpwardsConflictFinder(constraint).findAll());
			conflicts.addAll(new DownwardsConflictFinder(constraint).findAll());

			return conflicts;
		}
	}

	private class ConceptMoveConflictsResolver extends ConstraintConflictsResolver {

		ConceptMoveConflictsResolver(Concept moved) {

			initialise(new ConceptMoveConflictsFinder(moved).conflicts);
		}

		boolean confirmConflictRemovals(List<Constraint> conflicts) {

			return confirmations.confirmConceptMove(conflicts);
		}
	}

	void setConfirmations(Confirmations confirmations) {

		this.confirmations = confirmations;
	}

	ConflictResolution checkConstraintAddition(Constraint constraint) {

		return new ConstraintAdditionConflictsResolver(constraint).check();
	}

	ConflictResolution checkConceptMove(Concept moved) {

		return new ConceptMoveConflictsResolver(moved).check();
	}

	private boolean allSubsumed(Set<Concept> sups, Set<Concept> subs) {

		for (Concept sub : subs) {

			if (!subsumedByAny(sups, sub)) {

				return false;
			}
		}

		return true;
	}

	private boolean subsumedByAny(Set<Concept> sups, Concept sub) {

		for (Concept sup : sups) {

			if (sub.subsumedBy(sup)) {

				return true;
			}
		}

		return false;
	}
}
