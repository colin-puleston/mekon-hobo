package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class TextBlockValuesHandler extends TextEntityValuesHandler<TextBlock> {

	TextBlockValuesHandler(DModel model) {

		super(model);
	}

	Class<TextBlock> getValueObjectClass() {

		return TextBlock.class;
	}

	Inputter<String> createAssertionValueInputter(
						JComponent parent,
						TextBlock currentValueObj) {

		return new TextBlockInputter(parent, currentValueObj);
	}

	boolean displayValueObjectInDialog(TextBlock valueObj) {

		new TextBlockViewer(valueObj);

		return true;
	}
}
