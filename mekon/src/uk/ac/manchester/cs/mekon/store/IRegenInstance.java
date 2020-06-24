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
 * Represents a specific {@link IFrame}/{@link ISlot} network that
 * has been regenerated from a serialized form, and hence may
 * be partially or fully invalid with respect to the current model.
 *
 * @author Colin Puleston
 */
public interface IRegenInstance {

	/**
	 * Provides root-frame type identity, which may or may not represent
	 * a currently valid {@link CFrame} (see {@link #getStatus}).
	 *
	 * @return Root-frame type identity
	 */
	public CIdentity getRootTypeId();

	/**
	 * Provides root-frame of fully or partially valid network.
	 *
	 * @return Root-frame of network, or null if root-frame type no longer
	 * valid
	 */
	public IFrame getRootFrame();

	/**
	 * Provides the status of the instance with respect to the current model.
	 *
	 * @return Status with respect to current model
	 */
	public IRegenStatus getStatus();

	/**
	 * Provides list of any slots or slot-values that have been pruned from
	 * the network as a result of updates to the model since the instance was
	 * serialised.
	 *
	 * @return List of any pruned slots and slot-values
	 */
	public List<IRegenPath> getAllPrunedPaths();

	/**
	 * Provides list of any slots that have been pruned from the network as
	 * a result of updates to the model since the instance was serialised.
	 *
	 * @return List of any pruned slots
	 */
	public List<IRegenPath> getPrunedSlotPaths();

	/**
	 * Provides list of any slot-values that have been pruned from the
	 * network as a result of updates to the model since the instance was
	 * serialised.
	 *
	 * @return List of any pruned slot-values
	 */
	public List<IRegenPath> getPrunedValuePaths();
}