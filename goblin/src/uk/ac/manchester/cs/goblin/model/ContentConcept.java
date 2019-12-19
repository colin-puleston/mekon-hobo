package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ContentConcept extends Concept {

	private ConceptTracker parent;

	public boolean rename(String newName) {

		if (canRenameTo(newName)) {

			replace(new ContentConcept(this, getContentId(newName)));

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

		this.parent = new ConceptTracker(parent);
	}

	private ContentConcept(ContentConcept replaced, Concept parent) {

		super(replaced);

		this.parent = new ConceptTracker(parent);
		System.out.println("\nNEW-PARENT: " + parent);
		System.out.println("SET-PARENT: " + getParent());
	}

	private ContentConcept(ContentConcept replaced, EntityId conceptId) {

		super(replaced, conceptId);

		parent = replaced.parent;
	}

	private boolean canRenameTo(String newName) {

		return !getModel().contentConcept(newName);
	}

	private ConflictResolution checkMoveConflicts(Concept newParent) {

		ConceptTracker saveParent = parent;
		parent = new ConceptTracker(newParent);

		ConflictResolution conflicts = checkMovedConflicts();

		parent = saveParent;

		return conflicts;
	}

	private ConflictResolution checkMovedConflicts() {

		return getModel().getConflictResolver().checkConceptMove(this);
	}

	private EntityId getContentId(String name) {

		return getModel().getContentId(name);
	}
}
