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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Logger for {@link ORClassifier} and {@link ORMatcher}. Prints out
 * information concerning OWL reasoning, including operation timings
 * and, optionally, contents of requests and/or results.
 * <p>
 * Timings printed out are:
 * <ul>
 *   <li><i>Local time</i> Time taken by last classification operation
 *   <li><i>Total time</i> Time taken by all classification operations
 *   since monitor was last started
 * </ul>
 *
 * @author Colin Puleston
 */
public abstract class ORLogger extends ORMonitor {

	private boolean active = false;
	private boolean showRequests = false;
	private boolean showResults = false;

	private OActionLogger actions = new OActionLogger();

	/**
	 * Used to specify whether requests should be printed out.
	 * By default they will not be printed out.
	 *
	 * @param show True if requests should be printed out
	 */
	public void setShowRequests(boolean show) {

		showRequests = show;
	}

	/**
	 * Used to specify whether results should be printed out.
	 * By default they will not be printed out.
	 *
	 * @param show True if results should be printed out
	 */
	public void setShowResults(boolean show) {

		showResults = show;
	}

	/**
	 * Starts classification logging, if not already started.
	 */
	public void checkStart() {

		if (!active) {

			active = true;

			ORMonitor.start(this);
		}
	}

	/**
	 * Stops classification logging.
	 */
	public void stop() {

		if (active) {

			ORMonitor.stop(this);

			active = false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onStop() {

		printReasoniningTimesTitle();
		actions.printTotalTime("Operation Total");
	}

	void onRequest(OModel model, OWLObject request, String requestType) {

		if (showRequests) {

			actions.printTitle(requestType + "-Request");
			actions.printOWLObject(model, request);
		}

		actions.startAction();
	}

	void onReasoned(
			OModel model,
			Set<? extends OWLObject> results,
			String resultsType) {

		if (showResults) {

			actions.printTitle(resultsType);
			actions.printOWLObjects(model, results);
		}
	}

	void onRequestComplete() {

		actions.stopAction();

		printReasoniningTimesTitle();
		actions.printLastActionTime("Sub-Operation");
		actions.printTotalTime("Running Total");
	}

	private void printReasoniningTimesTitle() {

		actions.printTitle("Reasoning Time(s)");
	}
}
