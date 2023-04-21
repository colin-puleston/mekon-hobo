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

import java.util.*;

/**
 * @author Colin Puleston
 */
abstract class IFrameCopierAbstract {

	private IFrameFunction copyFunction;
	private Map<IFrame, IFrame> copies = new HashMap<IFrame, IFrame>();

	IFrameCopierAbstract(IFrameFunction copyFunction) {

		this.copyFunction = copyFunction;
	}

	IFrame copy(IFrame template) {

		IFrame copy = template.copyEmpty(copyFunction, freeInstance());

		copies.put(template, copy);

		initialiseCopy(template, copy);
		copy.completeReinstantiation(false);

		return copy;
	}

	void initialiseCopy(IFrame template, IFrame copy) {

		copySlots(template, copy);
	}

	abstract ISlot addSlot(IFrame container, CSlot slotType);

	abstract boolean freeInstance();

	private void copySlots(IFrame template, IFrame copy) {

		for (ISlot templateSlot : template.getSlots().asList()) {

			CSlot copySlotType = templateSlot.getType().copy();
			ISlot copySlot = addSlot(copy, copySlotType);

			setCopySlotValues(templateSlot, copySlot);
		}
	}

	private void setCopySlotValues(ISlot templateSlot, ISlot copySlot) {

		ISlotValues tempVals = templateSlot.getValues();

		List<IValue> fixeds = getCopyValues(tempVals.getFixedValues());
		List<IValue> asserteds = getCopyValues(tempVals.getAssertedValues());

		copySlot.getValues().reinitialise(fixeds, asserteds);
	}

	private List<IValue> getCopyValues(List<IValue> templateValues) {

		List<IValue> copyValues = new ArrayList<IValue>();

		for (IValue templateValue : templateValues) {

			copyValues.add(getCopyValue(templateValue));
		}

		return copyValues;
	}

	private IValue getCopyValue(IValue value) {

		return value instanceof IFrame ? getFrameCopy((IFrame)value) : value;
	}

	private IFrame getFrameCopy(IFrame template) {

		IFrame copy = copies.get(template);

		return copy != null ? copy : copy(template);
	}
}
