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

package uk.ac.manchester.cs.mekon.mechanism;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;

/**
 * Responsible for executing queries over sets of instantiations
 * of a MEKON Frames Model (FM). This is an abstract class that
 * leaves the implementatation of the actual matching mechanisms
 * to the derived class.
 * <p>
 * Both instances and queries are passed into the top-level methods
 * as instance-level frames. The frames representing instances will
 * always be of category {@link IFrameCategory#ASSERTION}, rather
 * than {@link IFrameCategory#QUERY}, whereas those representing
 * queries can be either, since assertion frames can also be
 * interpreted as queries.
 * <p>
 * The instance-level frames that are used by the top-level methods,
 * are converted into the intermediate network representations that
 * the abstract methods implemented by the derived classes operate on.
 * <p>
 * The matching process can be customised by adding one or more
 * pre-processors to modify the networks that will be passed to the
 * methods on the derived class (see {@link #addPreProcessor}) .
 *
 * @author Colin Puleston
 */
public abstract class IMatcher {

	private NNetworkManager networkManager = new NNetworkManager();

	/**
	 * Registers a pre-processor to perform certain required
	 * modifications to appropriate representations of instances that
	 * are about to be stored or queries that are about to be matched.
	 *
	 * @param preProcessor Pre-processor for instances and queries
	 */
	public void addPreProcessor(NNetworkProcessor preProcessor) {

		networkManager.addPreProcessor(preProcessor);
	}

	/**
	 * Checks whether the matcher handles instance-level frames
	 * of the specified type.
	 *
	 * @param type Relevant frame-type
	 * @return True if matcher handles specified type
	 */
	public abstract boolean handlesType(CFrame type);

	/**
	 * Converts the specified instance-level frame to the
	 * network-based version, runs any registered pre-processors
	 * over it, then adds it to the matcher via the
	 * {@link #add(NNode, CIdentity)} method, whose specific
	 * implementations are provided by the derived classes.
	 *
	 * @param instance Representation of instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(IFrame instance, CIdentity identity) {

		addPreProcessed(createNetwork(instance), identity);
	}

	/**
	 * Adds an instance to the matcher, after first running any
	 * registered pre-processors over it. It can be assumed that the
	 * instance is of an appropriate type (see {@link #handlesType}),
	 * and that an instance with the specified identity is not already
	 * present.
	 *
	 * @param instance Representation of instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(NNode instance, CIdentity identity) {

		preProcessNetwork(instance);
		addPreProcessed(instance, identity);
	}

	/**
	 * Removes an instance from the matcher. It can be assumed that
	 * this method will only be invoked when it is known that an
	 * instance with the specified identity is currently present.
	 *
	 * @param identity Unique identity of instance to be removed
	 * @return True if instance removed, false if instance with
	 * specified identity not present
	 */
	public abstract void remove(CIdentity identity);

	/**
	 * Converts the specified instance-level query frame to the
	 * network-based version, runs any registered pre-processors
	 * over it, then performs the query-matching operation via the
	 * {@link #match(NNode)} method, whose specific implementations
	 * are provided by the derived classes.
	 *
	 * @param query Representation of query
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(IFrame query) {

		return matchPreProcessed(createNetwork(query));
	}

	/**
	 * Converts the specified instance-level query and instance frames
	 * to the network-based versions, runs any registered
	 * pre-processors over them, then performs a single query-matching
	 * test via the {@link #matches(NNode, NNode)} method, whose
	 * specific implementations are provided by the derived classes.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return matchesPreProcessed(createNetwork(query), createNetwork(instance));
	}

	/**
	 * Adds an instance to the matcher. It can be assumed that the
	 * instance is of an appropriate type (see {@link #handlesType}),
	 * and that an instance with the specified identity is not already
	 * present.
	 *
	 * @param instance Representation of instance to be added
	 * @param identity Unique identity for instance
	 */
	public abstract void addPreProcessed(NNode instance, CIdentity identity);

	/**
	 * Finds all instances that are matched by the supplied query.
	 * It can be assumed that the query is of an appropriate type (see
	 * {@link #handlesType}).
	 *
	 * @param query Representation of query
	 * @return Results of query execution
	 */
	public abstract IMatches matchPreProcessed(NNode query);

	/**
	 * Tests whether the supplied instance is matched by the supplied
	 * query. It can be assumed that both instance and query are of an
	 * appropriate type (see {@link #handlesType}).
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	public abstract boolean matchesPreProcessed(NNode query, NNode instance);

	private NNode createNetwork(IFrame rootFrame) {

		return networkManager.createNetwork(rootFrame);
	}

	private void preProcessNetwork(NNode rootNode) {

		networkManager.preProcessNetwork(rootNode);
	}
}
