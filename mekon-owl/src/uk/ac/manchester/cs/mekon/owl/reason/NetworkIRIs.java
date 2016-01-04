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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Utility for extracting IRIs for the concepts and properties
 * associated with entities in the node/link network representation
 * as provided by the {@link uk.ac.manchester.cs.mekon.network}
 * package.
 *
 * @author Colin Puleston
 */
public class NetworkIRIs {

	/**
	 * Extracts IRI of concept associated with specified network
	 * entity, whose associated concept must be atomic, and which
	 * must have an identifier that is a valid IRI.
	 *
	 * @param entity Entity for which IRI is to be extracted
	 * @return Extracted IRI
	 */
	static public IRI getAtomicType(NEntity entity) {

		return get(entity.getType());
	}

	/**
	 * Extracts IRI of type-disjuncts associated with specified
	 * node, each of which must have an identifier that is a valid
	 * IRI.
	 *
	 * @param node Node for which IRIs are to be extracted
	 * @return Extracted IRIs
	 */
	static public List<IRI> getTypeDisjuncts(NNode node) {

		List<IRI> iris = new ArrayList<IRI>();

		for (CIdentity typeDisjunct : node.getTypeDisjuncts()) {

			iris.add(get(typeDisjunct));
		}

		return iris;
	}

	static private IRI get(CIdentity type) {

		IRI iri = O_IRIExtractor.extractIRI(type);

		if (iri == null) {

			throw new Error("Should never happen!");
		}

		return iri;
	}
}
