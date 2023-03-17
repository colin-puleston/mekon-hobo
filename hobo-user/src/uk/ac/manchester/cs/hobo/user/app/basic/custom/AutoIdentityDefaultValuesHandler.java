package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class AutoIdentityDefaultValuesHandler
			extends
				AutoIdentityValuesHandler<AutoIdentityDefault> {

	static private final String INPUTTER_TITLE = "Entity Identifier";

	AutoIdentityDefaultValuesHandler(DModel model) {

		super(model);
	}

	Class<AutoIdentityDefault> getValueObjectClass() {

		return AutoIdentityDefault.class;
	}

	Inputter<String> createValueInputter(
						JComponent parent,
						IFrameFunction function,
						AutoIdentityDefault currentValueObj) {

		return new StringInputter(parent, INPUTTER_TITLE, false);
	}
}
