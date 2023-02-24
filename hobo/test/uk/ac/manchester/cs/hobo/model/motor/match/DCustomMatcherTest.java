package uk.ac.manchester.cs.hobo.model.motor.match;

import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.network.*;

/**
 * @author Colin Puleston
 */
public class DCustomMatcherTest extends IMatcherTest {

	private NDirectMatcher coreMatcher = new NDirectMatcher();

	protected IMatcher createMatcher() {

		return new DCustomMatcher(coreMatcher);
	}

	protected void addValueMatchCustomiser(IValueMatchCustomiser customiser) {

		NMatcherTest.addValueMatchCustomiser(coreMatcher, customiser);
	}

	protected boolean handlesInstanceDisjunctionBasedQueries() {

		return true;
	}
}
