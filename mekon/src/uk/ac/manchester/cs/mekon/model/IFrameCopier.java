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

/**
 * @author Colin Puleston
 */
class IFrameCopier extends IFrameCopierAbstract {

	void initialiseCopy(IFrame template, IFrame copy) {

		copy.setAutoUpdateEnabled(false);

		super.initialiseCopy(template, copy);
		copyInferredTypes(template, copy);

		copy.setAutoUpdateEnabled(true);
	}

	ISlot addSlot(IFrame container, CSlot slotType) {

		return container.addSlotInternal(slotType, false);
	}

	boolean freeInstance() {

		return false;
	}

	private void copyInferredTypes(IFrame template, IFrame copy) {

		template.updateInferredTypes(copy.getInferredTypes().asList());
	}
}
