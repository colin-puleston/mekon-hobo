package uk.ac.manchester.cs.hobo.user.app.basic;

import javax.swing.*;

import uk.ac.manchester.cs.hobo.model.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class CalendarDateValuesHandler extends CustomValuesHandler<CalendarDate, CalendarDateInputter> {

	CalendarDateValuesHandler(DModel model) {

		super(model);
	}

	CalendarDateInputter createValueInputter(JComponent parent, CalendarDate currentValueObj) {

		return new CalendarDateInputter(parent);
	}

	Class<CalendarDate> getValueObjectClass() {

		return CalendarDate.class;
	}

	void configureValueObject(CalendarDateInputter valueInputter, CalendarDate value) {

		valueInputter.configureValueObject(value);
	}

	String getValueObjectDisplayLabel(CalendarDate valueObj) {

		return valueObj.toDisplayString();
	}

	boolean displayValueObjectInDialog(CalendarDate valueObj) {

		return false;
	}
}
