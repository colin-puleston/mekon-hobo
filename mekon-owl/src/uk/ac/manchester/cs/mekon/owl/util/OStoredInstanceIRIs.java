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
import uk.ac.manchester.cs.mekon.config.*;

/**
 * Manages the assignment of unique IRIs to a set of OWL-based
 * representaions of stored MEKON frames-model instantiations.
 *
 * @author Colin Puleston
 */
public class OStoredInstanceIRIs {

	static private final String DEFAULT_NAMESPACE
			= O_IRINamespaces.createNamespace("generated-instance-uris");

	/**
	 * Provides a unique IRI to be used in representing the specified
	 * instance. This IRI will either be derived directly from the
	 * instance identifier or, if the identifier is not a valid
	 * absolute IRI, will be created from the identifier in combination
	 * with a default-namespace.
	 *
	 * @param identity Identity of instance for which IRI is required
	 * @return Appropriate IRI
	 */
	static public IRI toIRI(CIdentity identity) {

		return O_IRIExtractor.extractIRI(identity, DEFAULT_NAMESPACE);
	}

	private Map<IRI, CIdentity> irisToIds = new HashMap<IRI, CIdentity>();

	/**
	 * Provides the unique IRI that will be used to represent the
	 * specified instance, as supplied via the {@link #toIRI} method,
	 * and ensures that the relevant mapping is registered to enable
	 * subsequent retrieval of the identity from the IRI.
	 *
	 * @param identity Identity of instance for which IRI is required
	 * @return Appropriate IRI
	 */
	public IRI mapToIRI(CIdentity identity) {

		IRI iri = toIRI(identity);

		if (!irisToIds.containsKey(iri)) {

			irisToIds.put(iri, identity);
		}

		return iri;
	}

	/**
	 * Tests whether the supplied IRI has previously been mapped to an
	 * instance-identity via the {@link #mapToIRI} method.
	 *
	 * @param iri IRI to test for
	 * @return True if mapping found for IRI
	 */
	public boolean mappedIRI(IRI iri) {

		return irisToIds.containsKey(iri);
	}

	/**
	 * Retrieves the set of instance-identity that was previously
	 * mapped to the supplied IRI.
	 *
	 * @param iri IRI for which instance-identity is required
	 * @return Relevant instance-identity
	 */
	public CIdentity getMappedId(IRI iri) {

		CIdentity identity = irisToIds.get(iri);

		if (identity != null) {

			return identity;
		}

		throw new KSystemConfigException(
					"IRI does not correspond to a stored instance: "
					+ iri);
	}

	/**
	 * Retrieves the set of instance-identities that have previously
	 * been mapped to the members the supplied set of IRIs.
	 *
	 * @param iris IRIs for which instance-identities are required
	 * @return Relevant set of instance-identities
	 */
	public List<CIdentity> getMappedIds(List<IRI> iris) {

		List<CIdentity> identities = new ArrayList<CIdentity>();

		for (IRI iri : iris) {

			identities.add(getMappedId(iri));
		}

		return identities;
	}
}
