package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class EditActions {

	private Deque<EditAction> undos = new ArrayDeque<EditAction>();
	private Deque<EditAction> redos = new ArrayDeque<EditAction>();

	void perform(EditAction action) {

		action.perform(true);
		undos.push(action);
	}

	FinalEdit flip(boolean forward) {

		EditAction action = getStack(forward, true).pop();

		action.perform(forward);
		getStack(forward, false).push(action);

		return action.getFinalAtomicAction(forward).toFinalEdit();
	}

	private Deque<EditAction> getStack(boolean forward, boolean froms) {

		return forward == froms ? redos : undos;
	}
}
