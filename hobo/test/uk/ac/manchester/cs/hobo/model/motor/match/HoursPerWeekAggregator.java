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
			DMatchAggregator<Citizen, Job, Job, Integer> {

	private DModel model;

	protected Class<Citizen> getMatchingClass() {

		return Citizen.class;
	}

	protected DArray<Job> getTargetsArrayOrNull(Citizen citizen) {

		return citizen.employment.get().jobs;
	}

	protected DCell<DNumberRange<Integer>> getAggregatorCellOrNull(Job job) {

		return new DNumberRangeCell<Integer>(job.hoursPerWeek);
	}

	protected Job getDataSectionOrNull(Job job) {

		Job jobCopy = copyJob(job);

		jobCopy.hoursPerWeek.clear();

		return jobCopy;
	}

	HoursPerWeekAggregator(DModel model, DCustomMatcher matcher) {

		super(model, matcher);

		this.model = model;
	}

	private Job copyJob(Job job) {

		return model.getDObject(copyFrame(job.getFrame()), Job.class);
	}

	private IFrame copyFrame(IFrame frame) {

		return IFreeInstantiator.get().createFreeCopy(frame);
	}
}

