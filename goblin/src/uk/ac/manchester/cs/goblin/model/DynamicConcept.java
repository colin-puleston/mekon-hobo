package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class DynamicConcept extends Concept {

	private ConceptTracker parent;

	public boolean resetId(DynamicId newDynamicId) {

		if (canResetId(newDynamicId)) {

			replace(new DynamicConcept(this, toEntityId(newDynamicId)));

			return true;
		}

		return false;
	}

	public boolean move(Concept newParent) {

		ConflictResolution conflictRes = checkMoveConflicts(newParent);

		if (conflictRes.resolvable()) {

			replace(new DynamicConcept(this, newParent), conflictRes);

			return true;
		}

		return false;
	}

	public boolean isRoot() {

		return false;
	}

	public Concept getParent() {

		return parent.getEntity();
	}

	public boolean descendantOf(Concept test) {

		return getParent().equals(test) || getParent().descendantOf(test);
	}

	public Constraint getClosestAncestorConstraint(ConstraintType type) {

		return getParent().getClosestConstraint(type);
	}

	DynamicConcept(EntityId conceptId, Concept parent) {

		super(parent.getHierarchy(), conceptId);

		this.parent = toConceptTracker(parent);
	}

	private DynamicConcept(DynamicConcept replaced, Concept parent) {

		super(replaced);

		this.parent = toConceptTracker(parent);
	}

	private DynamicConcept(DynamicConcept replaced, EntityId conceptId) {

		super(replaced, conceptId);

		parent = replaced.parent;
	}

	private boolean canResetId(DynamicId newDynamicId) {

		return getModel().canResetDynamicConceptId(this, newDynamicId);
	}

	private ConflictResolution checkMoveConflicts(Concept newParent) {

		ConceptTracker saveParent = parent;
		parent = toConceptTracker(newParent);

		ConflictResolution conflicts = checkMovedConflicts();

		parent = saveParent;

		return conflicts;
	}

	private ConflictResolution checkMovedConflicts() {

		return getModel().getConflictResolver().checkConceptMove(this);
	}

	private ConceptTracker toConceptTracker(Concept concept) {

		return getModel().getConceptTracking().toTracker(concept);
	}

	private EntityId toEntityId(DynamicId newDynamicId) {

		return getModel().toEntityId(newDynamicId);
	}
}