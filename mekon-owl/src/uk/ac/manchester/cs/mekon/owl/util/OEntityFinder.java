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

package uk.ac.manchester.cs.mekon.owl.util;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for finding IRIs of OWL entities that correspond
 * to particular concept-level Frames Model (FM) entities of
 * specific types.
 *
 * @author Colin Puleston
 */
public abstract class OEntityFinder<CE extends CIdentified> {

	private Set<IRI> entityIRIs;

	/**
	 * Provides the IRI of the OWL entity that corresponds to the
	 * specified concept-level FM entity.
	 *
	 * @param cEntity FM entity for which IRI is required
	 * @return Relevant IRI, or null if no OWL entity corresponding
	 * to specified FM entity
	 */
	public IRI getOrNull(CE cEntity) {

		IRI iri = O_IRIExtractor.extractIRI(cEntity);

		return iri != null && entityIRIs.contains(iri) ? iri : null;
	}

	OEntityFinder(Set<IRI> entityIRIs) {

		this.entityIRIs = entityIRIs;
	}
}
