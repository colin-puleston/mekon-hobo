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
	private ValueCopiesSupplier valueCopies = new ValueCopiesSupplier();

	private class ValueCopiesSupplier extends IValueVisitor {

		private IValue copyValue = null;

		protected void visit(IFrame value) {

			copyValue = getFrameCopy(value);
		}

		protected void visit(INumber value) {

			copyValue = value;
		}

		protected void visit(CFrame value) {

			copyValue = value;
		}

		IValue getCopy(IValue template) {

			visit(template);

			return copyValue;
		}
	}

	IFrame copy(IFrame template) {

		CFrame type = template.getType();
		IFrame copy = new IFrame(type, template.getCategory());

		copies.put(template, copy);

		initialiseCopy(template, copy);
		type.pollListenersForInstantiated(copy);

		return copy;
	}

	private void copyInferredTypes(IFrame template, IFrame copy) {

		template.updateInferredTypes(copy.getInferredTypes().asList());
	}

	private void copySlots(IFrame template, IFrame copy) {

		for (ISlot templateSlot : template.getSlots().asList()) {

			ISlot copySlot = createEmptySlotCopy(copy, templateSlot);

			copySlotValues(templateSlot, copySlot);
		}
	}

	private void copySlotValues(ISlot templateSlot, ISlot copySlot) {

		ISlotValues copyValues = copySlot.getValues();

		for (IValue value : templateSlot.getValues().asList()) {

			copyValues.add(valueCopies.getCopy(value), true);
		}
	}

	private void initialiseCopy(IFrame template, IFrame copy) {

		copy.setAutoUpdateEnabled(false);

		copyInferredTypes(template, copy);
		copySlots(template, copy);

		copy.setAutoUpdateEnabled(true);
	}

	private ISlot createEmptySlotCopy(IFrame container, ISlot template) {

		return container.addSlot(template.getType());
	}

	private IFrame getFrameCopy(IFrame template) {

		IFrame copy = copies.get(template);

		return copy != null ? copy : copy(template);
	}
}
