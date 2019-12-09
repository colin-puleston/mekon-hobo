package uk.ac.manchester.cs.hobo.model.motor.match;

import java.util.*;

import org.junit.Test;
import org.junit.Before;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.demo.model.*;

/**
 * @author Colin Puleston
 */
public class DMatchRangeOverlapperTest extends DMatcherCustomiserTest {

	@Before
	public void setUp() {

		Job.makeHoursPerWeekAbstractEditableForTesting();

		super.setUp();
	}

	@Test
	public void testMatchRangeOverlapper_exactValuesInRange() {

		Citizen _10 = assertions.createWithJobWithHoursPerWeek(10);
		Citizen _20 = assertions.createWithJobWithHoursPerWeek(20);
		Citizen _30 = assertions.createWithJobWithHoursPerWeek(30);

		Citizen query = queries.createWithJobWithHoursPerWeek(15, 25);

		testMatchAndNonMatches(query, _20, _10, _30);
	}

	@Test
	public void testMatchRangeOverlapper_rangeOverlaps() {

		Citizen _10_15 = assertions.createWithJobWithHoursPerWeek(10, 15);
		Citizen _10_20 = assertions.createWithJobWithHoursPerWeek(10, 20);
		Citizen _20_30 = assertions.createWithJobWithHoursPerWeek(20, 30);
		Citizen _25_35 = assertions.createWithJobWithHoursPerWeek(25, 35);
		Citizen _30_40 = assertions.createWithJobWithHoursPerWeek(30, 40);
		Citizen _40_50 = assertions.createWithJobWithHoursPerWeek(40, 50);
		Citizen _45_50 = assertions.createWithJobWithHoursPerWeek(45, 50);

		Citizen query = queries.createWithJobWithHoursPerWeek(20, 40);

		testMatchesAndNonMatches(
			query,
			Arrays.asList(_10_20, _20_30, _25_35, _30_40, _40_50),
			Arrays.asList(_10_15, _45_50));
	}

	DMatcherCustomiser<?, ?> createCustomiser(DModel model, DCustomMatcher matcher) {

		return new HoursPerWeekOverlapper(model);
	}
}
