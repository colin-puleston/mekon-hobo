package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
abstract class AtomicEditAction<T extends EditTarget> extends EditAction {

	private T target;

	AtomicEditAction(T target) {

		this.target = target;
	}

	T getTarget() {

		return target;
	}

	void perform(boolean forward) {

		if (forward == addAction()) {

			target.doAdd(replaceSubAction());
		}
		else {

			target.doRemove(replaceSubAction());
		}
	}

	AtomicEditAction<T> getPrimaryAtomicAction(boolean forward) {

		return this;
	}

	PrimaryEdit toFinalEdit() {

		return new PrimaryEdit(addAction(), target);
	}

	abstract boolean addAction();

	boolean replaceSubAction() {

		return false;
	}
}
