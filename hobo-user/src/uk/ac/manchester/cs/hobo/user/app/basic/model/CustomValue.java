package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class CustomValue extends DObjectShell {

	private class ValueCellInitialiser implements DObjectInitialiser {

		private DEditor dEditor;
		private DCell<?> valueCell;

		public void initialise() {

			getValueSlotEditor().setActivation(CActivation.ACTIVE_HIDDEN);
		}

		ValueCellInitialiser(DObjectBuilder builder, DCell<?> valueCell) {

			this.valueCell = valueCell;

			dEditor = builder.getEditor();

			builder.addInitialiser(this);
		}

		private ISlotEditor getValueSlotEditor() {

			return dEditor.getIEditor().getSlotEditor(valueCell.getSlot());
		}
	}

	public abstract String toDisplayString();

	CustomValue(DObjectBuilder builder) {

		super(builder);
	}

	void initialise(DObjectBuilder builder, DCell<?> valueCell) {

		new ValueCellInitialiser(builder, valueCell);
	}
}