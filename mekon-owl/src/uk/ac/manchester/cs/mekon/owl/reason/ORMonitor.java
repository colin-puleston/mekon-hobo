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

/**
 * Monitor for {@link ORClassifier} and {@link ORMatcher}
 * operations.
 * <p>
 * The class maintains a static list of current monitors, whose
 * appropriate methods will be invoked immediately before and
 * after a classification operation.
 *
 * @author Colin Puleston
 */
public abstract class ORMonitor {

	static private List<ORMonitor> monitors = new ArrayList<ORMonitor>();

	/**
	 * Starts monitoring with the specified version of the monitor.
	 *
	 * @param monitor Version of monitor to use
	 */
	static public void start(ORMonitor monitor) {

		monitors.add(monitor);
	}

	/**
	 * Stops monitoring with the specified version of the monitor.
	 *
	 * @param monitor Relevant version of monitor
	 */
	static public void stop(ORMonitor monitor) {

		monitor.onStop();
		monitors.remove(monitor);
	}

	static void pollForClassifierRequest(OModel model, OWLObject request) {

		for (ORMonitor monitor : monitors) {

			monitor.onClassifierRequest(model, request);
		}
	}

	static void pollForTypesInferred(OModel model, Set<OWLClass> types) {

		for (ORMonitor monitor : monitors) {

			monitor.onTypesInferred(model, types);
		}
	}

	static void pollForTypesSuggested(OModel model, Set<OWLClass> types) {

		for (ORMonitor monitor : monitors) {

			monitor.onTypesSuggested(model, types);
		}
	}

	static void pollForClassifierDone(OModel model, OWLObject request) {

		for (ORMonitor monitor : monitors) {

			monitor.onClassifierDone(model, request);
		}
	}

	static void pollForMatcherRequest(OModel model, OWLObject request) {

		for (ORMonitor monitor : monitors) {

			monitor.onMatcherRequest(model, request);
		}
	}

	static void pollForMatchesFound(OModel model, List<IRI> matches) {

		for (ORMonitor monitor : monitors) {

			monitor.onMatchesFound(model, matches);
		}
	}

	static void pollForMatcherDone(OModel model, OWLObject request) {

		for (ORMonitor monitor : monitors) {

			monitor.onMatcherDone(model, request);
		}
	}

	/**
	 * Method invoked immediately after a classification request has been
	 * received.
	 *
	 * @param model Relevant model
	 * @param request Received request
	 */
	protected abstract void onClassifierRequest(OModel model, OWLObject request);

	/**
	 * Method invoked immediately after operation to obtain inferred-types
	 *
	 * @param model Relevant model
	 * @param types Inferred types
	 */
	protected abstract void onTypesInferred(OModel model, Set<OWLClass> types);

	/**
	 * Method invoked immediately after operation to obtain suggested-types
	 *
	 * @param model Relevant model
	 * @param types Suggested types
	 */
	protected abstract void onTypesSuggested(OModel model, Set<OWLClass> types);

	/**
	 * Method invoked immediately after classification request has been
	 * processed.
	 *
	 * @param model Relevant model
	 * @param request Processed request
	 */
	protected abstract void onClassifierDone(OModel model, OWLObject request);

	/**
	 * Method invoked immediately after an instance-match request has
	 * been received.
	 *
	 * @param model Relevant model
	 * @param request Received request
	 */
	protected abstract void onMatcherRequest(OModel model, OWLObject request);

	/**
	 * Method invoked immediately after instance-match operation
	 *
	 * @param model Relevant model
	 * @param matches Matching instances
	 */
	protected abstract void onMatchesFound(OModel model, List<IRI> matches);

	/**
	 * Method invoked immediately after instance-match request has
	 * been processed.
	 *
	 * @param model Relevant model
	 * @param request Processed request
	 */
	protected abstract void onMatcherDone(OModel model, OWLObject request);

	/**
	 * Method invoked immediately after monitoring has stopped.
	 */
	protected abstract void onStop();
}
