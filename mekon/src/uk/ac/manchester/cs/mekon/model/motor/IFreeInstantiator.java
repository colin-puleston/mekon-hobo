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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;

/**
 * Responsible for generating "free" versions of existing
 * instance-level frame/slot networks, or "free-instances". A free
 * instance is one in which the schema has been loosened in the
 * following ways:
 * <ul>
 *   <li>No effective constraints on slot-values, other than general
 *	 value-category (i.e, {@link IFrame}, {@link CFrame}, or
 *	 {@link INumber})
 *   <li>No automatic updates to slot-sets or slot-values due to
 *	 either generic reasoning mechanisms, or custom procedures
 *	 associated with a mapped object model
 * </ul>
 * <p>
 * Such free-instances are intended for use by any plug-in mechanisms
 * that need to manipulate their instances in a way that (a) is
 * outside the general schema imposed by the model, and/or (b) does not
 * incur the additional, and unneccesary overheads of the reasoning
 * mechanisms kicking in as updates are made.
 *
 * @author Colin Puleston
 */
public abstract class IFreeInstantiator {

	/**
	 * Provides a free-instantiator object applicable to any model.
	 *
	 * @return Free-instantiator object for any model
	 */
	static public IFreeInstantiator get() {

		return ZCModelAccessor.get().getFreeInstantiator();
	}

	/**
	 * Creates a frame that will be part of a free-instance.
	 *
	 * @param type Type of frame to create
	 * @param function Function of frame to create
	 * @return Created free-instance frame
	 */
	public abstract IFrame startInstantiation(CFrame type, IFrameFunction function);

	/**
	 * Creates a free-instance-style {@link IFrame}-valued slot and adds
	 * it to the specified frame. The value-type for the slot will be
	 * derived by invoking the {@link CValue#toUnconstrained} method on
	 * a "source" value-type object.
	 *
	 * @param container Frame to which slot is to be added
	 * @param slotTypeId Identity of slot-type for slot to be created
	 * @param valueTypeSource Source value-type for slot to be created
	 * @return Created and added slot
	 */
	public abstract ISlot addSlot(
							IFrame container,
							CIdentity slotTypeId,
							CValue<?> valueTypeSource);

	/**
	 * Performs the required instantiation-completion operations for a
	 * free-instance frame, after all slots have been added.
	 *
	 * @param frame Relevant free-instance frame
	 */
	public abstract void completeInstantiation(IFrame frame);

	/**
	 * Generates a free-instance version of the specified source
	 * instance.
	 *
	 * @param sourceInstance Instance of which free-instance version
	 * is required
	 * @return Generated free-instance
	 */
	public abstract IFrame createFreeCopy(IFrame sourceInstance);
}
