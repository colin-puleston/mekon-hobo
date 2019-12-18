package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
enum EditActionType {

	ADD(true, false),
	REMOVE(false, false),
	REPLACE_ADD(true, true),
	REPLACE_REMOVE(false, true);

	private boolean addAction;
	private boolean replaceSubAction;

	EditAction createAction(EditTarget target) {

		return new EditAction(this, target);
	}

	void performAction(EditTarget target, boolean forward) {

		if (forward == addAction) {

			target.doAdd(replaceSubAction);
		}
		else {

			target.doRemove(replaceSubAction);
		}
	}

	private EditActionType(boolean addAction, boolean replaceSubAction) {

		this.addAction = addAction;
		this.replaceSubAction = replaceSubAction;
	}
}
