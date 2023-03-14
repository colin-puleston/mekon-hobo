package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
abstract class AutoIdentityValuesHandler
					<A extends AutoIdentity>
					extends CustomValuesHandler<A, String> {

	AutoIdentityValuesHandler(DModel model) {

		super(model);
	}

	void configureValueObject(A valueObj, String inputValue) {

		valueObj.identifier.set(inputValue);
	}

	boolean displayValueObjectInDialog(A valueObj) {

		return false;
	}
}
