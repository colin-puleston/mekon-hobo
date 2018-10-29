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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OBAxiomPurgeSpec implements OAxiomPurgeSpec {

	private boolean retainConceptHierarchy;

	private Set<IRI> conceptIRIs;
	private Set<IRI> propertyIRIs;

	public boolean retainConceptHierarchy() {

		return retainConceptHierarchy;
	}

	public boolean retainConcept(IRI iri) {

		return conceptIRIs.contains(iri);
	}

	public boolean retainProperty(IRI iri) {

		return propertyIRIs.contains(iri);
	}

	OBAxiomPurgeSpec(
		boolean retainConceptHierarchy,
		OBConcepts concepts,
		OBProperties properties) {

		this.retainConceptHierarchy = retainConceptHierarchy;

		conceptIRIs = toIRIs(concepts);
		propertyIRIs = toIRIs(properties);
	}

	private Set<IRI> toIRIs(OBEntities<?, ?, ?> entities) {

		Set<IRI> iris = new HashSet<IRI>();

		for (OWLEntity entity : entities.getAll()) {

			iris.add(entity.getIRI());
		}

		return iris;
	}
}
