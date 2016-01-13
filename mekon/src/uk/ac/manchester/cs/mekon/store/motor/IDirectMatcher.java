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

package uk.ac.manchester.cs.mekon.store.motor;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * Provides an implementation of the reasoning mechanisms defined
 * by {@link IMatcher} in which the matching is done directly on
 * the frame/slot network representations of queries and instances.
 * The matching acts recursively through the networks, taking into
 * account subsumption relationships between the frame-types.
 *
 * @author Colin Puleston
 */
public class IDirectMatcher implements IMatcher {

	private Core core = new Core();

	private class Core extends ISimpleMatcherCore<IFrame> {

		protected CFrame getTypeOrNull(IFrame instance) {

			return instance.getType();
		}

		protected boolean subsumesStructure(IFrame query, IFrame instance) {

			return query.subsumesStructure(instance);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialise(IMatcherIndexes indexes) {
	}

	/**
	 * Always returns true since a rebuild is always required on
	 * startup.
	 *
	 * @return true is rebuild required
	 */
	public boolean rebuildOnStartup() {

		return true;
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
	public void add(IFrame instance, CIdentity identity) {

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
	public IMatches match(IFrame query) {

		return core.match(query);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return core.matches(query, instance);
	}

	/**
	 * Does nothing since no clear-ups are required for this type
	 * of store.
	 */
	public void stop() {
	}
}
