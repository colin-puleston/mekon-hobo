/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import javax.swing.*;

/**
 * @author Colin Puleston
 */
class InfoDisplay {

	static void inform(String message) {

		JOptionPane.showMessageDialog(null, message);
	}

	static boolean checkContinue(String message) {

		return obtainContinueOption(message) == JOptionPane.OK_OPTION;
	}

	static private int obtainContinueOption(String message) {

		return JOptionPane.showConfirmDialog(
					null,
					message,
					"Continue?",
					JOptionPane.OK_CANCEL_OPTION);
	}
}
