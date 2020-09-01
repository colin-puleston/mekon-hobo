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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class IRegenPathImpl implements IRegenPath {

	private ISlot slot;
	private IValue value;

	private List<String> path;

	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		return other instanceof IRegenPathImpl && equalsRegenPath((IRegenPathImpl)other);
	}

	public int hashCode() {

		return path.hashCode();
	}

	public String toString() {

		return getPathTypePrefix() + ": \"" + pathToString() + "\"";
	}

	public String pathToString() {

		StringBuilder s = new StringBuilder();
		boolean first = true;

		for (String node : path) {

			if (first) {

				first = false;
			}
			else {

				s.append("/");
			}

			s.append(node);
		}

		return s.toString();
	}

	public List<String> getPath() {

		return new ArrayList<String>(path);
	}

	public boolean slotPath() {

		return slot != null;
	}

	public boolean valuePath() {

		return value != null;
	}

	public ISlot getSlot() {

		return slot;
	}

	public IValue getValue() {

		return value;
	}

	IRegenPathImpl(ISlot slot, IValue value, List<String> path) {

		this.slot = slot;
		this.value = value;
		this.path = path;
	}

	private boolean equalsRegenPath(IRegenPathImpl regenPath) {

		return path.equals(regenPath.path);
	}

	private String getPathTypePrefix() {

		return value != null ?  "VALUE" : "SLOT";
	}
}