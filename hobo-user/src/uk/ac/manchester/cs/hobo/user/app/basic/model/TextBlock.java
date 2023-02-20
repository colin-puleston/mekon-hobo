package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class TextBlock extends DObjectShell {

	static private final String PRESENT_TEXT_DISPLAY_STRING = "[TEXT...]";

	public final DCell<String> text;

	private class ValueSlotDecativator implements DObjectInitialiser {

		private DEditor dEditor;

		public void initialise() {

			getValueSlotEditor().setActivation(CActivation.ACTIVE_HIDDEN);
		}

		ValueSlotDecativator(DEditor dEditor) {

			this.dEditor = dEditor;
		}

		private ISlotEditor getValueSlotEditor() {

			return dEditor.getIEditor().getSlotEditor(text.getSlot());
		}
	}

	public TextBlock(DObjectBuilder builder) {

		super(builder);

		text = builder.addStringCell();

		builder.addInitialiser(new ValueSlotDecativator(builder.getEditor()));
	}

	public String toDisplayString() {

		return text.isSet() ? PRESENT_TEXT_DISPLAY_STRING : "";
	}
}