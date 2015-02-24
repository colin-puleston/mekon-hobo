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
class IFrameCopier {

	private Map<IFrame, IFrame> copies = new HashMap<IFrame, IFrame>();
	private CopyValuesSupplier copyValuesSupplier = new CopyValuesSupplier();

	private class CopyValuesSupplier extends IValueVisitor {

		private IValue copyValue = null;

		protected void visit(IFrame value) {

			copyValue = getCopy(value);
		}

		protected void visit(INumber value) {

			copyValue = value;
		}

		protected void visit(CFrame value) {

			copyValue = value;
		}

		IValue getCopyValue(IValue template) {

			visit(template);

			return copyValue;
		}
	}

	IFrame getCopy(IFrame template) {

		IFrame copy = copies.get(template);

		if (copy == null) {

			copy = template.instantiateCopy();

			copies.put(template, copy);
			copySlots(template, copy);
		}

		return copy;
	}

	private void copySlots(IFrame template, IFrame copy) {

		ISlots copySlots = copy.getSlots();

		for (ISlot templateSlot : template.getSlots().asList()) {

			ISlot copySlot = resolveCopySlot(template, templateSlot, copySlots);

			copySlotValues(templateSlot, copySlot);
		}
	}

	private void copySlotValues(ISlot templateSlot, ISlot copySlot) {

		ISlotValues copyValues = copySlot.getValues();

		for (IValue value : templateSlot.getValues().asList()) {

			copyValues.add(copyValuesSupplier.getCopyValue(value), true);
		}
	}

	private ISlot resolveCopySlot(
						IFrame template,
						ISlot templateSlot,
						ISlots copySlots) {

		CSlot type = templateSlot.getType();
		ISlot slot = copySlots.getOrNull(type.getIdentity());

		if (slot == null) {

			slot = new ISlot(type, template);

			copySlots.add(slot);
		}

		return slot;
	}
}
