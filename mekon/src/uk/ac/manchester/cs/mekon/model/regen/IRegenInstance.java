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

package uk.ac.manchester.cs.mekon.model.regen;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.regen.zlink.*;

/**
 * Represents a specific {@link IFrame}/{@link ISlot} network that
 * has been regenerated from a serialized form, and hence may
 * be partially or fully invalid with repect to the current model.
 *
 * @author Colin Puleston
 */
public class IRegenInstance {

	static {

		ZIRegenAccessor.set(new ZIRegenAccessorImpl());
	}

	private CIdentity rootTypeId;
	private IFrame rootFrame;

	private IRegenStatus status;
	private List<IRegenPath> prunedPaths = new ArrayList<IRegenPath>();

	/**
	 * Provides root-frame type identity, which may or may not represent
	 * a currently valid {@link CFrame} (see {@link #getStatus}).
	 *
	 * @return Root-frame type identity
	 */
	public CIdentity getRootTypeId() {

		return rootTypeId;
	}

	/**
	 * Provides root-frame of fully or partially valid network.
	 *
	 * @return Root-frame of network, or null if root-frame type no longer
	 * valid
	 */
	public IFrame getRootFrame() {

		return rootFrame;
	}

	/**
	 * Provides the status of the instance with repect to the current model.
	 *
	 * @return Status with repect to current model
	 */
	public IRegenStatus getStatus() {

		return status;
	}

	/**
	 * Provides list of any slots or slot-values that have been pruned from
	 * the network as a result of updates to the model since the instance was
	 * serialised.
	 *
	 * @return List of any pruned slots and slot-values
	 */
	public List<IRegenPath> getAllPrunedPaths() {

		return new ArrayList<IRegenPath>(prunedPaths);
	}

	/**
	 * Provides list of any slots that have been pruned from the network as
	 * a result of updates to the model since the instance was serialised.
	 *
	 * @return List of any pruned slots
	 */
	public List<IRegenPath> getPrunedSlotPaths() {

		return selectPrunedPaths(true);
	}

	/**
	 * Provides list of any slot-values that have been pruned from the
	 * network as a result of updates to the model since the instance was
	 * serialised.
	 *
	 * @return List of any pruned slot-values
	 */
	public List<IRegenPath> getPrunedValuePaths() {

		return selectPrunedPaths(false);
	}

	IRegenInstance(CIdentity rootTypeId, IFrame rootFrame, List<IRegenPath> prunedPaths) {

		this.rootTypeId = rootTypeId;
		this.rootFrame = rootFrame;
		this.prunedPaths = prunedPaths;

		status = determineStatus();
	}

	private IRegenStatus determineStatus() {

		if (rootFrame == null) {

			return IRegenStatus.FULLY_INVALID;
		}

		if (prunedPaths.isEmpty()) {

			return IRegenStatus.FULLY_VALID;
		}

		return IRegenStatus.PARTIALLY_VALID;
	}

	private List<IRegenPath> selectPrunedPaths(boolean slotPaths) {

		List<IRegenPath> selectedPaths = new ArrayList<IRegenPath>();

		for (IRegenPath path : prunedPaths) {

			if (path.slotPath() == slotPaths) {

				selectedPaths.add(path);
			}
		}

		return selectedPaths;
	}
}