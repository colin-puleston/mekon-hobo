package uk.ac.manchester.cs.hobo.model.motor.match;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.demo.*;

/**
 * @author Colin Puleston
 */
class HoursPerWeekAggregator
		extends
			DMatchAggregator<Citizen, Citizen, Job, Job, Integer> {

	private DModel model;
	private IFreeInstances freeInstances;

	protected Class<Citizen> getMatchingClass() {

		return Citizen.class;
	}

	protected Class<Citizen> getQueryClass() {

		return Citizen.class;
	}

	protected DArray<Job> getTargetsArrayOrNull(Citizen citizen) {

		if (citizen.employment.isSet()) {

			return citizen.employment.get().jobs;
		}

		return null;
	}

	protected DCell<DNumberRange<Integer>> getAggregatorCellOrNull(Job job) {

		return job.hoursPerWeekAsRange;
	}

	protected Job getDataSectionOrNull(Job job) {

		Job jobCopy = copyJob(job);

		jobCopy.hoursPerWeek.clear();

		return jobCopy;
	}

	HoursPerWeekAggregator(DModel model, DCustomMatcher matcher) {

		super(model, matcher);

		this.model = model;

		freeInstances = new IFreeInstances(model.getCModel());
	}

	private Job copyJob(Job job) {

		IFrame frame = job.getFrame();
		IFrame copyFrame = freeInstances.createFreeCopy(frame);

		return model.getDObject(copyFrame, Job.class);
	}
}

