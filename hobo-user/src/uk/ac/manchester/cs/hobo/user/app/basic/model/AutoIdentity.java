package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class AutoIdentity extends CustomValue {

	public final DCell<String> identifier;

	public AutoIdentity(DObjectBuilder builder) {

		super(builder);

		identifier = builder.addStringCell();

		initialise(builder, identifier);
	}

	public String toDisplayString() {

		return identifier.get();
	}

	abstract boolean fixedValue();
}