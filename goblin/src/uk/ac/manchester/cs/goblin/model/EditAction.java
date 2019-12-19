package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
abstract class EditAction {

	abstract void perform(boolean forward);

	abstract AtomicEditAction<?> getPrimaryAtomicAction(boolean forward);
}
