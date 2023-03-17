package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class TextLineValuesHandler extends TextEntityValuesHandler<TextLine> {

	static private final String ASSERTION_VALUE_INPUTTER_TITLE = "Enter Value";

	TextLineValuesHandler(DModel model) {

		super(model);
	}

	Class<TextLine> getValueObjectClass() {

		return TextLine.class;
	}

	Inputter<String> createAssertionValueInputter(
						JComponent parent,
						TextLine currentValueObj) {

		return new StringInputter(parent, ASSERTION_VALUE_INPUTTER_TITLE, true);
	}
}
