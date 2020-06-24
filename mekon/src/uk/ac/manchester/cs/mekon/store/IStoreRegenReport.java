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
 * Provides information concerning any {@link IFrame}/{@link ISlot}
 * networks serialized on disk, that are now fully invalid or partially
 * valid with respect to the current model. See {@link IRegenStatus}
 * for definitions of fully invalid and partially valid networks.
 *
 * @author Colin Puleston
 */
public interface IStoreRegenReport {

	/**
	 * Specifies whether there are any instance networks that are
	 * now fully invalid with respect to the current model.
	 *
	 * @return true if fully invalid instance networks exist
	 */
	public boolean fullyInvalidRegens();

	/**
	 * Specifies whether there are any instance networks that are
	 * now partially valid with respect to the current model.
	 *
	 * @return true if partially valid instance networks exist
	 */
	public boolean partiallyValidRegens();

	/**
	 * Provides identities of any instance networks that are now
	 * fully invalid with respect to the current model.
	 *
	 * @return Identities of all fully invalid instances networks
	 */
	public List<CIdentity> getFullyInvalidIds();

	/**
	 * Provides identities of any instance networks that are now
	 * partially valid with respect to the current model.
	 *
	 * @return Identities of all partially valid instances networks
	 */
	public List<CIdentity> getPartiallyValidIds();
}
