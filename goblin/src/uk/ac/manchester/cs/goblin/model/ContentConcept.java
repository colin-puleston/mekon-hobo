package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ContentConcept extends Concept {

	private ConceptTracker parent;

	public boolean rename(String newName) {

		if (canRenameTo(newName)) {

			EntityId newId = getContentId(newName);
			ContentConcept renamed = new ContentConcept(this, newId);

			replace(renamed, EditsInvoker.NO_EDITS);

			return true;
		}

		return false;
	}

	public boolean move(Concept newParent) {

		Concept moved = new ContentConcept(this, newParent);
		ConflictResolution conflictRes = checkMoveConflicts(moved);

		if (conflictRes.resolvable()) {

			replace(moved, conflictRes.getResolvingEdits());

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

		this.parent = new ConceptTracker(parent);
	}

	private ContentConcept(ContentConcept replaced, Concept parent) {

		super(replaced);

		this.parent = new ConceptTracker(parent);
	}

	private ContentConcept(ContentConcept replaced, EntityId conceptId) {

		super(replaced, conceptId);

		parent = replaced.parent;
	}

	private boolean canRenameTo(String newName) {

		return !getModel().contentConcept(newName);
	}

	private ConflictResolution checkMoveConflicts(Concept moved) {

		return getModel().getConflictResolver().checkConceptMove(moved);
	}

	private EntityId getContentId(String name) {

		return getModel().getContentId(name);
	}
}
