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

package uk.ac.manchester.cs.mekon.model.regen.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.regen.zlink.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Builder for {@link IRegenInstance} object.
 */
public class IRegenInstanceBuilder {

	static private final ZIRegenAccessor regenAccessor = ZIRegenAccessor.get();

	private List<IRegenPath> prunedPaths = new ArrayList<IRegenPath>();

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

		prunedPaths.add(regenAccessor.createRegenPath(slot, value, path));
	}

	/**
	 * Creates a regenerated-instance representation.
	 *
	 * @param rootFrame Root-frame of network
	 * @return Created regenerated-instance representation
	 */
	public IRegenInstance createValid(IFrame rootFrame) {

		return create(rootFrame.getType().getIdentity(), rootFrame);
	}

	/**
	 * Creates a regenerated-instance representation.
	 *
	 * @param rootTypeId Specification of no-longer valid type of
	 * root-frame of network
	 * @return Created regenerated-instance representation
	 */
	public IRegenInstance createInvalid(CIdentity rootTypeId) {

		return create(rootTypeId, null);
	}

	private IRegenInstance create(CIdentity rootTypeId, IFrame rootFrame) {

		return regenAccessor.createRegenInstance(rootTypeId, rootFrame, prunedPaths);
	}
}