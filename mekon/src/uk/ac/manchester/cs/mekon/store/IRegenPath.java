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
 * Represents the path from the root-frame of a regenerated
 * {@link IFrame}/{@link ISlot} network to a specific slot or
 * slot-value within the network.
 *
 * @author Colin Puleston
 */
public interface IRegenPath {

	/**
	 * Provides a string-based representation of the path.
	 *
	 * @return String-based representation of path
	 */
	public String pathToString();

	/**
	 * Provides a representation of the path as an ordered list of
	 * string-based representations of the components.
	 *
	 * @return Ordered list of string-based representations of path
	 * components
	 */
	public List<String> getPath();

	/**
	 * Specifies whether the path is a slot-path as opposed to a
	 * value-path.
	 *
	 * @return True if slot-path
	 */
	public boolean slotPath();

	/**
	 * Specifies whether the path is a value-path as opposed to a
	 * slot-path.
	 *
	 * @return True if value-path
	 */
	public boolean valuePath();

	/**
	 * Specifies the slot for a slot-path, or the slot to which the
	 * value is attached for a value-path.
	 *
	 * @return Relevant slot
	 */
	public ISlot getSlot();

	/**
	 * Specifies the value for a value-path.
	 *
	 * @return Relevant value, or null if not a value-path
	 */
	public IValue getValue();
}