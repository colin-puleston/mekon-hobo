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

package uk.ac.manchester.cs.mekon.model.serial;

import java.util.*;

/**
 * Represents the path from the root-frame of a
 * {@link IFrame}/{@link ISlot} network, representing a model
 * instance, to specific slot or slot-value within the network.
 *
 * @author Colin Puleston
 */
public class IPath {

	private List<String> path;
	private boolean slotPath;

	/**
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		return other instanceof IPath && path.equals(((IPath)other).path);
	}

	/**
	 */
	public int hashCode() {

		return path.hashCode();
	}

	/**
	 */
	public String toString() {

		return getPathTypePrefix() + ": " + path.toString();
	}

	/**
	 * Provides a string-based representation of the path,
	 * consisting of the labels of any frames or slots in the
	 * path, and string representations of any other types of
	 * value.
	 *
	 * @return String-based representation of path
	 */
	public List<String> getPath() {

		return new ArrayList<String>(path);
	}

	/**
	 * Specifies whether the path is a slot-path rather than a
	 * value-path.
	 *
	 * @return True if slot-path
	 */
	public boolean slotPath() {

		return slotPath;
	}

	/**
	 * Specifies whether the path is a value-path rather than a
	 * slot-path.
	 *
	 * @return True if value-path
	 */
	public boolean valuePath() {

		return !slotPath;
	}

	IPath(List<String> path, boolean slotPath) {

		this.path = path;
		this.slotPath = slotPath;
	}

	private String getPathTypePrefix() {

		return slotPath ?  "SLOT" : "VALUE";
	}
}