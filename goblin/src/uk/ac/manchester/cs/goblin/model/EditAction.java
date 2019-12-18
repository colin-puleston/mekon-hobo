package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class EditAction {

	private EditActionType type;
	private EditTarget target;

	EditAction(EditActionType type, EditTarget target) {

		this.type = type;
		this.target = target;
	}

	void perform(boolean forward) {

		type.performAction(target, forward);
	}
}
