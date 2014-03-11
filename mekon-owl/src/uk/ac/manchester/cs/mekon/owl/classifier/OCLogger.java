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

package uk.ac.manchester.cs.mekon.owl.classifier;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Logger that prints out timing information for OWL classification
 * requests and, optionally, prints out the requests and/or results.
 * Timings printed out are:
 * <ul>
 *   <li><i>Local time</i> Time taken by last classification operation
 *   <li><i>Total time</i> Time taken by all classification operations
 *   since monitor was last started
 * </ul>
 *
 * @author Colin Puleston
 */
public class OCLogger extends OCMonitor {

	static private boolean showRequests = false;
	static private boolean showResults = false;

	static private OCLogger logger = null;

	/**
	 * Used to specify whether requests should be printed out.
	 * By default they will not be printed out.
	 *
	 * @param show True if requests should be printed out
	 */
 	static public void setShowRequests(boolean show) {

		showRequests = show;
	}

	/**
	 * Used to specify whether results should be printed out.
	 * By default they will not be printed out.
	 *
	 * @param show True if results should be printed out
	 */
 	static public void setShowResults(boolean show) {

		showResults = show;
	}

	/**
	 * Starts classification logging.
	 */
	static public void start() {

		logger = new OCLogger();
		OCMonitor.start(logger);
	}

	/**
	 * Stops classification logging.
	 */
	static public void stop() {

		if (logger != null) {

			OCMonitor.stop(logger);
			logger = null;
		}
	}

	private OActionLogger actions = new OActionLogger();

 	/**
	 * {@inheritDoc}
	 */
 	protected void onPreClassify(OModel model, OWLObject request) {

		if (showRequests) {

			actions.printTitle("Request");
			actions.printOWLObject(model, request);
		}

		actions.startAction();
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onClassified(OModel model, OWLObject request, Set<OWLClass> results) {

		actions.stopAction();

		if (showResults) {

			actions.printTitle("Results");
			actions.printOWLObjects(model, results);
		}

		printClassificationTimesTitle();
		actions.printLastActionTime("Sub-Operation");
		actions.printTotalTime("Running Total");
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onStop() {

		printClassificationTimesTitle();
		actions.printTotalTime("Operation Total");
	}

 	private void printClassificationTimesTitle() {

		actions.printTitle("Reasoning Time(s)");
	}
}
