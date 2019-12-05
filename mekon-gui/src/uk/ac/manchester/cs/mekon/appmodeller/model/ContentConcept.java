package uk.ac.manchester.cs.mekon.appmodeller.model;

/**
 * @author Colin Puleston
 */
class ContentConcept extends Concept {

	private Concept parent;

	public boolean rename(String newName) {

		return renameNonRoot(newName);
	}

	public boolean move(Concept newParent) {

		Concept oldParent = parent;

		swapParentForMove(newParent);

		if (checkMove()) {

			onConceptMoved();

			return true;
		}

		swapParentForMove(oldParent);

		return false;
	}

	public void remove() {

		parent.removeChild(this);
	}

	public boolean isRoot() {

		return false;
	}

	public Concept getParent() {

		return parent;
	}

	public boolean descendantOf(Concept test) {

		return parent.equals(test) || parent.descendantOf(test);
	}

	public Constraint getClosestAncestorConstraint(ConstraintType type) {

		return parent.getClosestConstraint(type);
	}

	ContentConcept(EntityId conceptId, Concept parent) {

		super(parent.getHierarchy(), conceptId);

		this.parent = parent;
	}

	private void swapParentForMove(Concept newParent) {

		parent.removeChildForMove(this);
		parent = newParent;
		parent.addChildForMove(this);
	}

	private boolean checkMove() {

		return getModel().getConflictResolver().checkMovedConcept(this);
	}
}
