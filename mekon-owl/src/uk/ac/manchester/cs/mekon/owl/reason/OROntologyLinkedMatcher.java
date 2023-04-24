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
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Abstract base-class for OWL ontology-linked implementations of
 * the matching mechanisms defined by {@link IMatcher}.
 * <p>
 * This class is an abstract extention of {@link NMatcher}, and hence
 * operates on pre-processable instantiations of the network-based
 * representations. These network-based instantiations are passed to
 * appropriate abstract methods, whose implementations will convert
 * them to appropriate OWL constructs and perform the required
 * reasoning operations.
 * <p>
 * Before being passed on to the abstract methods, the network-based
 * instantiations are pre-processed to ensure "ontology-compliance".
 * This ensures that they will only contain entities for which
 * equivalents exist in, or for which substitutions can be made from,
 * the relevant ontology. Hence, any nodes whose associated concepts
 * do not have equivalents in the ontology, will either be modified to
 * reference appropriate ancestor-concepts (as determined by looking
 * at the frames model), or else removed from the network. Also, any
 * links whose associated properties do not have equivalents in the
 * ontology will be removed from the network.
 *
 * @author Colin Puleston
 */
public abstract class OROntologyLinkedMatcher extends NMatcher {

	private OModel model;

	private OntologyEntityResolver entityResolver;
	private OStoredInstanceIRIs instanceIRIs = new OStoredInstanceIRIs();

	/**
	 * Checks whether the matcher handles instance-level frames
	 * of the specified type, by testing whether that type or any
	 * of it's ancestors corresponds to a class in the OWL model.
	 *
	 * @param type Relevant frame-type
	 * @return True if type or any ancestor corresponds to OWL class
	 */
	public boolean handlesType(CFrame type) {

		return entityResolver.canResolve(type);
	}

	/**
	 * Processes the specified network-based instance representation
	 * to ensure ontology-compliance (see above), converts the
	 * specified instance-identity to an appropriate <code>IRI</code>
	 * then invokes {@link #addToOntologyLinkedStore} to perform the
	 * add operation.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(NNode instance, CIdentity identity) {

		entityResolver.resolve(instance);

		addToOntologyLinkedStore(instance, instanceIRIs.mapToIRI(identity));
	}

	/**
	 * Converts the specified instance-identity to the appropriate
	 * <code>IRI</code> then invokes {@link #removeFromOntologyLinkedStore}
	 * to perform the remove operation.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity) {

		removeFromOntologyLinkedStore(instanceIRIs.mapToIRI(identity));
	}

	/**
	 * Processes the specified network-based query representation
	 * to ensure ontology-compliance (see above), then invokes
	 * {@link #matchInOntologyLinkedStore} to perform the matching
	 * operation.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(NNode query) {

		entityResolver.resolve(query);

		List<IRI> iris = matchInOntologyLinkedStore(query);

		return new IUnrankedMatches(instanceIRIs.getMappedIds(iris));
	}

	/**
	 * Processes the specified network-based query and instance
	 * representations to ensure ontology-compliance (see above),
	 * then invokes {@link #matchesInOntologyLinkedStore} to perform
	 * the match-testing operation.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	public boolean matches(NNode query, NNode instance) {

		entityResolver.resolve(query);
		entityResolver.resolve(instance);

		return matchesWithRespectToOntology(query, instance);
	}

	/**
	 * Does nothing since no clear-ups are required for this type
	 * of store.
	 */
	public void stop() {
	}

	/**
	 * Provides the model over which the matcher is operating.
	 *
	 * @return Model over which matcher is operating
	 */
	public OModel getModel() {

		return model;
	}

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 */
	protected OROntologyLinkedMatcher(OModel model) {

		initialiseLinkedMatcher(model);
	}

	/**
	 * Adds an instance to the OWL store. It can be assumed that this
	 * method will only be invoked when it is known that an instance
	 * with the specified IRI is not already present.
	 *
	 * @param instance Representation of instance to be added
	 * @param iri IRI of instance to be added
	 */
	protected abstract void addToOntologyLinkedStore(NNode instance, IRI iri);

	/**
	 * Removes an instance from the OWL store. It can be assumed that
	 * this method will only be invoked when it is known that an
	 * instance with the specified IRI is currently present.
	 *
	 * @param iri IRI of instance to be removed
	 */
	protected abstract void removeFromOntologyLinkedStore(IRI iri);

	/**
	 * Performs the matching operation against the OWL store.
	 *
	 * @param query Representation of query
	 * @return Unique identities of all matching instances
	 */
	protected abstract List<IRI> matchInOntologyLinkedStore(NNode query);

	/**
	 * Performs the matching test using the OWL reasoner.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	protected abstract boolean matchesWithRespectToOntology(NNode query, NNode instance);

	OROntologyLinkedMatcher() {
	}

	void initialiseLinkedMatcher(OModel model) {

		this.model = model;

		entityResolver = new OntologyEntityResolver(model);
	}

	boolean instanceIRI(IRI iri) {

		return instanceIRIs.mappedIRI(iri);
	}
}
