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
import uk.ac.manchester.cs.mekon.store.*;

/**
 * Provides configuation information that may be required by
 * implementations of {@link IMatcher}
 *
 * @author Colin Puleston
 */
public class IMatcherConfig {

	private IStore store;
	private List<IValueMatchCustomiser> valueMatchCustomisers;
	private IMatcherIndexes indexes;

	/**
	 * Provides the Instance store to which the matcher is attached.
	 *
	 * @return Relevant instance store
	 */
	public IStore getStore() {

		return store;
	}

	/**
	 * Provides the set of mappings between unique instance identities
 	 * and corresponding unique index values maintained by the store
 	 * mechanisms.
	 *
	 * @return Relevant set of instance identity/index mappings
	 */
	public IMatcherIndexes getIndexes() {

		return indexes;
	}

	/**
	 * Provides the set of value-type/slot-type specfic query-match
	 * customisers that need to be handled by any matchers that
	 * implement value-specific custom query matching.
	 *
	 * @return Relevant set of query-match customisers
	 */
	public List<IValueMatchCustomiser> getValueMatchCustomisers() {

		return valueMatchCustomisers;
	}

	IMatcherConfig(
		IStore store,
		IMatcherIndexes indexes,
		List<IValueMatchCustomiser> valueMatchCustomisers) {

		this.store = store;
		this.valueMatchCustomisers = valueMatchCustomisers;
		this.indexes = indexes;
	}
}
