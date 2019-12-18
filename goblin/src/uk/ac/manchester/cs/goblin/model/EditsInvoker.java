package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
abstract class EditsInvoker {

	static final EditsInvoker NO_EDITS = new EditsInvoker() {

		void invokeEdits() {
		}
	};

	abstract void invokeEdits();
}
