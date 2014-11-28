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
import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class Employment extends DObjectShell {

	public final DArray<Job> jobs;
	public final DCellViewer<Integer> jobCount;
	public final DCellViewer<Integer> totalWeeklyPay;

	private DEditor dEditor;

	private class JobConsequenceUpdater implements KValuesListener<Job> {

		private class UpdaterForJobWeeklyPay implements KUpdateListener {

			public void onUpdated() {

				checkUpdate();
			}

			UpdaterForJobWeeklyPay(Job job) {

				job.weeklyPay.addUpdateListener(this);
			}
		}

		public void onAdded(Job value) {

			value.initialise();
			checkUpdate();

			new UpdaterForJobWeeklyPay(value);
		}

		public void onRemoved(Job value) {

			checkUpdate();
		}

		public void onCleared(List<Job> values) {

			checkUpdate();
		}

		JobConsequenceUpdater() {

			jobs.addValuesListener(this);
		}
	}

	private class Initialiser implements DObjectInitialiser {

		public void initialise() {

			new JobConsequenceUpdater();
		}
	}

	public Employment(DObjectBuilder builder) {

		super(builder);

		jobs = builder.addObjectArray(Job.class);
		jobCount = builder.getViewer(builder.addIntegerCell(CIntegerDef.min(0)));
		totalWeeklyPay = builder.getViewer(builder.addIntegerCell());

		dEditor = builder.getEditor();

		builder.addInitialiser(new Initialiser());
	}

	void initialise() {

		checkUpdate();
	}

	private void checkUpdate() {

		if (getFrame().getCategory().assertion()) {

			update();
		}
	}

	private void update() {

		setJobCount(jobs.getAll().size());
		setTotalWeeklyPay(calculateTotalWeeklyPay());
	}

	private void setJobCount(int count) {

		dEditor.getField(jobCount).set(count);
	}

	private void setTotalWeeklyPay(int value) {

		dEditor.getField(totalWeeklyPay).set(value);
	}

	private int calculateTotalWeeklyPay() {

		int pay = 0;

		for (Job job : jobs.getAll()) {

			if (job.weeklyPay.isSet()) {

				pay += job.weeklyPay.get();
			}
		}

		return pay;
	}
}
