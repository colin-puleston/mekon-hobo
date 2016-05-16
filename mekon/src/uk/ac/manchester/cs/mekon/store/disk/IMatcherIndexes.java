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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides mapping for each unique instance identity to a
 * corresponding unique index value. Each mapping will persist
 * between subsequent invocations of the system, for as long as the
 * relevant instance exists.
 *
 * @author Colin Puleston
 */
public interface IMatcherIndexes {

	/**
	 * Checks whether an identity has an assigned index.
	 *
	 * @param identity Identity to check
	 * @return True if identity has an assigned index
	 */
	public boolean hasIndex(CIdentity identity);

	/**
	 * Retrieves the index corresponding to the specified identity.
	 *
	 * @param identity Identity for which index is required
	 * @return Relevant index
	 */
	public int getIndex(CIdentity identity);

	/**
	 * Retrieves the identity corresponding to the specified index.
	 *
	 * @param index Index for which identity is required
	 * @return Relevant identity
	 */
	public CIdentity getIdentity(int index);

	/**
	 * Retrieves the identities corresponding to the specified set
	 * of indexes.
	 *
	 * @param indexes Indexes for which identities are required
	 * @return Relevant set of identities
	 */
	public List<CIdentity> getIdentities(List<Integer> indexes);
}
