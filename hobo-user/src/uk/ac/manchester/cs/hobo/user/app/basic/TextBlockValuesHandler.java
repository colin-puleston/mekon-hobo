package uk.ac.manchester.cs.hobo.user.app.basic;

import javax.swing.*;

import uk.ac.manchester.cs.hobo.model.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class TextBlockValuesHandler extends CustomValuesHandler<TextBlock, TextBlockInputter> {

	TextBlockValuesHandler(DModel model) {

		super(model);
	}

	TextBlockInputter createValueInputter(JComponent parent, TextBlock currentValueObj) {

		return new TextBlockInputter(parent, currentValueObj);
	}

	Class<TextBlock> getValueObjectClass() {

		return TextBlock.class;
	}

	void configureValueObject(TextBlockInputter valueInputter, TextBlock valueObj) {

		valueInputter.configureValueObject(valueObj);
	}

	String getValueObjectDisplayLabel(TextBlock valueObj) {

		return valueObj.toDisplayString();
	}

	boolean displayValueObjectInDialog(TextBlock valueObj) {

		new TextBlockViewer(valueObj);

		return true;
	}
}
