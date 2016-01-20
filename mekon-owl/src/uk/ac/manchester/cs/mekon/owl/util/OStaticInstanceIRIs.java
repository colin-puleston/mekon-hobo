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
import uk.ac.manchester.cs.mekon.store.motor.*;

/**
 * Manages the assignment of unique IRIs to a set of OWL-based
 * representaions of static MEKON frames-model instantiations.
 *
 * @author Colin Puleston
 */
public class OStaticInstanceIRIs extends OInstanceIRIs {

	static private final String NAMESPACE_EXTN = "";

	/**
	 * Tests whether an IRI has the format that would be generated
	 * by this class for a static instance.
	 *
	 * @param iri IRI to test
	 * @return true if IRI has relevant format
	 */
	static public boolean staticInstance(IRI iri) {

		return instanceIRI(iri, NAMESPACE_EXTN);
	}

	private IMatcherIndexes indexes = new LocalIndexes();

	/**
	 * Resets the index manager used in generating the IRIs. By default
	 * a locally created manager will be used.
	 *
	 * @return Index manager to be used
	 */
	public void resetIndexes(IMatcherIndexes indexes) {

		this.indexes = indexes;
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

	String getNamespaceExtn() {

		return NAMESPACE_EXTN;
	}

	CIdentity toIdentity(int index) {

		return indexes.getIdentity(index);
	}
}
