package uk.ac.manchester.cs.hobo.model.motor.match;

import java.util.*;

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
public abstract class DMatcherCustomiserTest extends DemoModelBasedTest {

	static private final String DEMO_PACKAGE_NAME = Citizen.class.getPackage().getName();

	final CitizenInstantiator assertions
				= new CitizenInstantiator(IFrameFunction.ASSERTION);

	final CitizenInstantiator queries
				= new CitizenInstantiator(IFrameFunction.QUERY);

	private DModel model;
	private IStore store;
	private DCustomMatcher matcher;

	private int storedAssertionCount = 0;

	class CitizenInstantiator {

		private IFrameFunction function;

		CitizenInstantiator(IFrameFunction function) {

			this.function = function;
		}

		Citizen create() {

			Citizen citizen = instantiate(Citizen.class);
			Personal personal = instantiate(Personal.class);
			Employment employment = instantiate(Employment.class);

			citizen.personal.set(personal);
			citizen.employment.set(employment);

			return citizen;
		}

		Citizen createWithJobWithHoursPerWeek(int hours) {

			Citizen citizen = create();

			addJobWithHoursPerWeek(citizen, JOB_TYPE, hours);

			return citizen;
		}

		Citizen createWithJobWithHoursPerWeek(int minHours, int maxHours) {

			Citizen citizen = create();

			addJobWithHoursPerWeek(citizen, JOB_TYPE, minHours, maxHours);

			return citizen;
		}

		void setLocation(Citizen citizen, String location) {

			setLocation(citizen.personal.get(), location);
		}

		void addJobWithHoursPerWeek(Citizen citizen, String jobType, int hours) {

			addJob(citizen, jobType).hoursPerWeek.set(hours);
		}

		void addJobWithHoursPerWeek(
				Citizen citizen,
				String jobType,
				int minHours,
				int maxHours) {

			DNumberRange<Integer> hours = DNumberRange.range(minHours, maxHours);

			setRange(addJob(citizen, jobType).hoursPerWeek, hours);
		}

		Job addJob(Citizen citizen, String jobType) {

			Job job = addJob(citizen);

			setJobType(job, jobType);

			return job;
		}

		private Job addJob(Citizen citizen) {

			Job job = instantiate(Job.class);

			citizen.employment.get().jobs.add(job);

			return job;
		}

		private void setLocation(Personal personal, String location) {

			setCFrameSlotValue(personal.getFrame(), LOCATION_PROPERTY, location);
		}

		private void setJobType(Job job, String jobType) {

			setCFrameSlotValue(job.getFrame(), JOB_TYPE_PROPERTY, jobType);
		}

		private void setRange(DCell<Integer> intCell, DNumberRange<Integer> range) {

			new DNumberRangeCell<Integer>(intCell).set(range);
		}

		private void setRange(DCellViewer<Integer> intCell, DNumberRange<Integer> range) {

			new DNumberRangeCell<Integer>(intCell).set(range);
		}

		private void setCFrameSlotValue(
						IFrame container,
						String slotName,
						String valueName) {

			getSlot(container, slotName).getValuesEditor().add(getCFrame(valueName));
		}

		private ISlot getSlot(IFrame container, String slotName) {

			return container.getSlots().get(getDemoModelId(slotName));
		}

		private CFrame getCFrame(String valueName) {

			return model.getCModel().getFrames().get(getDemoModelId(valueName));
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

		if (store != null) {

			store.clear();
			IStoreManager.checkStop(model.getCModel());
		}
	}

	abstract DMatcherCustomiser<?> createCustomiser(DModel model, DCustomMatcher matcher);

	void testMatchAndNonMatches(
			DObject query,
			DObject expectMatch,
			DObject... expectNonMatches) {

		testMatchesAndNonMatches(
			query,
			Arrays.asList(expectMatch),
			Arrays.asList(expectNonMatches));
	}

	void testMatchesAndNonMatches(
			DObject query,
			List<DObject> expectMatches,
			List<DObject> expectNonMatches) {

		storeAll(expectNonMatches);
		testMatchesSet(query, storeAll(expectMatches));
	}

	private DCustomMatcher createCustomMatcher(DModel model) {

		IMatcher core = new IDirectMatcher();
		DCustomMatcher custom = new DCustomMatcher(core);

		custom.addCustomiser(createCustomiser(model, custom));

		return custom;
	}

	private void removeAllMatchers(IStoreBuilder storeBuilder) {

		for (IMatcher matcher : storeBuilder.getMatchers()) {

			storeBuilder.removeMatcher(matcher);
		}
	}

	private void testMatchesSet(DObject query, List<CIdentity> expectedIds) {

		assertEquals(
			new HashSet<CIdentity>(expectedIds),
			new HashSet<CIdentity>(match(query)));
	}

	private List<CIdentity> match(DObject query) {

		return store.match(query.getFrame()).getAllMatches();
	}

	private List<CIdentity> storeAll(List<DObject> assertions) {

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
