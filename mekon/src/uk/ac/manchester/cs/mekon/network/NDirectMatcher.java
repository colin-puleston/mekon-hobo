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
import uk.ac.manchester.cs.mekon.store.motor.*;

/**
 * Provides an implementation of the reasoning mechanisms defined
 * by {@link IMatcher} in which the matching is done directly on
 * the intermediate node/link network-based representations of
 * queries and instances. The matching acts recursively through the
 * networks, taking into account subsumption relationships between
 * the {@link CFrame} representations of the node-types, where
 * available.
 *
 * @author Colin Puleston
 */
public class NDirectMatcher extends NMatcher {

	private Core core = new Core();

	private class Core extends ISimpleMatcherCore<NNode> {

		protected CFrame getTypeOrNull(NNode instance) {

			return instance.getCFrame();
		}

		protected boolean subsumesStructure(NNode query, NNode instance) {

			return query.subsumesStructure(instance);
		}
	}

	/**
	 * Returns true indicating that the matcher handles any type of
	 * instance-level frame. This method should be overriden if
	 * more specific behaviour is required.
	 *
	 * @param type Relevant frame-type
	 * @return True indicating that matcher handles specified type
	 */
	public boolean handlesType(CFrame type) {

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(NNode instance, CIdentity identity) {

		core.add(instance, identity);
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(CIdentity identity) {

		core.remove(identity);
	}

	/**
	 * {@inheritDoc}
	 */
	public IMatches match(NNode query) {

		return core.match(query);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean matches(NNode query, NNode instance) {

		return core.matches(query, instance);
	}

	/**
	 * Does nothing since no clear-ups are required for this type
	 * of store.
	 */
	public void stop() {
	}
}
