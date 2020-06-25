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
 * Implementation of {@link IRegenInstance} representing instance
 * network that is fully or partially valid with respect to the
 * current model.
 *
 * @author Colin Puleston
 */
public class IRegenValidInstance implements IRegenInstance {

	private IFrame rootFrame;
	private List<IRegenPath> prunedPaths = new ArrayList<IRegenPath>();

	/**
	 * Constructor.
	 *
	 * @param rootFrame Root-frame of network
	 */
	public IRegenValidInstance(IFrame rootFrame) {

		this.rootFrame = rootFrame;
	}

	/**
	 * Adds a path that has been pruned from the network as a result
	 * of updates to the model since the instance was serialised.
	 *
	 * @param slot Relevant slot for pruned slot-path, or slot to
	 * which value attached for pruned value-path
	 * @param value Relevant value if pruned value-path, or null otherwise
	 * @param path String-based representation of pruned path
	 */
	public void addPrunedPath(ISlot slot, IValue value, List<String> path) {

		prunedPaths.add(new IRegenPathImpl(slot, value, path));
	}

	/**
	 * {@inheritDoc}
	 */
	public CIdentity getRootTypeId() {

		return rootFrame.getType().getIdentity();
	}

	/**
	 * {@inheritDoc}
	 */
	public IFrame getRootFrame() {

		return rootFrame;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRegenStatus getStatus() {

		return prunedPaths.isEmpty()
				? IRegenStatus.FULLY_VALID
				: IRegenStatus.PARTIALLY_VALID;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRegenPath> getAllPrunedPaths() {

		return new ArrayList<IRegenPath>(prunedPaths);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRegenPath> getPrunedSlotPaths() {

		return selectPrunedPaths(true);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRegenPath> getPrunedValuePaths() {

		return selectPrunedPaths(false);
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