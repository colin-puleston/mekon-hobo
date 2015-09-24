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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Manages the assignment of unique IRIs to a set of OWL-based
 * representaions of MEKON frames-model instantiations.
 *
 * @author Colin Puleston
 */
public class OInstanceIRIs {

	static private final String BASE_NAMESPACE = "urn:mekon-owl:instances";
	static private final String DYNAMIC_INSTANCES_NAMESPACE_EXTN = ":dynamic";

	/**
	 * Tests whether an IRI has the format that would be generated
	 * by this class generate for a static instance.
	 *
	 * @param iri IRI to test
	 * @return true if IRI has relevant format
	 */
	static public boolean staticInstanceIRI(IRI iri) {

		return instanceIRI(iri, BASE_NAMESPACE);
	}

	/**
	 * Tests whether an IRI has the format that would be generated
	 * by this class generate for a dynamic instance.
	 *
	 * @param iri IRI to test
	 * @return true if IRI has relevant format
	 */
	static public boolean dynamicInstanceIRI(IRI iri) {

		return instanceIRI(iri, BASE_NAMESPACE);
	}

	static private boolean instanceIRI(IRI iri, String testNamespace) {

		return iri.toString().startsWith(testNamespace + "#");
	}

	private String namespace;
	private InstanceIndexes indexes = new InstanceIndexes();

	private class InstanceIndexes extends KIndexes<CIdentity> {

		protected KRuntimeException createException(String message) {

			return new KSystemConfigException(message);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param dynamicInstances True if IRIs will be generated with the
	 * dynamic-instances namespace, rather than the default namespace
	 */
	public OInstanceIRIs(boolean dynamicInstances) {

		namespace = BASE_NAMESPACE;

		if (dynamicInstances) {

			namespace += DYNAMIC_INSTANCES_NAMESPACE_EXTN;
		}
	}

	/**
	 * Assigns a unique IRI to an unspecified instance.
	 *
	 * @return Assigned IRI
	 */
	public IRI assign() {

		return create(indexes.assignIndex());
	}

	/**
	 * Assigns a unique IRI to an instance.
	 *
	 * @param instance Identity of instance for which IRI is required
	 * @return Assigned IRI
	 */
	public IRI assign(CIdentity identity) {

		return create(indexes.assignIndex(identity));
	}

	/**
	 * Frees up a unique IRI that was previously assigned to a specific
	 * instance.
	 *
	 * @element Identity of instance for which IRI is no longer required
	 * @return Freed IRI
	 */
	public IRI free(CIdentity identity) {

		IRI iri = get(identity);

		indexes.freeIndex(identity);

		return iri;
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
	 * Retrieves the currently assigned IRI for an instance.
	 *
	 * @param identity Identity of instance for which IRI is required
	 * @return Currently assigned IRI
	 */
	public IRI get(CIdentity identity) {

		return create(indexes.getIndex(identity));
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

		return IRI.create(namespace + "#" + index.toString());
	}

	private int extractIndex(IRI iri) {

		return Integer.parseInt(iri.getFragment());
	}
}
