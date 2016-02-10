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

package uk.ac.manchester.cs.mekon.owl.build;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Specifies the types of constructs from which slots are to
 * be derived.
 *
 * @author Colin Puleston
 */
public enum OBSlotSources {

	/**
	 * No slot-source construct types have been defined.
	 */
	UNSPECIFIED,

	/**
	 * Slots are to be derived from property-restrictions only.
	 */
	RESTRICTIONS_ONLY,

	/**
	 * Slots are to be derived from matching pairs of domain and
	 * range constraints only.
	 */
	DOMAIN_RANGE_PAIRS_ONLY,

	/**
	 * Slots are to be derived from both property-restrictions,
	 * and matching pairs of domain and range constraints.
	 */
	ALL;

	boolean includesRestrictions() {

		return this == ALL || this == RESTRICTIONS_ONLY;
	}

	boolean includesDomainRangePairs() {

		return this == ALL || this == DOMAIN_RANGE_PAIRS_ONLY;
	}
}
