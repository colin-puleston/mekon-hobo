package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class AutoIdentityDefault extends AutoIdentity {

	public AutoIdentityDefault(DObjectBuilder builder) {

		super(builder);
	}

	boolean fixedValue() {

		return false;
	}
}