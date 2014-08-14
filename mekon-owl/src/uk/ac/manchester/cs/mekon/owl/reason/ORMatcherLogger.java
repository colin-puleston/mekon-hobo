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
 * Logger for {@link ORMatcher}. See {@link ORLogger} for usage
 * details.
 *
 * @author Colin Puleston
 */
public class ORMatcherLogger extends ORLogger {

	static private final ORLogger logger = new ORMatcherLogger();

	/**
	 * Provides the singleton instance of the logger.
	 *
	 * @return Singleton instance of logger
	 */
 	static public ORLogger get() {

		return logger;
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onClassifierRequest(OModel model, OWLObject request) {
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onTypesInferred(OModel model, Set<OWLClass> types) {
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onTypesSuggested(OModel model, Set<OWLClass> types) {
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onClassifierDone(OModel model, OWLObject request) {
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onMatcherRequest(OModel model, OWLObject request) {

		onRequest(model, request, "Matching-Individuals");
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onMatchesFound(OModel model, Set<OWLNamedIndividual> matches) {

		onReasoned(model, matches, "Matches");
	}

 	/**
	 * {@inheritDoc}
	 */
 	protected void onMatcherDone(OModel model, OWLObject request) {

		onRequestComplete();
	}
}
