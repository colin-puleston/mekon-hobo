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

package uk.ac.manchester.cs.mekon.network;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.*;

/**
 * Abstract base-class for implementations of {@link IMatcher} that
 * operate on the intermediate network-based representation.
 * <p>
 * The instance-level frames that are passed to the implemented
 * methods, are converted into instantiations of the network-based
 * representation, and then passed to the corresponding abstract
 * methods.
 * <p>
 * The matching process can be customised by adding one or more
 * pre-processors to modify the networks that will be passed to the
 * abstract methods (see {@link #addPreProcessor}).
 *
 * @author Colin Puleston
 */
public abstract class NMatcher implements IMatcher {

	private NetworkCreator networkCreator = new NetworkCreator();

	/**
	 * Registers a pre-processor to perform certain required
	 * modifications to appropriate representations of instances that
	 * are about to be stored, or queries that are about to be matched.
	 *
	 * @param preProcessor Pre-processor for instances and queries
	 */
	public void addPreProcessor(NProcessor preProcessor) {

		networkCreator.addPreProcessor(preProcessor);
	}

	/**
	 * Converts the specified instance-level instance frame to the
	 * network-based representation, runs any registered pre-processors
	 * over the resulting network, then invokes {@link
	 * #add(NNode, CIdentity)} to perform the add operation.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(IFrame instance, CIdentity identity) {

		add(toNetwork(instance), identity);
	}

	/**
	 * Converts the specified instance-level query frame to the
	 * network-based representation, runs any registered pre-processors
	 * over the resulting network, then invokes {@link #match(NNode)}
	 * to perform the matching operation.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(IFrame query) {

		return match(toNetwork(query));
	}

	/**
	 * Converts the specified instance-level query and instance frames
	 * to the network-based representation, runs any registered
	 * pre-processors over the resulting networks, then invokes {@link
	 * #matches(NNode, NNode)} to perform the match-testing operation.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return matches(toNetwork(query), toNetwork(instance));
	}

	/**
	 * Adds the specified instance to the matcher.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	protected abstract void add(NNode instance, CIdentity identity);

	/**
	 * Finds all instances that match the specified query.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	protected abstract IMatches match(NNode query);

	/**
	 * Tests whether the specified query is matched by the specified
	 * instance.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	protected abstract boolean matches(NNode query, NNode instance);

	private NNode toNetwork(IFrame rootFrame) {

		return networkCreator.createNetwork(rootFrame);
	}
}
