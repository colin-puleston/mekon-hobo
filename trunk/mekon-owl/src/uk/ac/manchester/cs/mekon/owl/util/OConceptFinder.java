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

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Responsible for finding IRIs of OWL concepts that correspond
 * to particular concept-level frames, or their ancestors.
 *
 * @author Colin Puleston
 */
public class OConceptFinder extends OEntityFinder<CFrame> {

	public OConceptFinder(OModel model) {

		super(model.getConcepts().getAllIRIs());
	}

	/**
	 * Provides the IRI of the OWL entity that corresponds to either
	 * the specified concept-level frame, or, if no such OWL entity,
	 * to the closest ancestor frame for which a corresponding OWL
	 * entity does exist.
	 *
	 * @param cEntity Frame for which IRI is required
	 * @return Relevant IRI, or null if no OWL concept corresponding
	 * to specified frame or any of it's ancestors
	 */
	public IRI getOrAncestorOrNull(CFrame cFrame) {

		IRI iri = getOrNull(cFrame);

		if (iri == null) {

			for (CFrame sup : cFrame.getSupers()) {

				iri = getOrAncestorOrNull(sup);

				if (iri != null) {

					break;
				}
			}
		}

		return iri;
	}
}
