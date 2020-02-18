/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class AutoValueProvider {

	private Instantiator instantiator;
	private ISlot slot;

	AutoValueProvider(Instantiator instantiator, ISlot slot) {

		this.instantiator = instantiator;
		this.slot = slot;
	}

	boolean canProvide() {

		return getAutoValueOrNull() != null;
	}

	void checkProvide() {

		IValue value = getAutoValueOrNull();

		if (value != null) {

			slot.getValuesEditor().add(value);
		}
	}

	private IValue getAutoValueOrNull() {

		if (editable() && singleValued()) {

			CValue<?> type = slot.getValueType();

			if (type instanceof CFrame) {

				return getAutoIFrameValueOrNull((CFrame)type);
			}

			if (type instanceof MFrame) {

				return getAutoCFrameValueOrNull((MFrame)type);
			}
		}

		return null;
	}

	private IFrame getAutoIFrameValueOrNull(CFrame type) {

		if (autoIFrameValueType(type)) {

			IFrame frame = instantiator.instantiate(type);

			if (frame.getSlots().isEmpty()) {

				return frame;
			}
		}

		return null;
	}

	private CFrame getAutoCFrameValueOrNull(MFrame type) {

		CFrame value = type.getRootCFrame();

		return leafVisibleFrame(value) ? value : null;
	}

	private boolean autoIFrameValueType(CFrame type) {

		return !instantiator.instanceRefType(type) && leafVisibleFrame(type);
	}

	private boolean leafVisibleFrame(CFrame type) {

		return type.getSubs(CVisibility.EXPOSED).isEmpty();
	}

	private boolean editable() {

		return slot.getEditability().editable();
	}

	private boolean singleValued() {

		return slot.getType().getCardinality().singleValue();
	}
}
