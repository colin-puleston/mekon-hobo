/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.hobo.demo;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class Job extends CitizenshipObject {

	static private boolean makeHoursPerWeekAbstractEditable = false;

	static public void makeHoursPerWeekAbstractEditableForTesting() {

		makeHoursPerWeekAbstractEditable = true;
	}

	public final DCell<Integer> hoursPerWeek;
	public final DCell<Integer> hourlyPay;
	public final DCellViewer<Integer> weeklyPay;

	private DEditor dEditor;

	private class WeeklyPayUpdater implements KUpdateListener {

		public void onUpdated() {

			if (assertionFrame()) {

				updateWeeklyPayValue();
			}
			else {

				updateWeeklyPayValueType();
			}
		}

		WeeklyPayUpdater() {

			hourlyPay.addUpdateListener(this);
			hoursPerWeek.addUpdateListener(this);
		}
	}

	private class WeeklyPayValueTypeRestorer implements ISlotListener {

		private CNumber defaultPayRange = getDynamicWeeklyPayRange();

		public void onUpdatedValueType(CValue<?> valueType) {

			CNumber reqValueType = getRequiredValueType();

			if (!valueType.equals(reqValueType)) {

				setWeeklyPayValueType(reqValueType);
			}
		}

		public void onUpdatedCardinality(CCardinality cardinality) {
		}

		public void onUpdatedActivation(CActivation activation) {
		}

		public void onUpdatedEditability(CEditability editability) {
		}

		WeeklyPayValueTypeRestorer() {

			weeklyPay.getSlot().addListener(this);
		}

		private CNumber getRequiredValueType() {

			return assertionFrame() ? defaultPayRange : getDynamicWeeklyPayRange();
		}
	}

	private class Initialiser implements DObjectInitialiser {

		public void initialise() {

			updateWeeklyPayValue();
			updateWeeklyPayValueType();

			new WeeklyPayUpdater();
			new WeeklyPayValueTypeRestorer();
		}
	}

	public Job(DObjectBuilder builder) {

		super(builder);

		hoursPerWeek = builder.addIntegerCell();
		hourlyPay = builder.addIntegerCell();
		weeklyPay = builder.getViewer(builder.addIntegerCell());

		dEditor = builder.getEditor();

		builder.addInitialiser(new Initialiser());

		if (makeHoursPerWeekAbstractEditable) {

			builder.setEditability(hoursPerWeek, CEditability.FULL);
		}
	}

	private void updateWeeklyPayValue() {

		if (hourlyPay.isSet() && hoursPerWeek.isSet()) {

			getWeeklyPay().set(hourlyPay.get() * hoursPerWeek.get());
		}
		else {

			getWeeklyPay().clear();
		}
	}

	private void updateWeeklyPayValueType() {

		setWeeklyPayValueType(getDynamicWeeklyPayRange());
	}

	private void setWeeklyPayValueType(CNumber valueType) {

		getWeeklyPaySlotEditor().setValueType(valueType);
	}

	private CNumber getDynamicWeeklyPayRange() {

		CNumber pay = getRangeValue(hourlyPay);
		CNumber hours = getRangeValue(hoursPerWeek);

		INumber min = pay.getMin().multiplyBy(hours.getMin());
		INumber max = pay.getMax().multiplyBy(hours.getMax());

		return CNumber.range(Integer.class, min, max);
	}

	private CNumber getRangeValue(DCell<Integer> cell) {

		ISlot slot = cell.getSlot();
		ISlotValues values = slot.getValues();

		if (values.isEmpty()) {

			return (CNumber)slot.getValueType();
		}

		return (CNumber)values.asList().get(0).getType();
	}

	private DCell<Integer> getWeeklyPay() {

		return dEditor.getCell(weeklyPay);
	}

	private ISlotEditor getWeeklyPaySlotEditor() {

		return dEditor.getIEditor().getSlotEditor(weeklyPay.getSlot());
	}

	private boolean assertionFrame() {

		return getFrame().getFunction().assertion();
	}
}
