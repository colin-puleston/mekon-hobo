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
 * Provides customised query matching for a particular value-type,
 * via a specific {@link ICustomValueMatcher}, together with a
 * particular set of applicable slot-types.
 *
 * @author Colin Puleston
 */
public class IValueMatchCustomiser {

	private ICustomValueMatcher matcher;
	private Set<CIdentity> slotIds = new HashSet<CIdentity>();

	/**
	 * Constructor.
	 *
	 * @param matcher Specific matcher-implementation for relevant
	 * value-type
	 * @param slotId Single slot-type to which the matcher is applicable
	 */
	public IValueMatchCustomiser(ICustomValueMatcher matcher, CIdentity slotId) {

		this.matcher = matcher;

		slotIds.add(slotId);
	}

	/**
	 * Constructor.
	 *
	 * @param matcher Specific matcher-implementation for relevant
	 * value-type
	 * @param slotIds Set of slot-types to which the matcher is applicable
	 */
	public IValueMatchCustomiser(
				ICustomValueMatcher matcher,
				Collection<CIdentity> slotIds) {

		this.matcher = matcher;

		this.slotIds.addAll(slotIds);
	}

	/**
	 * Provides the matcher that will provide the customised query
	 * matching
	 *
	 * @return matcher providing customised query matching
	 */
	public ICustomValueMatcher getMatcher() {

		return matcher;
	}

	/**
	 * Provides the set of slot-types to which the matcher is
	 * applicable
	 *
	 * @return relevant set of slot-types
	 */
	public Set<CIdentity> getSlotIds() {

		return new HashSet<CIdentity>(slotIds);
	}
}
