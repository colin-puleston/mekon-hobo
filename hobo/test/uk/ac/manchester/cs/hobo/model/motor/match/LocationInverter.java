package uk.ac.manchester.cs.hobo.model.motor.match;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.demo.*;

/**
 * @author Colin Puleston
 */
class LocationInverter extends DMatchSectionInverter<Citizen, Personal> {

	protected Class<Citizen> getMatchingClass() {

		return Citizen.class;
	}

	protected DCell<Personal> getInversionSectionCellOrNull(Citizen citizen) {

		return citizen.personal;
	}

	LocationInverter(DModel model, DCustomMatcher matcher) {

		super(model, matcher);
	}
}

