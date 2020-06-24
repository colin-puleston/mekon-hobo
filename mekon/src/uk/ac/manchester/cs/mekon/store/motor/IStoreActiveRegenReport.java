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
 * Default implementation of {@link IStoreRegenReport}.
 *
 * @author Colin Puleston
 */
public class IStoreActiveRegenReport implements IStoreRegenReport {

	private List<CIdentity> fullyInvalidIds = new ArrayList<CIdentity>();
	private List<CIdentity> partiallyValidIds = new ArrayList<CIdentity>();

	/**
	 * Registers an instance network that is now fully invalid
	 * with respect to the current model.
	 *
	 * @param regenId Identity of fully invalid instance network
	 */
	public void addFullyInvalidRegenId(CIdentity regenId) {

		fullyInvalidIds.add(regenId);
	}

	/**
	 * Registers an instance network that is now partially valid
	 * with respect to the current model.
	 *
	 * @param regenId Identity of partially valid instance network
	 */
	public void addPartiallyValidRegenId(CIdentity regenId) {

		partiallyValidIds.add(regenId);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean fullyInvalidRegens() {

		return !fullyInvalidIds.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean partiallyValidRegens() {

		return !partiallyValidIds.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CIdentity> getFullyInvalidIds() {

		return new ArrayList<CIdentity>(fullyInvalidIds);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CIdentity> getPartiallyValidIds() {

		return new ArrayList<CIdentity>(partiallyValidIds);
	}
}
