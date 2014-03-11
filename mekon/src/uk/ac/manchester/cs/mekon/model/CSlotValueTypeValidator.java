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

import uk.ac.manchester.cs.mekon.*;

/**
 * @author Colin Puleston
 */
class CSlotValueTypeValidator extends CHierarchyCrawler {

	private CProperty property;
	private CValue<?> valueType;

	private boolean valid = false;

	CSlotValueTypeValidator(CSlot slot) {

		this(slot.getProperty(), slot.getValueType());
	}

	CSlotValueTypeValidator(CProperty property, CValue<?> valueType) {

		this.property = property;
		this.valueType = valueType;
	}

	void checkNotInvalidFor(CFrame container) {

		processLinked(getModelFrame(container));
	}

	void checkValidFor(CFrame container) {

		processAll(getModelFrame(container));

		if (!valid) {

			throw new KModelException(
						"No slot found for property: " + property
						+ " on frame: " + container);
		}
	}

	List<CModelFrame> getDirectlyLinked(CModelFrame current) {

		return current.getModelSupers().getAll();
	}

	CrawlMode process(CModelFrame current) {

		CSlots slots = current.getSlots();

		if (slots.containsSlotFor(property)) {

			checkValidForSlot(slots.getSlotFor(property));

			valid = true;

			return CrawlMode.DONE_BRANCH;
		}

		return CrawlMode.CRAWL;
	}

	private void checkValidForSlot(CSlot slot) {

		if (!slot.getValueType().subsumes(valueType)) {

			throw new KModelException(
						"Invalid value-type for slot: " + slot
						+ ": expected type: " + slot.getValueType()
						+ ": invalid type: " + valueType);
		}
	}

	private CModelFrame getModelFrame(CFrame container) {

		return container.getModelFrame().asModelFrame();
	}
}
