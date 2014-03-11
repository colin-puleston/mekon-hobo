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

import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Logger that prints out timing information for the OWL
 * ontology and reasoner loading operations.
 *
 * @author Colin Puleston
 */
public class OLogger extends OMonitor {

	static private OLogger logger = null;

	private OActionLogger actions = new OActionLogger();

	/**
	 * Starts load logging.
	 */
	static public void start() {

		logger = new OLogger();
		OMonitor.start(logger);
	}

	/**
	 * Stops load logging.
	 */
	static public void stop() {

		if (logger != null) {

			OMonitor.stop(logger);
			logger = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
 	protected void onPreOntologyLoad(File owlFile) {

		onPreLoad("Ontology", "OWL-File", owlFile.getPath());
	}

	/**
	 * {@inheritDoc}
	 */
 	protected void onOntologyLoaded() {

		onLoaded("Ontology");
	}

	/**
	 * {@inheritDoc}
	 */
 	protected void onPreReasonerLoad(Class<? extends OWLReasoner> reasonerClass) {

		onPreLoad("Reasoner", "Reasoner-Factory", reasonerClass.getName());
	}

	/**
	 * {@inheritDoc}
	 */
 	protected void onReasonerLoaded() {

		onLoaded("Reasoner");
	}

 	void onPreLoad(String thingName, String sourceName, String source) {

		actions.printTitle("Loading " + thingName);
		actions.printAttribute(sourceName, source);
		actions.startAction();
	}

 	void onLoaded(String thingName) {

		actions.stopAction();
		actions.printTitle("Loaded " + thingName);
		actions.printLastActionTime("Time");
	}
}
