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
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Abstract base-class for OWL-based implementations of the reasoning
 * mechanisms defined by {@link IMatcher}.
 * <p>
 * The network representation of instances and queries that are passed
 * in to the methods implemented on this class, are processed to
 * ensure "ontology-compliance". This ensures that they will only contain
 * entities for which equivalents exist in, or for which substitutions
 * can be made from, the relevant ontology. Hence, any nodes whose
 * associated concepts do not have equivalents in the ontology, will
 * either be modified to reference appropriate ancestor-concepts (as
 * determined by looking at the frames model), or else removed from the
 * network. Also, any links whose associated properties do not have
 * equivalents in the ontology will be removed from the network.
 *
 * @author Colin Puleston
 */
public abstract class ORMatcher extends IMatcher {

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

	private OModel model;
	private ORReasoningType reasoningType;
	private ORSemantics semantics;

	private OConceptFinder concepts;
	private OntologyEntityResolver ontologyEntityResolver;
	private OInstanceIRIs instanceIRIs = new OInstanceIRIs(false);

	/**
	 * {@inheritDoc}
	 */
	public boolean handlesType(CFrame type) {

		return concepts.getSubsumerOrNull(type) != null;
	}

	/**
	 * Removes the specified instance from the matcher via the
	 * {@link #removeFromOWLStore} method, whose specific
	 * implementations are provided by the derived class.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity) {

		removeFromOWLStore(instanceIRIs.free(identity));
	}

	/**
	 * Processes the specified instance representation to ensure
	 * ontology-compliance (see above), then adds it to the matcher
	 * via the {@link #addToOWLStore} method, whose implementation
	 * is provided by the derived class.
	 *
	 * @param instance Representation of instance to be added
	 * @param identity Unique identity for instance
	 */
	public void addPreProcessed(NNode instance, CIdentity identity) {

		ontologyEntityResolver.resolve(instance);

		addToOWLStore(instance, instanceIRIs.assign(identity));
	}

	/**
	 * Processes the specified query representation to ensure
	 * ontology-compliance (see above), then performs the matching
	 * operation via the {@link #matchInOWLStore} method, whose
	 * implementation is provided by the derived class.
	 *
	 * @param query Representation of query
	 * @return Unique identities of all matching instances
	 */
	public IMatches matchPreProcessed(NNode query) {

		ontologyEntityResolver.resolve(query);

		List<IRI> iris = matchInOWLStore(query);

		return new IMatches(instanceIRIs.toIdentities(iris));
	}

	/**
	 * Processes the specified query and instance representations
	 * to ensure ontology-compliance (see above), then performs the
	 * single matching test via the {@link #matchesInOWL()} method,
	 * whose implementation is provided by the derived class.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	public boolean matchesPreProcessed(NNode query, NNode instance) {

		ontologyEntityResolver.resolve(query);
		ontologyEntityResolver.resolve(instance);

		return matchesInOWL(query, instance);
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
	/**
	 * Provides the object used to specify the open/closed world
	 * semantics to be embodied by the OWL constructs that will be
	 * created to represent instances being stored and queries being
	 * executed.
	 *
	 * @return Object for specifying open/closed world semantics
	 */
	public ORSemantics getSemantics() {

		return semantics;
	}

	/**
	 * Specifies the type of reasoning that the matcher is required
	 * to perform.
	 *
	 * @return Required reasoning-type
	 */
	public ORReasoningType getReasoningType() {

		return reasoningType;
	}

	/**
	 * Constructs matcher for specified model and reasoning-type.
	 *
	 * @param model Model over which matcher is to operate
	 * @param reasoningType Required reasoning-type for matching
	 */
	protected ORMatcher(OModel model, ORReasoningType reasoningType) {

		this.model = model;
		this.reasoningType = reasoningType;

		semantics = new ORSemantics(model);

		concepts = new OConceptFinder(model);
		ontologyEntityResolver = new OntologyEntityResolver(model, concepts);
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

		this(createModel(parentConfigNode), parentConfigNode);
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

		this(model, ORReasoningType.DL);

		new ORMatcherConfig(parentConfigNode).configure(this);
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

	void setReasoningType(ORReasoningType reasoningType) {

		this.reasoningType = reasoningType;
	}
}
