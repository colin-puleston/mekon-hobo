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

import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class IStructureSubsumptionTester extends IStructureMatchTester {

	boolean match(IFrame frame1, IFrame frame2) {

		List<IFrame> disjuncts1 = frame1.asDisjuncts();

		for (IFrame disjunct2 : frame2.asDisjuncts()) {

			if (!anyMatched(disjuncts1, disjunct2)) {

				return false;
			}
		}

		return true;
	}

	boolean localMatch(IFrame frame1, IFrame frame2) {

		return frame1.subsumesLocalStructure(frame2);
	}

	boolean typesMatch(CFrame type1, CFrame type2) {

		return type1.subsumes(type2);
	}

	boolean numbersMatch(INumber number1, INumber number2) {

		return number1.getType().subsumes(number2.getType());
	}

	boolean valueSlotsSizeMatch(List<ISlot> slots1, List<ISlot> slots2) {

		return slots1.size() <= slots2.size();
	}

	boolean valuesMatch(List<IValue> values1, List<IValue> values2) {

		for (IValue value1 : values1) {

			if (!valueMatched(value1, values2)) {

				return false;
			}
		}

		return true;
	}

	private boolean anyMatched(List<IFrame> frames1, IFrame frame2) {

		for (IFrame frame1 : frames1) {

			if (super.match(frame1, frame2)) {

				return true;
			}
		}

		return false;
	}

	private boolean valueMatched(IValue value1, List<IValue> values2) {

		for (IValue value2 : values2) {

			if (valuesMatch(value1, value2)) {

				return true;
			}
		}

		return false;
	}
}
