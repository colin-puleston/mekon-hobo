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

import org.semanticweb.owlapi.model.*;

/**
 * Specification of an axiom-purge operation. Defines those axioms
 * that are to be retained after the purge.
 *
 * @author Colin Puleston
 */
public interface OAxiomPurgeSpec {

	/**
	 * Specifies whether any sub-class axioms concerning pairs of
	 * retained concepts should be retained.
	 *
	 * @return true if relevant sub-class axioms should be retained
	 */
	public boolean retainConceptHierarchy();

	/**
	 * Tests whether the declaration axiom for the specified concept
	 * should be retained.
	 *
	 * @param iri IRI of concept
	 * @return true if declaration axiom should be retained
	 */
	public boolean retainConcept(IRI iri);

	/**
	 * Tests whether the declaration axiom for the specified object
	 * or data property should be retained.
	 *
	 * @param iri IRI of property
	 * @return true if declaration axiom should be retained
	 */
	public boolean retainProperty(IRI iri);
}
