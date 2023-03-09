/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.hobo.user.app.basic;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class CalendarDateInputter extends SimpleTextInputter<String> {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Enter Date: (dd/mm/yyyy)";

	protected boolean validInputText(String text) {

		return CalendarDate.validDateString(text);
	}

	protected boolean potentiallyValidInputText(String text) {

		return CalendarDate.partiallyValidDateStringFormat(text);
	}

	protected String convertInputValue(String text) {

		return validInputText(text) ? text : "";
	}

	CalendarDateInputter(JComponent parent) {

		super(parent, TITLE, true);
	}
}
