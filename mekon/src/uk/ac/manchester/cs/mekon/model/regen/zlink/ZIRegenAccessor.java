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

package uk.ac.manchester.cs.mekon.model.regen.zlink;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.regen.motor.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * THIS CLASS SHOULD NOT BE ACCESSED DIRECTLY BY EITHER THE CLIENT
 * OR THE PLUGIN CODE.
 * <p>
 * Provides the MEKON mechanisms with privileged access to
 * the MEKON regenerated-instance representation.
 *
 * @author Colin Puleston
 */
public abstract class ZIRegenAccessor {

	static private KSingleton<ZIRegenAccessor> singleton
							= new KSingleton<ZIRegenAccessor>();

	/**
	 * Sets the singleton accessor object.
	 *
	 * @param accessor Accessor to set as singleton
	 */
	static public synchronized void set(ZIRegenAccessor accessor) {

		singleton.set(accessor);
	}

	/**
	 * Retrieves the singleton accessor object. Ensures that the
	 * {@link IRegenInstance} class is initialised, since it is the
	 * static initialisation method on that class that sets the
	 * singleton accessor, via the {@link #set} method.
	 *
	 * @return Singleton accessor object
	 */
	static public ZIRegenAccessor get() {

		return singleton.get(IRegenInstance.class);
	}

	/**
	 * Creates a regenerated-instance type representation.
	 *
	 * @param rootTypeId Specification of type of root-frame of network
	 * @param rootType Type of root-frame, or null if root-frame type no
	 * longer valid
	 * @return Created regenerated-type representation
	 */
	public abstract IRegenType createRegenType(CIdentity rootTypeId, CFrame rootType);

	/**
	 * Creates a regenerated-instance representation.
	 *
	 * @param rootTypeId Specification of type of root-frame of network
	 * @param rootFrame Root-frame of network, or null if root-frame
	 * type no longer valid
	 * @param prunedPaths List of any pruned slots and slot-values
	 * @return Created regenerated-instance representation
	 */
	public abstract IRegenInstance createRegenInstance(
										CIdentity rootTypeId,
										IFrame rootFrame,
										List<IRegenPath> prunedPaths);

	/**
	 * Creates a regenerated-instance path representation.
	 *
	 * @param slot Relevant slot if slot-path, or slot to which value
	 * attached if value-path
	 * @param value Relevant value if value-path, or null otherwise
	 * @param path String-based representation of path
	 * @return Created path representation
	 */
	public abstract IRegenPath createRegenPath(ISlot slot, IValue value, List<String> path);
}
