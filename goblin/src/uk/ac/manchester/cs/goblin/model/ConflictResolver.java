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

	private abstract class ConstraintConflictsChecker {

		boolean check(List<Constraint> conflicts) {

			if (!conflicts.isEmpty()) {

				if (!confirmConflictRemovals(conflicts)) {

					return false;
				}

				for (Constraint conflict : conflicts) {

					conflict.remove();
				}
			}

			return true;
		}

		abstract boolean confirmConflictRemovals(List<Constraint> conflicts);
	}

	private class ConstraintAdditionChecker extends ConstraintConflictsChecker {

		boolean check(Constraint constraint) {

			return check(findAll(constraint));
		}

		boolean confirmConflictRemovals(List<Constraint> conflicts) {

			return confirmations.confirmConstraintAddition(conflicts);
		}

		private List<Constraint> findAll(Constraint constraint) {

			List<Constraint> conflicts = new ArrayList<Constraint>();

			conflicts.addAll(new UpwardsConflictFinder(constraint).findAll());
			conflicts.addAll(new DownwardsConflictFinder(constraint).findAll());

			return conflicts;
		}
	}

	private class ConceptMoveChecker extends ConstraintConflictsChecker {

		boolean check(Concept moved) {

			return check(new ConceptMoveConflictsFinder(moved).conflicts);
		}

		boolean confirmConflictRemovals(List<Constraint> conflicts) {

			return confirmations.confirmConceptMove(conflicts);
		}
	}

	void setConfirmations(Confirmations confirmations) {

		this.confirmations = confirmations;
	}

	boolean checkConstraintAddition(Constraint constraint) {

		return new ConstraintAdditionChecker().check(constraint);
	}

	boolean checkMovedConcept(Concept moved) {

		return new ConceptMoveChecker().check(moved);
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
