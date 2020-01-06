/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

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

	static Confirmation checkConfirmOrCancel(String title, String message) {

		return obtainConfirmation(title, message, JOptionPane.YES_NO_CANCEL_OPTION);
	}

	static private int obtainContinueOption(String message) {

		return obtainOption("Continue?", message, JOptionPane.OK_CANCEL_OPTION);
	}

	static private Confirmation obtainConfirmation(String title, String message, int options) {

		return Confirmation.get(obtainOption(title, message, options));
	}

	static private int obtainOption(String title, String message, int options) {

		return JOptionPane.showConfirmDialog(null, message, title, options);
	}
}
