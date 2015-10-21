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

package uk.ac.manchester.cs.mekon.mechanism;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides the MEKON mechanisms, and those of any extensions of
 * the MEKON framework, with a means of building "free-instances".
 * <p>
 * NOTE: This class is only intended for use by the MEKON and
 * MEKON-extension mechanisms and should not be accessed directly
 * by the client code.
 *
 * @author Colin Puleston
 */
public interface IFreeInstantiator {

	/**
	 * Creates a frame that will be part of a free-instance.
	 *
	 * @param type Type of frame to create
	 * @param category Category of frame to create
	 * @return Created free-instance frame
	 */
	public IFrame startInstantiation(CFrame type, IFrameCategory category);

	/**
	 * Creates a free-instance-style {@link IFrame}-valued slot and
	 * adds it to the specified frame.
	 *
	 * @param container Frame to which slot is to be added
	 * @param slotTypeId Identity of slot-type for slot to be created
	 * @return Created and added slot
	 */
	public ISlot addIFrameSlot(IFrame container, CIdentity slotTypeId);

	/**
	 * Creates a free-instance-style {@link CFrame}-valued slot and
	 * adds it to the specified frame.
	 *
	 * @param container Frame to which slot is to be added
	 * @param slotTypeId Identity of slot-type for slot to be created
	 * @return Created and added slot
	 */
	public ISlot addCFrameSlot(IFrame container, CIdentity slotTypeId);

	/**
	 * Creates a free-instance-style {@link INumber}-valued slot and
	 * adds it to the specified frame.
	 *
	 * @param container Frame to which slot is to be added
	 * @param slotTypeId Identity of slot-type for slot to be created
	 * @param numberType Type of number that slot-values are to
	 * represent
	 * @return Created and added slot
	 */
	public ISlot addINumberSlot(
					IFrame container,
					CIdentity slotTypeId,
					Class<? extends Number> numberType);

	/**
	 * Performs the required instantiation-completion operations for a
	 * free-instance frame, after all slots have been added.
	 *
	 * @param frame Relevant free-instance frame
	 */
	public void completeInstantiation(IFrame frame);

	/**
	 * Generates a free-instance version of the specified source
	 * instance.
	 *
	 * @param sourceInstance Instance of which free-instance version
	 * is required
	 * @return Generated free-instance
	 */
	public IFrame deriveInstantiation(IFrame sourceInstance);
}
