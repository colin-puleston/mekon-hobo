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
	static private final String QUERY_DISPLAY_STRING_FORMAT = "CONTAINS %s";

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

		if (text.isSet()) {

			return assertionObject()
					? PRESENT_TEXT_DISPLAY_STRING
					: toQueryDisplayString();
		}

		return "";
	}

	public void setQueryExpression(TextExpression expr) {

		checkQueryObjectAccess();

		text.set(expr.toQueryString());
	}

	public TextExpression getQueryExpression() {

		checkQueryObjectAccess();

		return TextExpression.fromQueryString(text.get());
	}

	private String toQueryDisplayString() {

		String expr = getQueryExpression().toDisplayString();

		return String.format(QUERY_DISPLAY_STRING_FORMAT, expr);
	}

	private void checkQueryObjectAccess() {

		if (assertionObject()) {

			throw new RuntimeException("Cannot perform operation on assertion object!");
		}
	}

	private boolean assertionObject() {

		return getFrame().getFunction().assertion();
	}
}