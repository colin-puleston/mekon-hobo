package uk.ac.manchester.cs.hobo.model.motor.match;

import org.junit.Test;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.demo.*;

/**
 * @author Colin Puleston
 */
public class DMatchSectionInverterTest extends DMatcherCustomiserTest {

	@Test
	public void testSectionInverter() {

		Citizen allSpecific = assertions.create();
		Citizen allGeneral = assertions.create();
		Citizen specJobGenLoc = assertions.create();

		assertions.setLocation(allSpecific, ENGLAND);
		assertions.addJob(allSpecific, NURSE);

		assertions.setLocation(allGeneral, EU);
		assertions.addJob(allGeneral, SPECIALIST);

		assertions.setLocation(specJobGenLoc, EU);
		assertions.addJob(specJobGenLoc, NURSE);

		Citizen query = queries.create();

		queries.setLocation(query, UK);
		queries.addJob(query, MEDIC);

		testMatchAndNonMatches(
			query,
			specJobGenLoc,
			allSpecific,
			allGeneral);
	}

	DMatcherCustomiser<?, ?> createCustomiser(DModel model, DCustomMatcher matcher) {

		return new LocationInverter(model, matcher);
	}
}
