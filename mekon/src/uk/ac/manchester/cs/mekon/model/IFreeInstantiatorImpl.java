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

package uk.ac.manchester.cs.mekon.model;

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
class IFreeInstantiatorImpl extends IFreeInstantiator {

	private class OneTimeInstantiator extends IFrameCopierAbstract {

		ISlot addSlot(IFrame container, CSlot slotType) {

			return addFreeSlot(container, slotType.getIdentity(), slotType.getValueType());
		}

		boolean freeInstance() {

			return true;
		}
	}

	public IFrame startInstantiation(CFrame type, IFrameFunction function) {

		return new IFrame(type, function);
	}

	public ISlot addSlot(
					IFrame container,
					CIdentity slotTypeId,
					CValue<?> valueTypeSource) {

		return addFreeSlot(container, slotTypeId, valueTypeSource);
	}

	public void completeInstantiation(IFrame frame) {

		frame.completeInstantiation(true);
	}

	public IFrame createFreeCopy(IFrame sourceInstance) {

		return new OneTimeInstantiator().copy(sourceInstance);
	}

	private ISlot addFreeSlot(IFrame container, CIdentity id, CValue<?> valueTypeSource) {

		return container.addFreeSlot(createFreeSlotType(container, id, valueTypeSource));
	}

	private CSlot createFreeSlotType(
						IFrame container,
						CIdentity id,
						CValue<?> valueTypeSource) {

		CFrame contType = container.getType();
		CValue<?> valueType = valueTypeSource.toUnconstrained();

		CSlot slotType = new CSlot(contType, id, valueType, CCardinality.REPEATABLE_TYPES);

		slotType.setEditability(CEditability.FULL);

		return slotType;
	}
}
