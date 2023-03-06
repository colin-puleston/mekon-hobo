package uk.ac.manchester.cs.hobo.user.app.basic;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class TextBlockValuesHandler extends CustomValuesHandler<TextBlock, String> {

	TextBlockValuesHandler(DModel model) {

		super(model);
	}

	Inputter<String> createValueInputter(
						JComponent parent,
						IFrameFunction function,
						TextBlock currentValueObj) {

		if (function.query()) {

			return new TextBlockQueryInputter(parent, currentValueObj);
		}

		return new TextBlockInputter(parent, currentValueObj);
	}

	Class<TextBlock> getValueObjectClass() {

		return TextBlock.class;
	}

	void configureValueObject(TextBlock valueObj, String inputValue) {

		valueObj.text.set(inputValue);
	}

	String getValueObjectDisplayLabel(TextBlock valueObj) {

		return valueObj.toDisplayString();
	}

	boolean displayValueObjectInDialog(TextBlock valueObj) {

		new TextBlockViewer(valueObj);

		return true;
	}
}
