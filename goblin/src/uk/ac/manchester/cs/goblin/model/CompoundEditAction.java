package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class CompoundEditAction extends EditAction {

	private LinkedList<EditAction> subActions = new LinkedList<EditAction>();

	CompoundEditAction(EditAction... subActions) {

		this.subActions.addAll(Arrays.asList(subActions));
	}

	void addSubAction(EditAction subAction) {

		subActions.add(subAction);
	}

	void addSubActions(List<EditAction> subActions) {

		this.subActions.addAll(subActions);
	}

	void perform(boolean forward) {

		Iterator<EditAction> i = directionIterator(forward);

		while (i.hasNext()) {

			i.next().perform(forward);
		}
	}

	AtomicEditAction<?> getPrimaryAtomicAction(boolean forward) {

		EditAction finalSub = subActions.descendingIterator().next();

		if (finalSub instanceof AtomicEditAction) {

			return (AtomicEditAction<?>)finalSub;
		}

		return finalSub.getPrimaryAtomicAction(forward);
	}

	private Iterator<EditAction> directionIterator(boolean forward) {

		return forward ? subActions.iterator() : subActions.descendingIterator();
	}
}
