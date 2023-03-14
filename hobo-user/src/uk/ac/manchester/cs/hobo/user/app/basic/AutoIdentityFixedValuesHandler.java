package uk.ac.manchester.cs.hobo.user.app.basic;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class AutoIdentityFixedValuesHandler
			extends
				AutoIdentityValuesHandler<AutoIdentityFixed> {

	AutoIdentityFixedValuesHandler(DModel model) {

		super(model);
	}

	Inputter<String> createValueInputter(
						JComponent parent,
						IFrameFunction function,
						AutoIdentityFixed currentValueObj) {

		throw new Error("Method should never be invoked!");
	}

	Class<AutoIdentityFixed> getValueObjectClass() {

		return AutoIdentityFixed.class;
	}
}
