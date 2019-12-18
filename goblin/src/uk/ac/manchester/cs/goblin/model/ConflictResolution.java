package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ConflictResolution {

	static final ConflictResolution NO_CONFLICTS = new ConflictResolution();
	static final ConflictResolution NO_RESOLUTION = new ConflictResolution();

	private EditsInvoker resolvingEdits;

	ConflictResolution(EditsInvoker resolvingEdits) {

		this.resolvingEdits = resolvingEdits;
	}

	boolean resolvable() {

		return this != NO_RESOLUTION;
	}

	EditsInvoker getResolvingEdits() {

		return resolvingEdits;
	}

	private ConflictResolution() {

		this(EditsInvoker.NO_EDITS);
	}
}
