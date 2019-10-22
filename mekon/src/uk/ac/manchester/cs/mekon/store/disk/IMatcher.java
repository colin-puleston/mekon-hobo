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

package uk.ac.manchester.cs.mekon.store.disk;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * Provides mechanisms for storing and executing queries over
 * representations of sets of instantiations of a MEKON Frames
 * Model (FM).
 * <p>
 * All of the instance-level frame/slot-network representations of
 * instances and queries that are passed into the matcher methods
 * will be "free-instance" copies of the originals (see {@link
 * IFreeCopier}).
 *
 * @author Colin Puleston
 */
public interface IMatcher {

	/**
	 * Initialisation method that will be called at some point
	 * prior to the first access of any of the other defined methods.
	 *
	 * @param store Instance store to which matcher is attached
	 * @param indexes Mappings between unique instance identities
 	 * and corresponding unique index values
	 */
	public void initialise(IStore store, IMatcherIndexes indexes);

	/**
	 * Specifies whether the representations held by the matcher of
	 * any persistently stored instances needs to be rebuilt on
	 * start-up.
	 *
	 * @return true as rebuild required
	 */
	public boolean rebuildOnStartup();

	/**
	 * Checks whether the matcher handles instance-level frames
	 * of the specified type.
	 *
	 * @param type Relevant frame-type
	 * @return True if matcher handles specified type
	 */
	public boolean handlesType(CFrame type);

	/**
	 * Adds the specified instance to the matcher.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(IFrame instance, CIdentity identity);

	/**
	 * Removes the specified instance from the matcher.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity);

	/**
	 * Finds all instances that match the specified query.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(IFrame query);

	/**
	 * Tests whether the specified query is matched by the specified
	 * instance.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	public boolean matches(IFrame query, IFrame instance);

	/**
	 * Performs any necessary clear-ups after all access of the store
	 * has terminated.
	 */
	public void stop();
}
