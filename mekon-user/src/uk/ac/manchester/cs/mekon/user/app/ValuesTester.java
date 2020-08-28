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

package uk.ac.manchester.cs.mekon.user.app;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class ValuesTester {

	static private abstract class NestedSlotTester {

		boolean testFrom(IValue value) {

			return value instanceof IFrame && testFrom((IFrame)value);
		}

		boolean testFrom(IFrame frame) {

			for (ISlot slot : frame.getSlots().activesAsList()) {

				if (testFrom(slot)) {

					return true;
				}
			}

			return false;
		}

		abstract boolean testFrom(ISlot slot);

		boolean testFromValues(ISlot slot, boolean datatypeValuesResult) {

			if (slot.getValueType() instanceof CFrame) {

				return testFromValueFrames(slot);
			}

			return datatypeValuesResult;
		}

		private boolean testFromValueFrames(ISlot slot) {

			for (IValue value : slot.getValues().asList()) {

				if (testFrom((IFrame)value)) {

					return true;
				}
			}

			return false;
		}
	}

	static private class UserEditabilityTester extends NestedSlotTester {

		boolean testFrom(ISlot slot) {

			if (slot.getEditability().editable()) {

				return true;
			}

			return testFromValues(slot, false);
		}
	}

	static private class UserValuePresenceTester extends UserEditabilityTester {

		boolean testFrom(ISlot slot) {

			if (slot.getValues().isEmpty()) {

				return false;
			}

			return super.testFrom(slot);
		}
	}

	static private class TerminalValuePresenceTester extends NestedSlotTester {

		boolean testFrom(ISlot slot) {

			if (slot.getValues().isEmpty()) {

				return false;
			}

			return testFromValues(slot, true);
		}

		boolean testFrom(IFrame frame) {

			return terminalFrameValue(frame) || super.testFrom(frame);
		}
	}

	static boolean anyNestedUserEditability(IValue value) {

		return new UserEditabilityTester().testFrom(value);
	}

	static boolean anyEffectiveNestedValues(IValue value) {

		return anyNestedUserValues(value) || anyNestedTerminalValues(value);
	}

	static boolean anyNestedUserValues(IValue value) {

		return new UserValuePresenceTester().testFrom(value);
	}

	static boolean anyNestedTerminalValues(IValue value) {

		return new TerminalValuePresenceTester().testFrom(value);
	}

	static boolean terminalValue(IValue value) {

		if (value instanceof IFrame) {

			return terminalFrameValue((IFrame)value);
		}

		return true;
	}

	static private boolean terminalFrameValue(IFrame value) {

		return value.getSlots().activesAsList().isEmpty();
	}
}
