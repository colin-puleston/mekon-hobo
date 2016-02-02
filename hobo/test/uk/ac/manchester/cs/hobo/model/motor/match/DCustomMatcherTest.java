package uk.ac.manchester.cs.hobo.model.motor.match;

import java.util.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;

import uk.ac.manchester.cs.hobo.manage.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.model.motor.*;
import uk.ac.manchester.cs.hobo.demo.*;

/**
 * @author Colin Puleston
 */
public class DCustomMatcherTest {

	static private final String DEMO_PACKAGE_NAME = Citizen.class.getPackage().getName();

	private DModel model;
	private IStore store;
	private DCustomMatcher matcher;

	private int storedAssertionCount = 0;

	@Before
	public void setUp() {

		DBuilder dBuilder = DManager.createBuilder();
		CBuilder cBuilder = dBuilder.getCBuilder();
		IStoreBuilder storeBuilder = IStoreManager.getBuilder(cBuilder);

		model = dBuilder.build();
		matcher = createCustomMatcher(model);

		removeAllMatchers(storeBuilder);
		storeBuilder.addMatcher(matcher);

		store = storeBuilder.build();
	}

	@After
	public void clearUp() {

		store.clear();

		IStoreManager.stop(model.getCModel());
	}

	@Test
	public void testBasicRangeQuery() {

		Citizen _100 = createCitizenWithTotalWeeklyPay(100);
		Citizen _200 = createCitizenWithTotalWeeklyPay(200);
		Citizen _300 = createCitizenWithTotalWeeklyPay(300);

		Citizen query = createCitizenWithTotalWeeklyPay(150, 250);

		testMatchAndNonMatches(query, _200, _100, _300);
	}

	@Test
	public void testRangeOverlapQuery() {

		Citizen _0_50 = createCitizenWithTotalWeeklyPay(0, 50);
		Citizen _100_200 = createCitizenWithTotalWeeklyPay(100, 200);
		Citizen _150_240 = createCitizenWithTotalWeeklyPay(150, 240);
		Citizen _160_240 = createCitizenWithTotalWeeklyPay(160, 240);
		Citizen _160_250 = createCitizenWithTotalWeeklyPay(160, 250);
		Citizen _200_300 = createCitizenWithTotalWeeklyPay(200, 300);
		Citizen _300_400 = createCitizenWithTotalWeeklyPay(300, 400);

		Citizen query = createCitizenWithTotalWeeklyPay(150, 250);

		testMatchesAndNonMatches(
			query,
			new DObject[]{_100_200, _150_240, _160_240, _160_250, _200_300},
			new DObject[]{_0_50, _300_400});
	}

	private DCustomMatcher createCustomMatcher(DModel model) {

		IMatcher core = new IDirectMatcher();
		DCustomMatcher custom = new DCustomMatcher(model, core);

		custom.addCustomiser(new TotalWeeklyPayOverlapper(model));

		return custom;
	}

	private void removeAllMatchers(IStoreBuilder storeBuilder) {

		for (IMatcher matcher : storeBuilder.getMatchers()) {

			storeBuilder.removeMatcher(matcher);
		}
	}

	private Citizen createCitizenWithTotalWeeklyPayQuery(int minPay, int maxPay) {

		Citizen citizen = createCitizenWithTotalWeeklyPay(minPay, maxPay);

		citizen.getFrame().resetFunction(IFrameFunction.QUERY);

		return citizen;
	}

	private Citizen createCitizenWithTotalWeeklyPay(int pay) {

		return createCitizenWithTotalWeeklyPay(DNumberRange.exact(pay));
	}

	private Citizen createCitizenWithTotalWeeklyPay(int minPay, int maxPay) {

		return createCitizenWithTotalWeeklyPay(DNumberRange.range(minPay, maxPay));
	}

	private Citizen createCitizenWithTotalWeeklyPay(DNumberRange<Integer> pay) {

		Citizen citizen = model.instantiate(Citizen.class);
		Employment employment = model.instantiate(Employment.class);
		Job job = model.instantiate(Job.class);

		citizen.employment.set(employment);

		employment.jobs.add(job);
		employment.totalWeeklyPayAsRange.set(pay);

		return citizen;
	}

	private void testMatchAndNonMatches(
					DObject query,
					DObject expectMatch,
					DObject... expectNonMatches) {

		testMatchesAndNonMatches(
			query,
			new DObject[]{expectMatch},
			expectNonMatches);
	}

	private void testMatchesAndNonMatches(
					DObject query,
					DObject[] expectMatches,
					DObject[] expectNonMatches) {

		storeAll(expectNonMatches);
		testMatchesSet(query, storeAll(expectMatches));
	}

	private void testOrderedMatches(DObject query, DObject... expectMatches) {

		testMatchesList(query, storeAll(expectMatches));
	}

	private void testMatchesList(DObject query, List<CIdentity> expectedIds) {

		assertEquals(expectedIds, match(query));
	}

	private void testMatchesSet(DObject query, List<CIdentity> expectedIds) {

		assertEquals(
			new HashSet<CIdentity>(expectedIds),
			new HashSet<CIdentity>(match(query)));
	}

	private List<CIdentity> match(DObject query) {

		return store.match(query.getFrame()).getAllMatches();
	}

	private List<CIdentity> storeAll(DObject[] assertions) {

		List<CIdentity> ids = new ArrayList<CIdentity>();

		for (DObject assertion : assertions) {

			ids.add(store(assertion));
		}

		return ids;
	}

	private CIdentity store(DObject assertion) {

		CIdentity id = getNextAssertionId();

		store.add(assertion.getFrame(), id);

		return id;
	}

	private CIdentity getNextAssertionId() {

		String id = "A" + (++storedAssertionCount);

		return new CIdentity(id, id);
	}
}
