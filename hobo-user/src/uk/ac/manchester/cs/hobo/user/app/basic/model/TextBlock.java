package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class TextBlock extends CustomValue {

	static private final String PRESENT_TEXT_DISPLAY_STRING = "[TEXT...]";
	static private final String QUERY_DISPLAY_STRING_FORMAT = "CONTAINS %s";

	public final DCell<String> text;

	public TextBlock(DObjectBuilder builder) {

		super(builder);

		text = builder.addStringCell();

		initialise(builder, text);
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