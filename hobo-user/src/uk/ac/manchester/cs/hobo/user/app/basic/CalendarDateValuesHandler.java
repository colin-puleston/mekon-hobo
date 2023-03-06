package uk.ac.manchester.cs.hobo.user.app.basic;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class CalendarDateValuesHandler extends CustomValuesHandler<CalendarDate, String> {

	CalendarDateValuesHandler(DModel model) {

		super(model);
	}

	Inputter<String> createValueInputter(
						JComponent parent,
						IFrameFunction function,
						CalendarDate currentValueObj) {

		return new CalendarDateInputter(parent);
	}

	Class<CalendarDate> getValueObjectClass() {

		return CalendarDate.class;
	}

	void configureValueObject(CalendarDate valueObj, String inputValue) {

		valueObj.setDate(inputValue);
	}

	String getValueObjectDisplayLabel(CalendarDate valueObj) {

		return valueObj.toDisplayString();
	}

	boolean displayValueObjectInDialog(CalendarDate valueObj) {

		return false;
	}
}
