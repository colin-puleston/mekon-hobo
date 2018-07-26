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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

/**
 * Represents the types of update operation that can be performed
 * on instance-level frames as the result of reasoning.
 *
 * @author Colin Puleston
 */
public enum IUpdateOp {

	/**
	 * Updates of inferred-types for relevant frames.
	 */
	INFERRED_TYPES,

	/**
	 * Updates of suggested-types for relevant frames.
	 */
	SUGGESTED_TYPES,

	/**
	 * Updates of slot-sets on relevant frames, and/or value-types
	 * and other attributes for specific slots.
	 */
	SLOTS,

	/**
	 * Updates of fixed values for relevant slots.
	 */
	SLOT_VALUES;

	/**
	 * Provides all value-options in the form of a set.
	 *
	 * @return Value-options as set
	 */
	static public Set<IUpdateOp> valuesAsSet() {

		return new HashSet<IUpdateOp>(Arrays.asList(IUpdateOp.values()));
	}
}

