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

package uk.ac.manchester.cs.mekon.model.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;

/**
 * Responsible for regenerating previously existing instance-level
 * frame/slot networks, without performing the normal reasoning
 * operations that would be required if the networks were being built
 * up from scratch. Specifically suitable for regenerating networks
 * that have been serialised in some way. Enables the generation of
 * either normal instances or "free-instances" (see {@link IFreeCopier}).
 *
 * @author Colin Puleston
 */
public abstract class IRelaxedInstantiator {

	/**
	 * Provides a relaxed-instantiator object applicable to any model.
	 *
	 * @return Relaxed-instantiator object for any model
	 */
	static public IRelaxedInstantiator get() {

		return ZCModelAccessor.get().getRelaxedInstantiator();
	}

	/**
	 * Creates a frame that will be part of a regenerated network.
	 *
	 * @param type Type of frame to create
	 * @param function Function of frame to create
	 * @param freeInstance True if generating a free-instance
	 * @return Created frame
	 */
	public abstract IFrame startInstantiation(
								CFrame type,
								IFrameFunction function,
								boolean freeInstance);

	/**
	 * Creates a {@link CFrame} object that is not part of the model,
	 * specifically for use during the parsing of a serialised
	 * instance that is non-longer valid with respect to the current
	 * model.
	 *
	 * @param identity Identity of created non-model frame
	 */
	public abstract CFrame getNonModelFrameType(CIdentity identity);

	/**
	 * Adds a slot to a regenerated network.
	 *
	 * @param container Frame to which slot is to be added
	 * @param slotId Identity of slot to be created
	 * @param valueType Value-type for slot to be created
	 * @param cardinality Cardinality of slot to be created
	 * @param activation Activation of slot to be created
	 * @param editability Editability of slot to be created
	 * @return Created and added slot
	 */
	public abstract ISlot addSlot(
							IFrame container,
							CIdentity slotId,
							CValue<?> valueType,
							CCardinality cardinality,
							CActivation activation,
							IEditability editability);

	/**
	 * Creates a free-instance-style slot and adds it to the specified
	 * frame. The value-type for the slot will be  derived by invoking the
	 * {@link CValue#toUnconstrained} method on a "source" value-type object.
	 *
	 * @param container Frame to which slot is to be added
	 * @param slotId Identity of slot to be created
	 * @param valueTypeSource Source value-type for slot to be created
	 * @return Created and added slot
	 */
	public abstract ISlot addFreeSlot(
							IFrame container,
							CIdentity slotId,
							CValue<?> valueTypeSource);

	/**
	 * Performs the required instantiation-completion operations for a
	 * frame in a regenerated network, after all slots have been added.
	 *
	 * @param frame Relevant frame
	 * @param possibleModelUpdates True if re-instantiating serialized
	 * network where model may have been updated since serialization
	 * @return Any update operations performed in order to align
	 * regenerated network with updated model, if applicable
	 */
	public abstract Set<IUpdateOp> completeReinstantiation(
										IFrame frame,
										boolean possibleModelUpdates);
}
