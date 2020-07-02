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
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * Abstract base-class for OWL-based implementations of the matching
 * mechanisms defined by {@link IMatcher}.
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
public abstract class ORMatcher extends NMatcher {

	/**
	 * Test whether an appropriately-tagged child of the specified
	 * parent configuration-node exists, defining the configuration
	 * for an {@link ORMatcher} to be created.
	 *
	 * @param parentConfigNode Parent configuration-node
	 * @return True if required child node exists
	 */
	static public boolean configExists(KConfigNode parentConfigNode) {

		return ORMatcherConfig.configNodeExists(parentConfigNode);
	}

	/**
	 * Constructs matcher, with the configuration for both the
	 * matcher itself, and the model over which it is to operate,
	 * defined via the appropriately-tagged child of the specified
	 * parent configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @return Created object
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	static public ORMatcher create(KConfigNode parentConfigNode) {

		return new ORMatcherCreator(parentConfigNode).create();
	}

	/**
	 * Constructs matcher, with the configuration defined via the
	 * appropriately-tagged child of the specified parent
	 * configuration-node.
	 *
	 * @param model Model over which matcher is to operate
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @return Created object
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	static public ORMatcher create(OModel model, KConfigNode parentConfigNode) {

		return new ORMatcherCreator(parentConfigNode).create(model);
	}

	static private OModel createModel(KConfigNode parentConfigNode) {

		return new OModelBuilder(parentConfigNode).create(true);
	}

	private ReasoningModel reasoningModel;
	private OStoredInstanceIRIs instanceIRIs = new OStoredInstanceIRIs();

	/**
	 * Provides the model over which the matcher is operating.
	 *
	 * @return Model over which matcher is operating
	 */
	public OModel getModel() {

		return reasoningModel.getModel();
	}

	/**
	 * Checks whether the matcher handles instance-level frames
	 * of the specified type, by testing whether that type or any
	 * of it's ancestors corresponds to a class in the OWL model.
	 *
	 * @param type Relevant frame-type
	 * @return True if type or any ancestor corresponds to OWL class
	 */
	public boolean handlesType(CFrame type) {

		return reasoningModel.canResolveOntologyEntities(type);
	}

	/**
	 * Processes the specified network-based instance representation
	 * to ensure ontology-compliance (see above), converts the
	 * specified instance-identity to an appropriate <code>IRI</code>
	 * then invokes {@link #addToOWLStore} to perform the add operation.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(NNode instance, CIdentity identity) {

		reasoningModel.resolveOntologyEntities(instance);

		addToOWLStore(instance, instanceIRIs.mapToIRI(identity));
	}

	/**
	 * Converts the specified instance-identity to the appropriate
	 * <code>IRI</code> then invokes {@link #removeFromOWLStore} to
	 * perform the remove operation.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity) {

		removeFromOWLStore(instanceIRIs.mapToIRI(identity));
	}

	/**
	 * Processes the specified network-based query representation
	 * to ensure ontology-compliance (see above), then invokes
	 * {@link #matchInOWLStore} to perform the matching operation.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(NNode query) {

		reasoningModel.resolveOntologyEntities(query);

		List<IRI> iris = matchInOWLStore(query);

		return new IUnrankedMatches(instanceIRIs.getMappedIds(iris));
	}

	/**
	 * Processes the specified network-based query and instance
	 * representations to ensure ontology-compliance (see above),
	 * then invokes {@link #matchesInOWL} to perform the match-testing
	 * operation.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	public boolean matches(NNode query, NNode instance) {

		reasoningModel.resolveOntologyEntities(query);
		reasoningModel.resolveOntologyEntities(instance);

		return matchesInOWL(query, instance);
	}

	/**
	 * Does nothing since no clear-ups are required for this type
	 * of store.
	 */
	public void stop() {
	}

	/**
	 * Constructs matcher for specified model and reasoning-type.
	 *
	 * @param model Model over which matcher is to operate
	 */
	protected ORMatcher(OModel model) {

		this(model, false, null);
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
	protected ORMatcher(KConfigNode parentConfigNode) {

		this(createModel(parentConfigNode), true, parentConfigNode);
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
	protected ORMatcher(OModel model, KConfigNode parentConfigNode) {

		this(model, false, parentConfigNode);
	}

	/**
	 * Adds an instance to the OWL store. It can be assumed that this
	 * method will only be invoked when it is known that an instance
	 * with the specified IRI is not already present.
	 *
	 * @param instance Representation of instance to be added
	 * @param iri IRI of instance to be added
	 */
	protected abstract void addToOWLStore(NNode instance, IRI iri);

	/**
	 * Removes an instance from the OWL store. It can be assumed that
	 * this method will only be invoked when it is known that an
	 * instance with the specified IRI is currently present.
	 *
	 * @param iri IRI of instance to be removed
	 */
	protected abstract void removeFromOWLStore(IRI iri);

	/**
	 * Performs the matching operation against the OWL store.
	 *
	 * @param query Representation of query
	 * @return Unique identities of all matching instances
	 */
	protected abstract List<IRI> matchInOWLStore(NNode query);

	/**
	 * Performs the matching test using the OWL reasoner.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	protected abstract boolean matchesInOWL(NNode query, NNode instance);

	ReasoningModel getReasoningModel() {

		return reasoningModel;
	}

	boolean instanceIRI(IRI iri) {

		return instanceIRIs.mappedIRI(iri);
	}

	boolean requireLocalModel() {

		return false;
	}

	private ORMatcher(OModel model, boolean localModel, KConfigNode parentConfigNode) {

		reasoningModel = new ReasoningModel(model);

		if (parentConfigNode != null) {

			new ORMatcherConfig(reasoningModel, parentConfigNode);
		}

		if (requireLocalModel() && !localModel) {

			reasoningModel.ensureLocalModel();
		}
	}
}
