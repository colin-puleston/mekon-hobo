package uk.ac.manchester.cs.hobo.user.app.basic;

import uk.ac.manchester.cs.hobo.user.app.*;

import uk.ac.manchester.cs.hobo.user.app.basic.custom.*;

/**
 * @author Colin Puleston
 */
public class HoboBasicApp extends HoboApp {

	static private final long serialVersionUID = -1;

	static public void main(String[] args) {

		new HoboBasicApp();
	}

	public HoboBasicApp() {

		setCustomiser(new HoboBasicAppCustomiser(getDModel(), getCentralStore()));

		configureFromFile();
		display();
	}
}
