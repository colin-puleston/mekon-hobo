/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import javax.swing.*;

/**
 * @author Colin Puleston
 */
enum Confirmation {

	YES(JOptionPane.YES_OPTION),
	NO(JOptionPane.NO_OPTION),
	CANCEL(JOptionPane.CANCEL_OPTION);

	static Confirmation get(int option) {

		for (Confirmation value : values()) {

			if (value.option == option) {

				return value;
			}
		}

		throw new Error("Confirmation not found for option: " + option);
	}

	boolean yes() {

		return this == YES;
	}

	boolean no() {

		return this == NO;
	}

	boolean cancel() {

		return this == CANCEL;
	}

	private int option;

	private Confirmation(int option) {

		this.option = option;
	}
}
