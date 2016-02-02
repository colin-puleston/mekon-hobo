package uk.ac.manchester.cs.hobo.model.motor.match;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.demo.*;

/**
 * @author Colin Puleston
 */
class TotalWeeklyPayOverlapper
		extends
			DMatchRangeOverlapper<Citizen, Citizen, Integer> {

	protected Class<Citizen> getMatchingClass() {

		return Citizen.class;
	}

	protected Class<Citizen> getQueryClass() {

		return Citizen.class;
	}

	protected DCell<DNumberRange<Integer>> getRangeMatchCellOrNull(Citizen citizen) {

		if (citizen.employment.isSet()) {

			return citizen.employment.get().totalWeeklyPayAsRange;
		}

		return null;
	}

	TotalWeeklyPayOverlapper(DModel model) {

		super(model);
	}
}

