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
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Manages the assignment of unique IRIs to a set of OWL-based
 * representaions of dynamic MEKON frames-model instantiations.
 *
 * @author Colin Puleston
 */
public class ODynamicInstanceIRIs {

	static private final String NAMESPACE_EXTENSION = "dynamic-instances";

	/**
	 * Tests whether an IRI has the format that would be generated
	 * by this class.
	 *
	 * @param iri IRI to test
	 * @return true if IRI has relevant format
	 */
	static public boolean dynamicInstance(IRI iri) {

		return O_IRINamespaces.entityInNamespace(iri, NAMESPACE_EXTENSION);
	}

	private KIndexes<CIdentity> indexes = new LocalIndexes();

	/**
	 * Assigns a unique IRI to an unspecified instance.
	 *
	 * @return Assigned IRI
	 */
	public IRI assign() {

		return create(indexes.assignIndex());
	}

	/**
	 * Frees up a unique IRI that was previously assigned to an
	 * unspecified instance.
	 *
	 * @param iri IRI to be freed
	 */
	public void free(IRI iri) {

		indexes.freeIndex(extractIndex(iri));
	}

	/**
	 * Retrieves the identity of the instance to which an IRI is
	 * currently assigned.
	 *
	 * @param iri IRI for which instance identity is required
	 * @return Identity of relevant instance
	 */
	public CIdentity toIdentity(IRI iri) {

		return indexes.getElement(extractIndex(iri));
	}

	/**
	 * Retrieves the identities of the instances to which the members
	 * of a set of IRIs are currently assigned.
	 *
	 * @param iris IRIs for which instance identities are required
	 * @return Identities of relevant instances
	 */
	public List<CIdentity> toIdentities(List<IRI> iris) {

		List<CIdentity> identities = new ArrayList<CIdentity>();

		for (IRI iri : iris) {

			identities.add(toIdentity(iri));
		}

		return identities;
	}

	private IRI create(Integer index) {

		return O_IRINamespaces.createEntityIRI(NAMESPACE_EXTENSION, index.toString());
	}

	private int extractIndex(IRI iri) {

		return Integer.parseInt(iri.toURI().getFragment());
	}
}
