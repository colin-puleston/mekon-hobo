package uk.ac.manchester.cs.hobo.user.app.basic;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.*;
import uk.ac.manchester.cs.hobo.user.app.basic.custom.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
public class HoboBasicApp extends HoboApp {

	static private final long serialVersionUID = -1;

	static public void main(String[] args) {

		new HoboBasicApp();
	}

	public HoboBasicApp() {

		DModel model = getDModel();

		ModelLoadChecker.checkLoaded(model);
		setCustomiser(new HoboBasicAppCustomiser(model, getCentralStore()));

		configureFromFile();
		display();
	}
}
