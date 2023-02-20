package uk.ac.manchester.cs.hobo.user.app.basic.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class CalendarDate extends DObjectShell {

	static private final String DATE_STRING_FORMAT = "%d/%d/%d";

	static private class DateParse {

		final boolean parseOk;

		private int day = -1;
		private int month = -1;
		private int year = -1;

		DateParse(String dateString) {

			String[] sections = dateString.split("/");

			if (sections.length == 3) {

				try {

					day = parseSection(sections[0], 1);
					month = parseSection(sections[1], 1);
					year = parseSection(sections[2], 0);
				}
				catch (NumberFormatException e) {
				}
			}

			parseOk = year != -1;
		}

		boolean validDate() {

			return parseOk && toDateValueOrNull(year, month, day) != null;
		}

		boolean setDate(CalendarDate cd) {

			return parseOk && cd.setDate(year, month, day);
		}

		private int parseSection(String section, int min) {

			int i = Integer.parseInt(section.trim());

			if (i < min) {

				throw new NumberFormatException();
			}

			return i;
		}
	}

	static public boolean validDateStringFormat(String dateString) {

		return new DateParse(dateString).parseOk;
	}

	static public boolean validDateString(String dateString) {

		return new DateParse(dateString).validDate();
	}

	static private Long toDateValueOrNull(int year, int month, int day) {

		Calendar c = Calendar.getInstance();

		try {

			c.setLenient(false);
			c.set(year, month - 1, day);

			return c.getTime().getTime();
		}
		catch (RuntimeException e) {

			return null;
		}
	}

	public final DCell<Long> dateValue;

	private class ValueSlotDecativator implements DObjectInitialiser {

		private DEditor dEditor;

		public void initialise() {

			getValueSlotEditor().setActivation(CActivation.ACTIVE_HIDDEN);
		}

		ValueSlotDecativator(DEditor dEditor) {

			this.dEditor = dEditor;
		}

		private ISlotEditor getValueSlotEditor() {

			return dEditor.getIEditor().getSlotEditor(dateValue.getSlot());
		}
	}

	public CalendarDate(DObjectBuilder builder) {

		super(builder);

		dateValue = builder.addNumberCell(DNumberRange.LONG);

		builder.addInitialiser(new ValueSlotDecativator(builder.getEditor()));
	}

	public boolean setDate(int year, int month, int day) {

		Long value = toDateValueOrNull(year, month, day);

		if (value != null) {

			dateValue.set(value);

			return true;
		}

		return false;
	}

	public boolean setDate(String dateString) {

		return new DateParse(dateString).setDate(this);
	}

	public String toDisplayString() {

		return dateValue.isSet() ? dateToString(dateValue.get()) : "";
	}

	private String dateToString(long value) {

		Calendar c = Calendar.getInstance();

		c.setTime(new Date(value));

		return dateToString(
					c.get(Calendar.DAY_OF_MONTH),
					c.get(Calendar.MONTH) + 1,
					c.get(Calendar.YEAR));
	}

	private String dateToString(int year, int month, int day) {

		return String.format(DATE_STRING_FORMAT, year, month, day);
	}
}