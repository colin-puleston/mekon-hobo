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
import uk.ac.manchester.cs.mekon.demomodel.*;

import uk.ac.manchester.cs.hobo.manage.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.model.motor.*;
import uk.ac.manchester.cs.hobo.demo.*;

/**
 * @author Colin Puleston
 */
public class DCustomMatcherTest extends DemoModelBasedTest {

	static private final String DEMO_PACKAGE_NAME = Citizen.class.getPackage().getName();

	private DModel model;
	private IStore store;
	private DCustomMatcher matcher;

	private CitizenInstantiator assertions
				= new CitizenInstantiator(IFrameFunction.ASSERTION);

	private CitizenInstantiator queries
				= new CitizenInstantiator(IFrameFunction.QUERY);

	private int storedAssertionCount = 0;

	private class CitizenInstantiator {

		private IFrameFunction function;

		CitizenInstantiator(IFrameFunction function) {

			this.function = function;
		}

		Citizen createWithWeeklyPay(int pay) {

			return createWithWeeklyPay(DNumberRange.exact(pay));
		}

		Citizen createWithWeeklyPay(int minPay, int maxPay) {

			return createWithWeeklyPay(DNumberRange.range(minPay, maxPay));
		}

		Citizen createWithWeeklyPay(DNumberRange<Integer> pay) {

			Citizen citizen = createEmployed();

			addJob(citizen);
			citizen.employment.get().totalWeeklyPayAsRange.set(pay);

			return citizen;
		}

		Citizen createEmployed() {

			Citizen citizen = instantiate(Citizen.class);
			Employment employment = instantiate(Employment.class);

			citizen.employment.set(employment);

			return citizen;
		}

		void addJobWithHoursPerWeek(Citizen citizen, String jobType, int hours) {

			addJobWithHoursPerWeek(citizen, jobType, DNumberRange.exact(hours));
		}

		void addJobWithHoursPerWeek(Citizen citizen, String jobType, int minHours, int maxHours) {

			addJobWithHoursPerWeek(
				citizen,
				jobType,
				DNumberRange.range(minHours, maxHours));
		}

		void addJobWithHoursPerWeek(Citizen citizen, String jobType, DNumberRange<Integer> hours) {

			Job job = addJob(citizen);

			setJobType(job, jobType);
			job.hoursPerWeekAsRange.set(hours);
		}

		private Job addJob(Citizen citizen) {

			Job job = instantiate(Job.class);

			citizen.employment.get().jobs.add(job);

			return job;
		}

		private void setJobType(Job job, String jobType) {

			ISlot slot = getJobTypeSlot(job.getFrame());
			CFrame value = getJobTypeValue(jobType);

			slot.getValuesEditor().add(value);
		}

		private ISlot getJobTypeSlot(IFrame job) {

			return job.getSlots().get(getDemoModelId(JOB_TYPE_PROPERTY));
		}

		private CFrame getJobTypeValue(String jobType) {

			return model.getCModel().getFrames().get(getDemoModelId(JOB_TYPE));
		}

		private <D extends DObject>D instantiate(Class<D> dClass) {

			return model.getConcept(dClass).instantiate(function);
		}
	}

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
	public void testMatchRangeOverlapper_simpleContainment() {

		Citizen _100 = assertions.createWithWeeklyPay(100);
		Citizen _200 = assertions.createWithWeeklyPay(200);
		Citizen _300 = assertions.createWithWeeklyPay(300);

		Citizen query = queries.createWithWeeklyPay(150, 250);

		testMatchAndNonMatches(query, _200, _100, _300);
	}

	@Test
	public void testMatchRangeOverlapper_overlaps() {

		Citizen _0_50 = assertions.createWithWeeklyPay(0, 50);
		Citizen _100_200 = assertions.createWithWeeklyPay(100, 200);
		Citizen _150_240 = assertions.createWithWeeklyPay(150, 240);
		Citizen _160_240 = assertions.createWithWeeklyPay(160, 240);
		Citizen _160_250 = assertions.createWithWeeklyPay(160, 250);
		Citizen _200_300 = assertions.createWithWeeklyPay(200, 300);
		Citizen _300_400 = assertions.createWithWeeklyPay(300, 400);

		Citizen query = queries.createWithWeeklyPay(150, 250);

		testMatchesAndNonMatches(
			query,
			new DObject[]{_100_200, _150_240, _160_240, _160_250, _200_300},
			new DObject[]{_0_50, _300_400});
	}

	@Test
	public void testMatchAggregator() {

		Citizen zero = assertions.createEmployed();
		Citizen thirty = assertions.createEmployed();
		Citizen forty = assertions.createEmployed();
		Citizen fifty = assertions.createEmployed();

		assertions.addJobWithHoursPerWeek(zero, SPECIALIST, 15);

		assertions.addJobWithHoursPerWeek(thirty, DOCTOR, 30);

		assertions.addJobWithHoursPerWeek(forty, NURSE, 20);
		assertions.addJobWithHoursPerWeek(forty, PHYSIO, 20);

		assertions.addJobWithHoursPerWeek(fifty, NURSE, 10);
		assertions.addJobWithHoursPerWeek(fifty, DOCTOR, 10);
		assertions.addJobWithHoursPerWeek(fifty, PHYSIO, 30);

		Citizen query = queries.createEmployed();

		queries.addJobWithHoursPerWeek(query, MEDIC, 35, 45);

		testMatchAndNonMatches(query, forty, zero, thirty, fifty);
	}

	private DCustomMatcher createCustomMatcher(DModel model) {

		IMatcher core = new IDirectMatcher();
		DCustomMatcher custom = new DCustomMatcher(model, core);

		custom.addCustomiser(new TotalWeeklyPayOverlapper(model));
		custom.addCustomiser(new HoursPerWeekAggregator(model, custom));

		return custom;
	}

	private void removeAllMatchers(IStoreBuilder storeBuilder) {

		for (IMatcher matcher : storeBuilder.getMatchers()) {

			storeBuilder.removeMatcher(matcher);
		}
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

	private CIdentity getDemoModelId(String name) {

		return DemoModelBasedTest.nameToIdentity(name);
	}
}
