package uk.ac.manchester.cs.hobo.model.motor.match;

import org.junit.Test;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.demo.*;

/**
 * @author Colin Puleston
 */
public class DMatchAggregatorTest extends DMatcherCustomiserTest {

	@Test
	public void testMatchAggregator() {

		Citizen zero = assertions.create();
		Citizen thirty = assertions.create();
		Citizen forty = assertions.create();
		Citizen fifty = assertions.create();

		assertions.addJobWithHoursPerWeek(zero, SPECIALIST, 15);

		assertions.addJobWithHoursPerWeek(thirty, DOCTOR, 30);

		assertions.addJobWithHoursPerWeek(forty, NURSE, 20);
		assertions.addJobWithHoursPerWeek(forty, PHYSIO, 20);

		assertions.addJobWithHoursPerWeek(fifty, NURSE, 10);
		assertions.addJobWithHoursPerWeek(fifty, DOCTOR, 10);
		assertions.addJobWithHoursPerWeek(fifty, PHYSIO, 30);

		Citizen query = queries.create();

		queries.addJobWithHoursPerWeek(query, MEDIC, 35, 45);

		testMatchAndNonMatches(query, forty, zero, thirty, fifty);
	}

	DMatcherCustomiser<?> createCustomiser(DModel model, DCustomMatcher matcher) {

		return new HoursPerWeekAggregator(model, matcher);
	}
}
