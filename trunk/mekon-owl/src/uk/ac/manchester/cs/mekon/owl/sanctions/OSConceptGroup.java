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

package uk.ac.manchester.cs.mekon.owl.sanctions;

import org.semanticweb.owlapi.model.*;

/**
 * Represents a set of OWL classes defined via a single root-class.
 * The set will include all descendant classes of the root-class,
 * and optionally the root-class itself.
 *
 * @author Colin Puleston
 */
public class OSConceptGroup extends OSEntityGroup {

	private OSConceptHiding hiding = new OSConceptHiding();

	/**
	 * Constructor.
	 *
	 * @param rootConceptIRI IRI of root-class
	 */
	public OSConceptGroup(IRI rootConceptIRI) {

		super(rootConceptIRI);
	}

	/**
	 * Provides the object that specifies for which concepts in the
	 * group the generated frames will be "hidden" frames.
	 *
	 * @return Object specifying concept-hiding for group
	 */
	public OSConceptHiding getConceptHiding() {

		return hiding;
	}
}
