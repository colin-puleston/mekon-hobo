package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class EditActions {

	private Deque<ActionSequence> undos = new ArrayDeque<ActionSequence>();
	private Deque<ActionSequence> redos = new ArrayDeque<ActionSequence>();

	private ActionSequence currentSequence = null;

	private class ActionSequence {

		private LinkedList<EditAction> actions = new LinkedList<EditAction>();

		void add(EditAction action) {

			actions.add(action);
		}

		void performSequence(boolean forward) {

			Iterator<EditAction> i = directionIterator(forward);

			while (i.hasNext()) {

				i.next().perform(forward);
			}
		}

		private Iterator<EditAction> directionIterator(boolean forward) {

			return forward ? actions.iterator() : actions.descendingIterator();
		}
	}

	private abstract class NewActionHandler {

		void handle(EditsInvoker enablingEdits) {

			if (currentSequence == null) {

				currentSequence = new ActionSequence();

				enablingEdits.invokeEdits();
				handleCurrentAction();

				undos.add(currentSequence);

				currentSequence = null;
			}
			else {

				handleCurrentAction();
			}
		}

		abstract void handleCurrentAction();

		void handleCurrentActionComponent(EditActionType type, EditTarget target) {

			EditAction action = type.createAction(target);

			action.perform(true);
			currentSequence.add(action);
		}
	}

	private class NewAtomicActionHandler extends NewActionHandler {

		private EditActionType type;
		private EditTarget target;

		NewAtomicActionHandler(EditActionType type, EditTarget target) {

			this.type = type;
			this.target = target;
		}

		void handleCurrentAction() {

			handleCurrentActionComponent(type, target);
		}
	}

	private class NewReplaceHandler<T extends EditTarget> extends NewActionHandler {

		private T removeTarget;
		private T addTarget;
		private EntityTracking<T> targetTracking;

		NewReplaceHandler(T removeTarget, T addTarget, EntityTracking<T> targetTracking) {

			this.removeTarget = removeTarget;
			this.addTarget = addTarget;
			this.targetTracking = targetTracking;
		}

		void handleCurrentAction() {

			handleCurrentActionComponent(EditActionType.REPLACE_REMOVE, removeTarget);
			targetTracking.updateForReplacement(removeTarget, addTarget);
			handleCurrentActionComponent(EditActionType.REPLACE_ADD, addTarget);
		}
	}

	void performAdd(EditTarget target, EditsInvoker enablingEdits) {

		new NewAtomicActionHandler(EditActionType.ADD, target).handle(enablingEdits);
	}

	void performRemove(EditTarget target, EditsInvoker enablingEdits) {

		new NewAtomicActionHandler(EditActionType.REMOVE, target).handle(enablingEdits);
	}

	<T extends EditTarget> void performReplace(
									T removeTarget,
									T addTarget,
									EntityTracking<T> targetTracking,
									EditsInvoker enablingEdits) {

		new NewReplaceHandler<T>(removeTarget, addTarget, targetTracking).handle(enablingEdits);
	}

	void undo() {

		ActionSequence sequence = undos.pop();

		sequence.performSequence(false);
		redos.push(sequence);
	}

	void redo() {

		ActionSequence sequence = redos.pop();

		sequence.performSequence(true);
		undos.push(sequence);
	}
}
