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

package uk.ac.manchester.cs.mekon.owl.triples;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;

/**
 * Extension of {@link ORMatcher} that represents the instances
 * via sets of triples in an RDF triple store, and the queries as
 * <i>SPARQL</i> queries over that store. The store will be
 * dynamically populated, and possibly created, on start-up, and
 * emptied or removed on termination.
 * <p>
 * This is an abstract class each of whose extensions will provide
 * an implementation for a particular type of triple store.
 *
 * @author Colin Puleston
 */
public abstract class OTMatcher extends ORMatcher {

	private Store store = null;

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 */
	protected OTMatcher(OModel model) {

		super(model);
	}

	/**
	 * Constructs matcher, with the configuration for both the
	 * matcher itself, and the model over which it is to operate,
	 * defined via the appropriately-tagged child of the specified
	 * parent configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	protected OTMatcher(KConfigNode parentConfigNode) {

		super(parentConfigNode);
	}

	/**
	 * Constructs matcher for specified model, with the configuration
	 * defined via the appropriately-tagged child of the specified parent
	 * configuration-node.
	 *
	 * @param model Model over which matcher is to operate
	 * @param parentConfigNode Parent configuration-node
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	protected OTMatcher(OModel model, KConfigNode parentConfigNode) {

		super(model, parentConfigNode);
	}

	/**
	 * Specifies that referenced instances are not to be expanded.
	 *
	 * @return False since referenced instances are not to be expanded
	 */
	protected boolean expandInstanceRefs() {

		return false;
	}

	/**
	 */
	protected void addToOWLStore(NNode instance, IRI iri) {

		store.add(instance, iri);
	}

	/**
	 */
	protected void removeFromOWLStore(IRI iri) {

		store.remove(iri);
	}

	/**
	 */
	protected List<IRI> matchInOWLStore(NNode query) {

		return store.match(query);
	}

	/**
	 */
	protected boolean matchesInOWL(NNode query, NNode instance) {

		return store.matches(query, instance);
	}

	/**
	 * Method that should be invoked by extension-classes in
	 * order to perform necessary post-construction initialisations
	 * of the matcher.
	 *
	 * @param factory Implementation-specific data-factory
	 */
	protected void initialise(OTFactory factory) {

		store = new Store(factory);
	}
}
