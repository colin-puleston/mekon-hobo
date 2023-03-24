package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public class ModelLoadChecker {

	static public void checkLoaded(DModel model) {

		if (!model.loadedDClass(AutoIdentifiedEntity.class)) {

			throw new HoboBasicAppModelException(
						"Basic App direct model has not been loaded!");
		}
	}
}
