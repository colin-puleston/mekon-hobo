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

package uk.ac.manchester.cs.mekon.owl;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.reasoner.*;

/**
 * Monitor for loading of the OWL ontology and loading of the
 * reasoner.
 * <p>
 * The class maintains a static list of current monitors, whose
 * appropriate methods will be invoked at the relevant points in
 * the build process.
 *
 * @author Colin Puleston
 */
public abstract class OMonitor {

	static private List<OMonitor> monitors = new ArrayList<OMonitor>();

	/**
	 * Starts monitoring with the specified version of the monitor.
	 *
	 * @param monitor Version of monitor to use
	 */
	static public void start(OMonitor monitor) {

		monitors.add(monitor);
	}

	/**
	 * Stops monitoring with the specified version of the monitor.
	 *
	 * @param monitor Relevant version of monitor
	 */
	static public void stop(OMonitor monitor) {

		monitors.remove(monitor);
	}

	static synchronized void pollForPreOntologyLoad(File owlFile) {

		for (OMonitor monitor : monitors) {

			monitor.onPreOntologyLoad(owlFile);
		}
	}

	static synchronized void pollForOntologyLoaded() {

		for (OMonitor monitor : monitors) {

			monitor.onOntologyLoaded();
		}
	}

	static synchronized void pollForPreReasonerLoad(Class<? extends OWLReasoner> reasonerClass) {

		for (OMonitor monitor : monitors) {

			monitor.onPreReasonerLoad(reasonerClass);
		}
	}

	static synchronized void pollForReasonerLoaded() {

		for (OMonitor monitor : monitors) {

			monitor.onReasonerLoaded();
		}
	}

	/**
	 * Method invoked immediately before loading of ontolgy.
	 *
	 * @param owlFile File from which ontology is to be loaded
	 */
	protected abstract void onPreOntologyLoad(File owlFile);

	/**
	 * Method invoked immediately after loading of ontolgy.
	 */
	protected abstract void onOntologyLoaded();

	/**
	 * Method invoked immediately before creation and intitialisation
	 * of reasoner.
	 *
	 * @param reasonerClass Type of reasoner to be loaded
	 */
	protected abstract void onPreReasonerLoad(Class<? extends OWLReasoner> reasonerClass);

	/**
	 * Method invoked immediately after creation and intitialisation
	 * of reasoner.
	 */
	protected abstract void onReasonerLoaded();
}
