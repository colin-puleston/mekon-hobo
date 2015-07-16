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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Abstract base-class for OWL-based implementations of the reasoning
 * mechanisms defined by {@link IMatcher}.
 * <p>
 * The matching process can be customised in two distinct ways:
 * <ul>
 *   <li>Overriding {@link #match} method
 *   <li>Adding one or more pre-processors, via {@link #addPreProcessor}
 * </ul>
 *
 * @author Colin Puleston
 */
public abstract class ORMatcher implements IMatcher {

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
	private OConceptFinder concepts;
	private FramesManager framesManager;

	/**
	 * Registers a pre-processor to perform certain required
	 * pre-classification modifications to appropriate
	 * representations of instances that are about to be classified.
	 *
	 * @param preProcessor Pre-processor for instances about to be
	 * classified
	 */
	public void addPreProcessor(ORPreProcessor preProcessor) {

		framesManager.addPreProcessor(preProcessor);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean handlesType(CFrame type) {

		return concepts.getSubsumerOrNull(type) != null;
	}

	/**
	 * Converts the specified instance-level frame to the
	 * pre-processable version, runs any registered pre-processors
	 * over it, then adds it to the matcher via the {@link #addInstance}
	 * method, whose specific implementations are provided by the
	 * extending classes.
	 *
	 * @param instance Representation of instance to be added
	 * @param identity Unique identity for instance
	 * @return True if instance added, false if instance with specified
	 * identity already present
	 */
	public synchronized boolean add(IFrame instance, CIdentity identity) {

		if (containsInstance(identity)) {

			return false;
		}

		addInstance(toPreProcessed(instance), identity);

		return true;
	}

	/**
	 * Removes the specified instance from the matcher via the
	 * {@link #removeInstance} method, whose specific implementations
	 * are provided by the extending classes.
	 *
	 * @param identity Unique identity of instance to be removed
	 * @return True if instance removed, false if instance with specified
	 * identity not present
	 */
	public synchronized boolean remove(CIdentity identity) {

		return removeInstance(identity);
	}

	/**
	 * Converts the specified instance-level query frame to the
	 * pre-processable version, runs any registered pre-processors
	 * over it, then performs the query-matching operation via the
	 * {@link #match(ORFrame)} method, whose specific implementations
	 * are provided by the extending classes.
	 *
	 * @param query Representation of query
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(IFrame query) {

		return new IMatches(match(toPreProcessed(query)));
	}

	/**
	 * Converts the specified instance-level query and instance frames
	 * to the pre-processable versions, runs any registered
	 * pre-processors over them, then performs a single query-matching
	 * test via the {@link #matches(ORFrame, ORFrame)} method, whose
	 * specific implementations are provided by the extending classes.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return matches(toPreProcessed(query), toPreProcessed(instance));
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
	 * Provides the object used to specify the semantics that will
	 * apply to specific slots from the incoming frames-based
	 * instances.
	 *
	 * @return Object for specifying slot-semantics to be applied
	 * by matcher
	 */
	public ORSlotSemantics getSlotSemantics() {

		return framesManager.getSlotSemantics();
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

		this.model = model;

		concepts = new OConceptFinder(model);
		framesManager = new FramesManager(model);

		new ORMatcherConfig(parentConfigNode).configure(this);
	}

	/**
	 * Adds the specified instance-level frame to the matcher.
	 *
	 * @param instance Representation of instance to be added
	 * @param identity Unique identity for instance
	 */
	protected abstract void addInstance(ORFrame instance, CIdentity identity);

	/**
	 * Removes the specified instance from the matcher.
	 *
	 * @param identity Unique identity of instance to be removed
	 * @return True if instance removed, false if instance with specified
	 * identity not present
	 */
	protected abstract boolean removeInstance(CIdentity identity);

	/**
	 * Tests whether an instance with the specified identity is currently
	 * present in the matcher.
	 *
	 * @return True if specified instance present
	 */
	protected abstract boolean containsInstance(CIdentity identity);

	/**
	 * Performs the query-matching operation.
	 *
	 * @param query Representation of query
	 * @return Unique identities of all matching instances
	 */
	protected abstract List<CIdentity> match(ORFrame query);

	/**
	 * Performs a single query-matching test.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	protected abstract boolean matches(ORFrame query, ORFrame instance);

	private ORFrame toPreProcessed(IFrame frame) {

		return framesManager.toPreProcessed(frame);
	}
}
