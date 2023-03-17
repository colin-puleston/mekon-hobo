package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
abstract class TextEntityValuesHandler
					<V extends TextEntity>
					extends CustomValuesHandler<V, String> {

	TextEntityValuesHandler(DModel model) {

		super(model);
	}

	Inputter<String> createValueInputter(
						JComponent parent,
						IFrameFunction function,
						V currentValueObj) {

		if (function.query()) {

			return new TextEntityQueryInputter(parent, currentValueObj);
		}

		return createAssertionValueInputter(parent, currentValueObj);
	}

	abstract Inputter<String> createAssertionValueInputter(
									JComponent parent,
									V currentValueObj);

	void configureValueObject(V valueObj, String inputValue) {

		valueObj.text.set(inputValue);
	}
}
