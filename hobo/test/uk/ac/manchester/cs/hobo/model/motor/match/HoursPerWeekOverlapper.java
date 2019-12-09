package uk.ac.manchester.cs.hobo.model.motor.match;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.demo.model.*;

/**
 * @author Colin Puleston
 */
class HoursPerWeekOverlapper
		extends
			DMatchRangeOverlapper<Citizen, Citizen, Integer> {

	protected Class<Citizen> getMatchingClass() {

		return Citizen.class;
	}

	protected Class<Citizen> getQueryClass() {

		return Citizen.class;
	}

	protected DCell<DNumberRange<Integer>> getRangeMatchCellOrNull(Citizen citizen) {

		return new DNumberRangeCell<Integer>(getHoursPerWeek(citizen));
	}

	HoursPerWeekOverlapper(DModel model) {

		super(model);
	}

	private DCell<Integer> getHoursPerWeek(Citizen citizen) {

		return citizen.employment.get().jobs.getAll().get(0).hoursPerWeek;
	}
}

