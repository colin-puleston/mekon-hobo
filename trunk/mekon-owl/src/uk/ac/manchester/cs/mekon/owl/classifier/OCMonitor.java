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

/**
 * Monitor for OWL classification requests.
 * <p>
 * The class maintains a static list of current monitors, whose
 * appropriate methods will be invoked immediately before and
 * after a classification operation.
 *
 * @author Colin Puleston
 */
public abstract class OCMonitor {

	static private List<OCMonitor> monitors = new ArrayList<OCMonitor>();

	/**
	 * Starts monitoring with the specified version of the monitor.
	 *
	 * @param monitor Version of monitor to use
	 */
	static public void start(OCMonitor monitor) {

		monitors.add(monitor);
	}

	/**
	 * Stops monitoring with the specified version of the monitor.
	 */
	static public void stop(OCMonitor monitor) {

		monitor.onStop();
		monitors.remove(monitor);
	}

 	static void pollForRequestReceived(OModel model, OWLObject request) {

		for (OCMonitor monitor : monitors) {

			monitor.onRequestReceived(model, request);
		}
	}

 	static void pollForTypesInferred(OModel model, Set<OWLClass> types) {

		for (OCMonitor monitor : monitors) {

			monitor.onTypesInferred(model, types);
		}
	}

 	static void pollForTypesSuggested(OModel model, Set<OWLClass> types) {

		for (OCMonitor monitor : monitors) {

			monitor.onTypesSuggested(model, types);
		}
	}

 	static void pollForRequestCompleted(OModel model, OWLObject request) {

		for (OCMonitor monitor : monitors) {

			monitor.onRequestCompleted(model, request);
		}
	}

 	/**
	 * Method invoked when a classification request has been received.
	 *
	 * @param model Relevant model
	 * @param request Request about to be classified
	 */
 	protected abstract void onRequestReceived(OModel model, OWLObject request);

 	/**
	 * Method invoked immediately after operation to obtain inferred-types
	 *
	 * @param model Relevant model
	 * @param types Inferred-types
	 */
 	protected abstract void onTypesInferred(OModel model, Set<OWLClass> types);

 	/**
	 * Method invoked immediately after operation to obtain suggested-types
	 *
	 * @param model Relevant model
	 * @param types Suggested-types
	 */
 	protected abstract void onTypesSuggested(OModel model, Set<OWLClass> types);

 	/**
	 * Method invoked immediately after specific request has been dealt with.
	 *
	 * @param model Relevant model
	 * @param request Request that was classified
	 */
 	protected abstract void onRequestCompleted(OModel model, OWLObject request);

 	/**
	 * Method invoked when monitoring is stopped.
	 */
 	protected abstract void onStop();
}
