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

package uk.ac.manchester.cs.mekon.network;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a numeric feature in the network-based instance
 * representation
 *
 * @author Colin Puleston
 */
public class NNumeric extends NFeature<INumber> {

	/**
	 * Constructor.
	 *
	 * @param type Type for numeric feature
	 */
	public NNumeric(CIdentity type) {

		super(type, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean link() {

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public NNumeric asNumeric() {

		return this;
	}

	/**
	 * Provides current number-value for the feature.
	 *
	 * @return Current number-value
	 * @throws KAccessException if no current value
	 */
	public INumber getValue() {

		if (hasValues()) {

			return getValues().iterator().next();
		}

		throw new KAccessException("No current value for numeric: " + this);
	}

	NNumeric(CIdentity type, ISlot iSlot) {

		super(type, iSlot);
	}
}
