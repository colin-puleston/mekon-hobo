package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
abstract class AutoIdentityValuesHandler
					<V extends AutoIdentity>
					extends CustomValuesHandler<V, String> {

	AutoIdentityValuesHandler(DModel model) {

		super(model);
	}

	void configureValueObject(V valueObj, String inputValue) {

		valueObj.identifier.set(inputValue);
	}
}
