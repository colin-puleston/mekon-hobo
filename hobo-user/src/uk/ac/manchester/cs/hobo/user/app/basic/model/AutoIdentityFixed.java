package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class AutoIdentityFixed extends AutoIdentity {

	public AutoIdentityFixed(DObjectBuilder builder) {

		super(builder);
	}

	boolean fixedValue() {

		return true;
	}
}