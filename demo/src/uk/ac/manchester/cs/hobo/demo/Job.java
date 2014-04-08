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

import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class Job extends DObjectShell {

	public final DCell<Integer> hoursPerWeek;
	public final DCell<Integer> hourlyPay;
	public final DCellViewer<Integer> weeklyPay;

	private DEditor dEditor;

	private class WeeklyPayUpdater implements KUpdateListener {

		public void onUpdated() {

			if (hourlyPay.isSet() && hoursPerWeek.isSet()) {

				getWeeklyPay().set(hourlyPay.get() * hoursPerWeek.get());
			}
			else {

				getWeeklyPay().clear();
			}
		}

		WeeklyPayUpdater() {

			hourlyPay.addUpdateListener(this);
			hoursPerWeek.addUpdateListener(this);
		}

		private DCell<Integer> getWeeklyPay() {

			return dEditor.getField(weeklyPay);
		}
	}

	private class Initialiser implements DObjectInitialiser {

		public void initialise() {

			if (!getFrame().abstractInstance()) {

				new WeeklyPayUpdater();
			}
		}
	}

	public Job(DObjectBuilder builder) {

		super(builder);

		hoursPerWeek = builder.addIntegerCell();
		hourlyPay = builder.addIntegerCell();
		weeklyPay = builder.getViewer(builder.addIntegerCell());

		dEditor = builder.getEditor();

		builder.addInitialiser(new Initialiser());
	}
}
