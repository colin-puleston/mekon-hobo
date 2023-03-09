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

	static private class DateParseException extends RuntimeException {

		static private final long serialVersionUID = -1;
	}

	static private abstract class DateParse {

		class Parser {

			private String[] sections;
			private boolean emptyFinalSection;

			Parser(String dateString) {

				sections = dateString.split("/");
				emptyFinalSection = dateString.endsWith("/");
			}

			boolean parse() {

				return validFormat(this) && parseSections();
			}

			boolean validCompletedFormat() {

				return sections.length == 3 && !emptyFinalSection;
			}

			boolean validPartialFormat() {

				return sections.length <= (emptyFinalSection ? 2 : 3);
			}

			private boolean parseSections() {

				try {

					if (sections.length > 0) {

						parseDay();

						if (sections.length > 1) {

							parseMonth();

							if (sections.length == 3) {

								parseYear();
							}
						}
					}
				}
				catch (DateParseException e) {

					return false;
				}

				return true;
			}

			private void parseDay() {

				onParsedDay(parseSection(0, 2, 1, 31));
			}

			private void parseMonth() {

				onParsedMonth(parseSection(1, 2, 1, 12));
			}

			private void parseYear() {

				onParsedYear(parseSection(2, 4, 0, 9999));
			}

			private int parseSection(int index, int maxLength, int minValue, int maxValue) {

				String section = sections[index];

				if (section.length() <= maxLength) {

					try {

						int i = Integer.parseInt(section.trim());

						if (i >= minValue && i <= maxValue) {

							return i;
						}

					}
					catch (NumberFormatException e) {
					}
				}

				throw new DateParseException();
			}
		}

		boolean parse(String dateString) {

			return new Parser(dateString).parse();
		}

		abstract boolean validFormat(Parser parser);

		void onParsedDay(int value) {
		}

		void onParsedMonth(int value) {
		}

		void onParsedYear(int value) {
		}
	}

	static private class CompletedDateParse extends DateParse {

		final boolean parseOk;

		private int day;
		private int month;
		private int year;

		CompletedDateParse(String dateString) {

			parseOk = parse(dateString);
		}

		boolean validDate() {

			return parseOk && toDateValueOrNull(year, month, day) != null;
		}

		boolean setDate(CalendarDate cd) {

			return parseOk && cd.setDate(year, month, day);
		}

		boolean validFormat(Parser parser) {

			return parser.validCompletedFormat();
		}

		void onParsedDay(int value) {

			day = value;
		}

		void onParsedMonth(int value) {

			month = value;
		}

		void onParsedYear(int value) {

			year = value;
		}
	}

	static private class PartialDateParse extends DateParse {

		final boolean parseOk;

		PartialDateParse(String dateString) {

			parseOk = parse(dateString);
		}

		boolean validFormat(Parser parser) {

			return parser.validPartialFormat();
		}
	}

	static public boolean validDateStringFormat(String dateString) {

		return new CompletedDateParse(dateString).parseOk;
	}

	static public boolean partiallyValidDateStringFormat(String dateString) {

		return new PartialDateParse(dateString).parseOk;
	}

	static public boolean validDateString(String dateString) {

		return new CompletedDateParse(dateString).validDate();
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

		return new CompletedDateParse(dateString).setDate(this);
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