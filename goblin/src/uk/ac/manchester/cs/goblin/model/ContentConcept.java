package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ContentConcept extends Concept {

	private ConceptTracker parent;

	public boolean resetId(EntityIdSpec newIdSpec) {

		if (canResetId(newIdSpec)) {

			replace(new ContentConcept(this, toContentId(newIdSpec)));

			return true;
		}

		return false;
	}

	public boolean move(Concept newParent) {

		ConflictResolution conflictRes = checkMoveConflicts(newParent);

		if (conflictRes.resolvable()) {

			replace(new ContentConcept(this, newParent), conflictRes);

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

	ContentConcept(EntityId conceptId, Concept parent) {

		super(parent.getHierarchy(), conceptId);

		this.parent = toConceptTracker(parent);
	}

	private ContentConcept(ContentConcept replaced, Concept parent) {

		super(replaced);

		this.parent = toConceptTracker(parent);
	}

	private ContentConcept(ContentConcept replaced, EntityId conceptId) {

		super(replaced, conceptId);

		parent = replaced.parent;
	}

	private boolean canResetId(EntityIdSpec newIdSpec) {

		return getModel().canResetContentConceptId(this, newIdSpec);
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

	private EntityId toContentId(EntityIdSpec newIdSpec) {

		return getModel().toContentId(newIdSpec);
	}
}
