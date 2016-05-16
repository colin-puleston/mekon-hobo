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

package uk.ac.manchester.cs.mekon.store;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents an instance-store associated with a MEKON Frames
 * Model (FM). Both instances and queries are represented via
 * instance-level frames. The frames representing instances must
 * have function {@link IFrameFunction#ASSERTION} rather than
 * {@link IFrameFunction#QUERY}, whereas those representing queries
 * can be either, since assertion frames can also be interpreted as
 * queries.
 *
 * @author Colin Puleston
 */
public interface IStore {

	/**
	 * Provides the model with which the store is associated.
	 *
	 * @return Model with which store is associated
	 */
	public CModel getModel();

	/**
	 * Adds an instance to the store, possibly replacing an existing
	 * instance with the same identity.
	 *
	 * @param instance Representation of instance to be stored
	 * @param identity Unique identity for instance
	 * @return Existing instance that was replaced, or null if not
	 * applicable
	 * @throws KAccessException if instance frame does not have function
	 * {@link IFrameFunction#ASSERTION}
	 */
	public IFrame add(IFrame instance, CIdentity identity);

	/**
	 * Removes an instance from the store.
	 *
	 * @param identity Unique identity of instance
	 * @return True if instance removed, false if instance with
	 * specified identity not present
	 */
	public boolean remove(CIdentity identity);

	/**
	 * Removes all instances from the store.
	 */
	public void clear();

	/**
	 * Checks whether store contains a particular instance.
	 *
	 * @param identity Unique identity of instance to check for
	 * @return True if store contains required instance
	 */
	public boolean contains(CIdentity identity);

	/**
	 * Retrieves an instance from the store.
	 *
	 * @param identity Unique identity of instance
	 * @return Instance-level frame representing required instance,
	 * or null if instance with specified identity not present
	 */
	public IFrame get(CIdentity identity);

	/**
	 * Provides unique identities of all instances in store,
	 * ordered by the time/date they were added.
	 *
	 * @return Unique identities of all instances, oldest entries
	 * first
	 */
	public List<CIdentity> getAllIdentities();

	/**
	 * Finds all instances that are matched by the supplied query.
	 *
	 * @param query Representation of query
	 * @return Results of query execution
	 */
	public IMatches match(IFrame query);

	/**
	 * Tests whether the supplied instance is matched by the supplied
	 * query.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	public boolean matches(IFrame query, IFrame instance);
}
